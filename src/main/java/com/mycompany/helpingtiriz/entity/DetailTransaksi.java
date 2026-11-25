package com.mycompany.helpingtiriz.entity;

import java.math.BigDecimal;

public class DetailTransaksi {
    private int idDetail;
    private Produk produk;
    private int jumlah;
    private BigDecimal hargaSatuan = BigDecimal.ZERO;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private String catatan;        // topping + catatan bebas

    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }
    public Produk getProduk() { return produk; }
    public void setProduk(Produk produk) { this.produk = produk; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public BigDecimal getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(BigDecimal hargaSatuan) { this.hargaSatuan = hargaSatuan; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    /** subtotal = jumlah x hargaSatuan. */
    public BigDecimal hitungSubtotal() {
        subtotal = hargaSatuan.multiply(BigDecimal.valueOf(jumlah));
        return subtotal;
    }
}
