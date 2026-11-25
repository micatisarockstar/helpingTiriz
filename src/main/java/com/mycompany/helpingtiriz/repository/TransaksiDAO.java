package com.mycompany.helpingtiriz.repository;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.entity.Transaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    /**
     * Menyimpan transaksi + detailnya + mengurangi stok dalam SATU transaksi database.
     * Bila salah satu langkah gagal (mis. stok kurang), semuanya dibatalkan (rollback).
     * Status awal transaksi selalu 'Belum' (barang belum diserahkan ke pelanggan).
     *
     * @return id_transaksi yang baru dibuat
     */
    public int simpanTransaksi(Transaksi t) throws SQLException, StokTidakCukupException {
        String sqlTransaksi = "INSERT INTO transaksi "
                + "(tanggal, total, metode_pembayaran, bayar, kembalian, status_pesanan, id_pengguna) "
                + "VALUES (NOW(), ?, ?, ?, ?, 'Belum', ?)";
        String sqlStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";
        String sqlDetail = "INSERT INTO detail_transaksi "
                + "(id_transaksi, id_produk, jumlah, harga_satuan, subtotal, catatan) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = Koneksi.getConnection()) {
            c.setAutoCommit(false);
            try {
                int idTransaksi;
                try (PreparedStatement ps = c.prepareStatement(sqlTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setBigDecimal(1, t.getTotal());
                    ps.setString(2, t.getMetodePembayaran());
                    ps.setBigDecimal(3, t.getBayar());
                    ps.setBigDecimal(4, t.getKembalian());
                    ps.setInt(5, t.getIdPengguna());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idTransaksi = keys.getInt(1);
                    }
                }

                try (PreparedStatement psStok = c.prepareStatement(sqlStok);
                     PreparedStatement psDetail = c.prepareStatement(sqlDetail)) {
                    for (DetailTransaksi d : t.getDaftarDetail()) {
                        // kurangi stok hanya bila masih cukup
                        psStok.setInt(1, d.getJumlah());
                        psStok.setInt(2, d.getProduk().getIdProduk());
                        psStok.setInt(3, d.getJumlah());
                        if (psStok.executeUpdate() == 0) {
                            throw new StokTidakCukupException(d.getProduk().getNamaTampil());
                        }

                        String catatan = d.getCatatan();
                        psDetail.setInt(1, idTransaksi);
                        psDetail.setInt(2, d.getProduk().getIdProduk());
                        psDetail.setInt(3, d.getJumlah());
                        psDetail.setBigDecimal(4, d.getHargaSatuan());
                        psDetail.setBigDecimal(5, d.getSubtotal());
                        psDetail.setString(6, (catatan == null || catatan.isEmpty()) ? null : catatan);
                        psDetail.executeUpdate();
                    }
                }

                c.commit();
                return idTransaksi;
            } catch (SQLException | StokTidakCukupException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    /**
     * Daftar transaksi terbaru di atas.
     * @param filterStatus "Belum", "Selesai", atau null / "Semua" untuk semuanya
     */
    public List<Transaksi> ambilSemua(String filterStatus) throws SQLException {
        boolean pakaiFilter = filterStatus != null
                && (Transaksi.STATUS_BELUM.equals(filterStatus) || Transaksi.STATUS_SELESAI.equals(filterStatus));
        String sql = "SELECT t.id_transaksi, t.tanggal, t.total, t.metode_pembayaran, t.bayar, t.kembalian, "
                + "t.status_pesanan, t.waktu_selesai, t.id_pengguna, p.nama_lengkap "
                + "FROM transaksi t JOIN pengguna p ON p.id_pengguna = t.id_pengguna "
                + (pakaiFilter ? "WHERE t.status_pesanan = ? " : "")
                + "ORDER BY t.tanggal DESC, t.id_transaksi DESC";
        List<Transaksi> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (pakaiFilter) {
                ps.setString(1, filterStatus);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setIdTransaksi(rs.getInt("id_transaksi"));
                    t.setTanggal(rs.getTimestamp("tanggal").toLocalDateTime());
                    t.setTotal(rs.getBigDecimal("total"));
                    t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                    t.setBayar(rs.getBigDecimal("bayar"));
                    t.setKembalian(rs.getBigDecimal("kembalian"));
                    t.setStatusPesanan(rs.getString("status_pesanan"));
                    Timestamp selesai = rs.getTimestamp("waktu_selesai");
                    t.setWaktuSelesai(selesai == null ? null : selesai.toLocalDateTime());
                    t.setIdPengguna(rs.getInt("id_pengguna"));
                    t.setNamaKasir(rs.getString("nama_lengkap"));
                    hasil.add(t);
                }
            }
        }
        return hasil;
    }

    public List<DetailTransaksi> ambilDetail(int idTransaksi) throws SQLException {
        String sql = "SELECT d.id_detail, d.id_produk, pr.nama_produk, pr.ukuran, "
                + "d.jumlah, d.harga_satuan, d.subtotal, d.catatan "
                + "FROM detail_transaksi d JOIN produk pr ON pr.id_produk = d.id_produk "
                + "WHERE d.id_transaksi = ? ORDER BY d.id_detail";
        List<DetailTransaksi> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produk p = new Produk();
                    p.setIdProduk(rs.getInt("id_produk"));
                    p.setNamaProduk(rs.getString("nama_produk"));
                    p.setUkuran(rs.getString("ukuran"));

                    DetailTransaksi d = new DetailTransaksi();
                    d.setIdDetail(rs.getInt("id_detail"));
                    d.setProduk(p);
                    d.setJumlah(rs.getInt("jumlah"));
                    d.setHargaSatuan(rs.getBigDecimal("harga_satuan"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    d.setCatatan(rs.getString("catatan"));
                    hasil.add(d);
                }
            }
        }
        return hasil;
    }

    /**
     * Menandai pesanan sudah sampai ke pelanggan.
     * @return true bila berhasil, false bila transaksi tidak ada atau sudah Selesai
     */
    public boolean tandaiSelesai(int idTransaksi) throws SQLException {
        String sql = "UPDATE transaksi SET status_pesanan = 'Selesai', waktu_selesai = NOW() "
                + "WHERE id_transaksi = ? AND status_pesanan = 'Belum'";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            return ps.executeUpdate() > 0;
        }
    }
}
