package com.mycompany.helpingtiriz.controller;

import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.repository.ProdukDAO;
import com.mycompany.helpingtiriz.util.Sesi;
import com.mycompany.helpingtiriz.util.Validasi;
import java.sql.SQLException;
import java.util.List;

/** CONTROLLER Data Barang: semua pemeriksaan dan aturan produk */
public class ProdukController {
    public static final String[] KATEGORI = {"Minuman", "Makanan", "Topping"};
    public static final String[] UKURAN = {"-", "S", "M", "L"};
    private static final int MAKS = 1_000_000;

    private final ProdukDAO dao = new ProdukDAO();

    public List<Produk> cari(String keyword) throws ValidasiException {
        try {
            return dao.cari(keyword == null ? "" : keyword.trim());
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "memuat data barang");
        }
    }

    public void tambah(String kode, String nama, String kategori, String ukuran,
                       String harga, String stok, String stokMin) throws ValidasiException {
        wajibAdmin();
        Produk p = periksa(kode, nama, kategori, ukuran, harga, stok, stokMin);
        try {
            if (dao.kodeSudahAda(p.getKodeProduk(), 0)) {
                throw new ValidasiException("Kode " + p.getKodeProduk() + " sudah dipakai.");
            }
            dao.tambah(p);
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "menambah produk");
        }
    }

    public void ubah(Produk terpilih, String kode, String nama, String kategori, String ukuran,
                     String harga, String stok, String stokMin) throws ValidasiException {
        wajibAdmin();
        if (terpilih == null) {
            throw new ValidasiException("Pilih produk di tabel yang akan diubah.");
        }
        Produk p = periksa(kode, nama, kategori, ukuran, harga, stok, stokMin);
        p.setIdProduk(terpilih.getIdProduk());
        try {
            if (dao.kodeSudahAda(p.getKodeProduk(), p.getIdProduk())) {
                throw new ValidasiException("Kode " + p.getKodeProduk() + " sudah dipakai produk lain.");
            }
            dao.ubah(p);
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "mengubah produk");
        }
    }

    public void hapus(Produk terpilih) throws ValidasiException {
        wajibAdmin();
        if (terpilih == null) {
            throw new ValidasiException("Pilih produk di tabel yang akan dihapus.");
        }
        try {
            if (dao.sudahPernahTerjual(terpilih.getIdProduk())) {
                throw new ValidasiException("Produk ini sudah pernah terjual, tidak bisa dihapus.\n"
                        + "Ubah stoknya menjadi 0 bila sudah tidak dijual.");
            }
            dao.hapus(terpilih.getIdProduk());
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "menghapus produk");
        }
    }

    public void tambahStok(Produk terpilih, String jumlahTeks) throws ValidasiException {
        wajibAdmin();
        if (terpilih == null) {
            throw new ValidasiException("Pilih produk di tabel yang stoknya akan ditambah.");
        }
        int jumlah = Validasi.bulat("Jumlah tambahan", jumlahTeks, 1, MAKS);
        try {
            dao.tambahStok(terpilih.getIdProduk(), jumlah);
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "menambah stok");
        }
    }

    /** Perika semua isian lalu menyusunnya jadi objek Produk. */
    private Produk periksa(String kode, String nama, String kategori, String ukuran,
                           String harga, String stok, String stokMin) throws ValidasiException {
        String kd = Validasi.wajib("Kode", kode, 20).toUpperCase();
        if (!kd.matches("[A-Z0-9-]+")) {
            throw new ValidasiException("Kode hanya boleh huruf, angka, dan tanda minus (-), tanpa spasi.");
        }
        String nm = Validasi.wajib("Nama", nama, 100);

        boolean minuman = "Minuman".equals(kategori);
        if (minuman && (ukuran == null || "-".equals(ukuran))) {
            throw new ValidasiException("Minuman wajib punya ukuran (S, M, atau L).");
        }

        Produk p = new Produk();
        p.setKodeProduk(kd);
        p.setNamaProduk(nm);
        p.setKategori(kategori);
        p.setUkuran(minuman ? ukuran : null);
        p.setHarga(Validasi.rupiah("Harga", harga, "Topping".equals(kategori) ? 0 : 1, 99_999_999));
        p.setStok(Validasi.bulat("Stok", stok, 0, MAKS));
        p.setStokMinimum(Validasi.bulat("Stok minimum", stokMin, 0, MAKS));
        return p;
    }

    private void wajibAdmin() throws ValidasiException {
        if (!Sesi.isAdmin()) {
            throw new ValidasiException("Hanya admin yang boleh mengelola data barang.");
        }
    }
}
