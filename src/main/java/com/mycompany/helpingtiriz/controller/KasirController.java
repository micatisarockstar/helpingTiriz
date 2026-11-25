package com.mycompany.helpingtiriz.controller;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.repository.ProdukDAO;
import com.mycompany.helpingtiriz.repository.TransaksiDAO;
import com.mycompany.helpingtiriz.util.FormatRupiah;
import com.mycompany.helpingtiriz.util.Sesi;
import com.mycompany.helpingtiriz.util.Validasi;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER Kasir: memegang keranjang, memeriksa stok / topping / pembayaran,
 * lalu menyimpan transaksi lewat DAO.
 */
public class KasirController {
    public static final String[] METODE = {"Tunai", "QRIS", "Transfer"};

    private final ProdukDAO produkDAO = new ProdukDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final List<DetailTransaksi> keranjang = new ArrayList<>();
    private List<Produk> daftarTopping = new ArrayList<>();

    public List<DetailTransaksi> getKeranjang() {
        return keranjang;
    }

    // ---------------- data ----------------

    public List<Produk> cariProduk(String keyword) throws ValidasiException {
        try {
            return produkDAO.ambilUntukJual(keyword == null ? "" : keyword.trim());
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "memuat daftar barang");
        }
    }

    /** Daftar topping untuk pilihan kasir, mis. "Oreo (+Rp 2.000)". */
    public List<String> muatTopping() throws ValidasiException {
        try {
            daftarTopping = produkDAO.ambilTopping();
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "memuat daftar topping");
        }
        List<String> label = new ArrayList<>();
        for (Produk p : daftarTopping) {
            label.add(labelTopping(p));
        }
        return label;
    }

    private String labelTopping(Produk p) {
        return p.getNamaProduk() + (p.getHarga().signum() > 0 ? " (+" + FormatRupiah.format(p.getHarga()) + ")" : "");
    }

    // ---------------- keranjang ----------------

    /** Memasukkan satu baris ke keranjang. topping1/topping2 boleh null bila tidak dipilih. */
    public void tambahKeKeranjang(Produk produk, int jumlah, String topping1, String topping2,
                                  String catatanBebas) throws ValidasiException {
        if (produk == null) {
            throw new ValidasiException("Pilih barang di tabel terlebih dahulu.");
        }
        if ("Topping".equals(produk.getKategori())) {
            throw new ValidasiException("Topping tidak dijual sendiri. Tambahkan sebagai topping barang lain.");
        }
        if (jumlah < 1 || jumlah > 999) {
            throw new ValidasiException("Jumlah harus antara 1 dan 999.");
        }

        // stok: yang sudah ada di keranjang + yang baru tidak boleh melebihi stok
        int sudahAda = 0;
        for (DetailTransaksi d : keranjang) {
            if (d.getProduk().getIdProduk() == produk.getIdProduk()) {
                sudahAda += d.getJumlah();
            }
        }
        if (sudahAda + jumlah > produk.getStok()) {
            throw new ValidasiException("Stok " + produk.getNamaTampil() + " tidak cukup.\nSisa stok: "
                    + produk.getStok() + (sudahAda > 0 ? " (" + sudahAda + " sudah ada di keranjang)" : ""));
        }

        // topping: maksimal 2, Topping 2 butuh Topping 1, boleh sama; harga & stok topping ikut dihitung
        List<Produk> terpilih = new ArrayList<>();
        boolean ada1 = topping1 != null && !topping1.isEmpty();
        boolean ada2 = topping2 != null && !topping2.isEmpty();
        if (ada2 && !ada1) {
            throw new ValidasiException("Pilih Topping 1 dulu sebelum memilih Topping 2.");
        }
        if (ada1) {
            terpilih.add(cariTopping(topping1));
        }
        if (ada2) {
            terpilih.add(cariTopping(topping2));
        }
        BigDecimal hargaSatuan = produk.getHarga();
        List<String> bagian = new ArrayList<>();
        if (!terpilih.isEmpty()) {
            List<String> nama = new ArrayList<>();
            for (Produk t : terpilih) {
                hargaSatuan = hargaSatuan.add(t.getHarga());
                nama.add(t.getNamaProduk());
                int perCup = 0;                           // topping ini dipakai berapa kali per cup
                for (Produk x : terpilih) {
                    if (x.getIdProduk() == t.getIdProduk()) {
                        perCup++;
                    }
                }
                int terpakai = 0;                         // yang sudah di keranjang
                for (DetailTransaksi d : keranjang) {
                    for (Produk x : d.getTopping()) {
                        if (x.getIdProduk() == t.getIdProduk()) {
                            terpakai += d.getJumlah();
                        }
                    }
                }
                if (terpakai + perCup * jumlah > t.getStok()) {
                    throw new ValidasiException("Stok topping " + t.getNamaProduk() + " tidak cukup.\nSisa stok: "
                            + t.getStok() + (terpakai > 0 ? " (" + terpakai + " sudah dipakai di keranjang)" : ""));
                }
            }
            bagian.add("Topping: " + String.join(", ", nama));
        }
        String bebas = catatanBebas == null ? "" : catatanBebas.trim().replaceAll("\\s+", " ");
        if (!bebas.isEmpty()) {
            bagian.add(bebas);
        }
        String catatan = String.join(" | ", bagian);
        if (catatan.length() > 200) {
            throw new ValidasiException("Catatan terlalu panjang (maksimal 200 karakter).");
        }

        // produk + catatan yang sama digabung jadi satu baris
        for (DetailTransaksi d : keranjang) {
            if (d.getProduk().getIdProduk() == produk.getIdProduk() && catatan.equals(nvl(d.getCatatan()))) {
                d.setJumlah(d.getJumlah() + jumlah);
                d.hitungSubtotal();
                return;
            }
        }
        DetailTransaksi d = new DetailTransaksi();
        d.setProduk(produk);
        d.setJumlah(jumlah);
        d.setHargaSatuan(hargaSatuan);                    // harga barang + harga topping
        d.setCatatan(catatan);
        d.setTopping(terpilih);
        d.hitungSubtotal();
        keranjang.add(d);
    }

    private Produk cariTopping(String label) throws ValidasiException {
        for (Produk p : daftarTopping) {
            if (labelTopping(p).equals(label)) {
                return p;
            }
        }
        throw new ValidasiException("Topping \"" + label + "\" tidak ada di daftar topping.");
    }

    public void hapusItem(int baris) throws ValidasiException {
        if (baris < 0 || baris >= keranjang.size()) {
            throw new ValidasiException("Pilih baris di keranjang yang akan dihapus.");
        }
        keranjang.remove(baris);
    }

    public void kosongkan() {
        keranjang.clear();
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetailTransaksi d : keranjang) {
            total = total.add(d.getSubtotal());
        }
        return total;
    }

    // ---------------- pembayaran ----------------

    /** Selisih uang bayar dan total (negatif = kurang). Null bila teksnya belum angka valid. */
    public BigDecimal selisihBayar(String uangBayarTeks) {
        try {
            return FormatRupiah.parse(uangBayarTeks).subtract(total());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Memeriksa pembayaran lalu menyimpan transaksi + mengurangi stok. Keranjang dikosongkan bila sukses. */
    public Transaksi bayarDanSimpan(String metode, String uangBayarTeks) throws ValidasiException {
        if (!Sesi.sudahLogin()) {
            throw new ValidasiException("Sesi login habis. Silakan login ulang.");
        }
        if (keranjang.isEmpty()) {
            throw new ValidasiException("Keranjang masih kosong.");
        }
        BigDecimal total = total();
        BigDecimal bayar = total;                       // QRIS / Transfer: uang pas
        if ("Tunai".equals(metode)) {
            bayar = Validasi.rupiah("Uang bayar", uangBayarTeks, 0, 999_999_999L);
            if (bayar.compareTo(total) < 0) {
                throw new ValidasiException("Uang bayar kurang " + FormatRupiah.format(total.subtract(bayar)) + ".");
            }
        }

        Transaksi t = new Transaksi();
        t.setIdPengguna(Sesi.getPenggunaAktif().getIdPengguna());
        t.setNamaKasir(Sesi.getPenggunaAktif().getNamaLengkap());
        t.setMetodePembayaran(metode);
        t.setDaftarDetail(new ArrayList<>(keranjang));
        t.setTotal(total);
        t.setBayar(bayar);
        t.setKembalian(bayar.subtract(total));
        t.setTanggal(LocalDateTime.now());
        try {
            t.setIdTransaksi(transaksiDAO.simpanTransaksi(t));
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "menyimpan transaksi");
        }
        keranjang.clear();
        return t;
    }

    /** Teks struk untuk ditampilkan setelah transaksi tersimpan. */
    public String buatStruk(Transaksi t) {
        String garis = "------------------------------------------\n";
        StringBuilder sb = new StringBuilder();
        sb.append("                 TIRIZ\n  Your day with peace, love, and TIRIZ\n").append(garis);
        sb.append("No Transaksi : ").append(t.getIdTransaksi()).append('\n');
        sb.append("Tanggal      : ").append(t.getTanggal().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append('\n');
        sb.append("Kasir        : ").append(t.getNamaKasir()).append('\n');
        sb.append("Metode       : ").append(t.getMetodePembayaran()).append('\n').append(garis);
        for (DetailTransaksi d : t.getDaftarDetail()) {
            sb.append(d.getProduk().getNamaTampil()).append("\n  ").append(d.getJumlah()).append(" x ")
                    .append(FormatRupiah.format(d.getHargaSatuan())).append(" = ")
                    .append(FormatRupiah.format(d.getSubtotal())).append('\n');
            if (!nvl(d.getCatatan()).isEmpty()) {
                sb.append("  * ").append(d.getCatatan()).append('\n');
            }
        }
        sb.append(garis);
        sb.append(String.format("%-10s: %s%n", "Total", FormatRupiah.format(t.getTotal())));
        sb.append(String.format("%-10s: %s%n", "Bayar", FormatRupiah.format(t.getBayar())));
        sb.append(String.format("%-10s: %s%n", "Kembalian", FormatRupiah.format(t.getKembalian())));
        sb.append(garis).append("Status pesanan: Belum diserahkan\n");
        return sb.toString();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }
}
