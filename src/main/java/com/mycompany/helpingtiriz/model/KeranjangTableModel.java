package com.mycompany.helpingtiriz.model;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Keranjang kasir: daftar DetailTransaksi yang belum disimpan ke database. */
public class KeranjangTableModel extends AbstractTableModel {
    private static final String[] KOLOM = {"Nama", "Jumlah", "Harga", "Subtotal", "Catatan"};

    private final List<DetailTransaksi> items = new ArrayList<>();

    /** Produk + catatan yang sama digabung jadi satu baris. */
    public void tambah(DetailTransaksi baru) {
        for (DetailTransaksi d : items) {
            boolean produkSama = d.getProduk().getIdProduk() == baru.getProduk().getIdProduk();
            boolean catatanSama = nvl(d.getCatatan()).equals(nvl(baru.getCatatan()));
            if (produkSama && catatanSama) {
                d.setJumlah(d.getJumlah() + baru.getJumlah());
                d.hitungSubtotal();
                fireTableDataChanged();
                return;
            }
        }
        items.add(baru);
        fireTableDataChanged();
    }

    public void hapus(int baris) {
        items.remove(baris);
        fireTableDataChanged();
    }

    public void kosongkan() {
        items.clear();
        fireTableDataChanged();
    }

    /** Total jumlah satu produk di seluruh baris keranjang (untuk cek stok). */
    public int jumlahDiKeranjang(int idProduk) {
        int total = 0;
        for (DetailTransaksi d : items) {
            if (d.getProduk().getIdProduk() == idProduk) {
                total += d.getJumlah();
            }
        }
        return total;
    }

    public BigDecimal hitungTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetailTransaksi d : items) {
            total = total.add(d.getSubtotal());
        }
        return total;
    }

    public List<DetailTransaksi> getItems() {
        return items;
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return KOLOM.length;
    }

    @Override
    public String getColumnName(int kolom) {
        return KOLOM[kolom];
    }

    @Override
    public Object getValueAt(int baris, int kolom) {
        DetailTransaksi d = items.get(baris);
        switch (kolom) {
            case 0: return d.getProduk().getNamaTampil();
            case 1: return d.getJumlah();
            case 2: return FormatRupiah.format(d.getHargaSatuan());
            case 3: return FormatRupiah.format(d.getSubtotal());
            case 4: return nvl(d.getCatatan());
            default: return "";
        }
    }
}
