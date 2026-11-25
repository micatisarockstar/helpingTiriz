package com.mycompany.helpingtiriz.entity;

import java.math.BigDecimal;

public class Produk {
    private int idProduk;
    private String kodeProduk;
    private String namaProduk;
    private String kategori;       // Minuman / Makanan / Topping
    private String ukuran;         // S / M / L untuk minuman, null untuk yang lain
    private BigDecimal harga = BigDecimal.ZERO;
    private int stok;
    private int stokMinimum = 5;

    public int getIdProduk() { return idProduk; }
    public void setIdProduk(int idProduk) { this.idProduk = idProduk; }
    public String getKodeProduk() { return kodeProduk; }
    public void setKodeProduk(String kodeProduk) { this.kodeProduk = kodeProduk; }
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }
    public BigDecimal getHarga() { return harga; }
    public void setHarga(BigDecimal harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public int getStokMinimum() { return stokMinimum; }
    public void setStokMinimum(int stokMinimum) { this.stokMinimum = stokMinimum; }

    /** Stok menipis bila sudah di batas minimum atau kurang. */
    public boolean isStokMenipis() {
        return stok <= stokMinimum;
    }

    /** kek "Matcha Original (M)" atau "Cookies Matcha". */
    public String getNamaTampil() {
        if (ukuran == null || ukuran.isEmpty()) {
            return namaProduk;
        }
        return namaProduk + " (" + ukuran + ")";
    }
}
