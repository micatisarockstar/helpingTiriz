package com.mycompany.helpingtiriz.repository;

import com.mycompany.helpingtiriz.entity.Produk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {
    private static final String KOLOM =
            "id_produk, kode_produk, nama_produk, kategori, ukuran, harga, stok, stok_minimum";

    /** Semua produk (untuk data barang), Keyword kosong = tampilkan semua. */
    public List<Produk> cari(String keyword) throws SQLException {
        String sql = "SELECT " + KOLOM + " FROM produk "
                + "WHERE kode_produk LIKE ? OR nama_produk LIKE ? ORDER BY id_produk";
        return ambilDenganKeyword(sql, keyword);
    }

    /** Produk yang dijual di kasir: Minuman & Makanan saja (topping tidak dijual sendiri). */
    public List<Produk> ambilUntukJual(String keyword) throws SQLException {
        String sql = "SELECT " + KOLOM + " FROM produk WHERE kategori <> 'Topping' "
                + "AND (kode_produk LIKE ? OR nama_produk LIKE ?) "
                + "ORDER BY kategori, nama_produk, FIELD(ukuran, 'S', 'M', 'L')";
        return ambilDenganKeyword(sql, keyword);
    }

    public List<Produk> ambilTopping() throws SQLException {
        String sql = "SELECT " + KOLOM + " FROM produk WHERE kategori = 'Topping' ORDER BY nama_produk";
        List<Produk> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                hasil.add(baris(rs));
            }
        }
        return hasil;
    }

    public void tambah(Produk p) throws SQLException {
        String sql = "INSERT INTO produk (kode_produk, nama_produk, kategori, ukuran, harga, stok, stok_minimum) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            isiParameter(ps, p);
            ps.executeUpdate();
        }
    }

    /** @return jumlah baris yang berubah (0 = produk tidak ditemukan, mungkin sudah dihapus) */
    public int ubah(Produk p) throws SQLException {
        String sql = "UPDATE produk SET kode_produk = ?, nama_produk = ?, kategori = ?, ukuran = ?, "
                + "harga = ?, stok = ?, stok_minimum = ? WHERE id_produk = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            isiParameter(ps, p);
            ps.setInt(8, p.getIdProduk());
            return ps.executeUpdate();
        }
    }

    /** @return jumlah baris yang terhapus (0 = produk tidak ditemukan) */
    public int hapus(int idProduk) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM produk WHERE id_produk = ?")) {
            ps.setInt(1, idProduk);
            return ps.executeUpdate();
        }
    }

    /** @return jumlah baris yang berubah (0 = produk tidak ditemukan) */
    public int tambahStok(int idProduk, int jumlah) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE produk SET stok = stok + ? WHERE id_produk = ?")) {
            ps.setInt(1, jumlah);
            ps.setInt(2, idProduk);
            return ps.executeUpdate();
        }
    }

    /** true bila kode sudah dipakai produk lain. Saat tambah isi idKecuali = 0. */
    public boolean kodeSudahAda(String kode, int idKecuali) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produk WHERE kode_produk = ? AND id_produk <> ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, idKecuali);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Produk yang sudah ada di transaksi tidak boleh dihapus (foreign key). */
    public boolean sudahPernahTerjual(int idProduk) throws SQLException {
        String sql = "SELECT COUNT(*) FROM detail_transaksi WHERE id_produk = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // ---------- helper ----------
    private List<Produk> ambilDenganKeyword(String sql, String keyword) throws SQLException {
        String pola = "%" + escapeLike(keyword) + "%";
        List<Produk> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pola);
            ps.setString(2, pola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(baris(rs));
                }
            }
        }
        return hasil;
    }

    /** Supaya karakter % dan _ yang diketik user dianggap huruf biasa, bukan wildcard LIKE. */
    private String escapeLike(String teks) {
        return teks.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private void isiParameter(PreparedStatement ps, Produk p) throws SQLException {
        ps.setString(1, p.getKodeProduk());
        ps.setString(2, p.getNamaProduk());
        ps.setString(3, p.getKategori());
        ps.setString(4, p.getUkuran());
        ps.setBigDecimal(5, p.getHarga());
        ps.setInt(6, p.getStok());
        ps.setInt(7, p.getStokMinimum());
    }

    private Produk baris(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setIdProduk(rs.getInt("id_produk"));
        p.setKodeProduk(rs.getString("kode_produk"));
        p.setNamaProduk(rs.getString("nama_produk"));
        p.setKategori(rs.getString("kategori"));
        p.setUkuran(rs.getString("ukuran"));
        p.setHarga(rs.getBigDecimal("harga"));
        p.setStok(rs.getInt("stok"));
        p.setStokMinimum(rs.getInt("stok_minimum"));
        return p;
    }
}
