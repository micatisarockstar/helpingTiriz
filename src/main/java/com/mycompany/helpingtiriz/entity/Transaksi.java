package com.mycompany.helpingtiriz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    public static final String STATUS_BELUM = "Belum";
    public static final String STATUS_SELESAI = "Selesai";

    private int idTransaksi;
    private LocalDateTime tanggal;
    private BigDecimal total = BigDecimal.ZERO;
    private String metodePembayaran = "Tunai";   // Tunai / QRIS / Transfer
    private BigDecimal bayar = BigDecimal.ZERO;
    private BigDecimal kembalian = BigDecimal.ZERO;
    private String statusPesanan = STATUS_BELUM; // Belum = belum sampai ke pelanggan
    private LocalDateTime waktuSelesai;
    private int idPengguna;
    private String namaKasir;
    private List<DetailTransaksi> daftarDetail = new ArrayList<>();

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }
    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public BigDecimal getBayar() { return bayar; }
    public void setBayar(BigDecimal bayar) { this.bayar = bayar; }
    public BigDecimal getKembalian() { return kembalian; }
    public void setKembalian(BigDecimal kembalian) { this.kembalian = kembalian; }
    public String getStatusPesanan() { return statusPesanan; }
    public void setStatusPesanan(String statusPesanan) { this.statusPesanan = statusPesanan; }
    public LocalDateTime getWaktuSelesai() { return waktuSelesai; }
    public void setWaktuSelesai(LocalDateTime waktuSelesai) { this.waktuSelesai = waktuSelesai; }
    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }
    public String getNamaKasir() { return namaKasir; }
    public void setNamaKasir(String namaKasir) { this.namaKasir = namaKasir; }
    public List<DetailTransaksi> getDaftarDetail() { return daftarDetail; }
    public void setDaftarDetail(List<DetailTransaksi> daftarDetail) { this.daftarDetail = daftarDetail; }

    public boolean isSelesai() {
        return STATUS_SELESAI.equals(statusPesanan);
    }

    /** Jumlah seluruh subtotal di daftarDetail. */
    public BigDecimal hitungTotal() {
        BigDecimal hasil = BigDecimal.ZERO;
        for (DetailTransaksi d : daftarDetail) {
            hasil = hasil.add(d.getSubtotal());
        }
        return hasil;
    }

    /** kembalian = bayar - total. */
    public BigDecimal hitungKembalian() {
        return bayar.subtract(total);
    }
}
