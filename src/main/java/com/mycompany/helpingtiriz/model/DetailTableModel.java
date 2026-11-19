package com.mycompany.helpingtiriz.model;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Tabel "Detail Barang" di FormRiwayat. */
public class DetailTableModel extends AbstractTableModel {
    private static final String[] KOLOM = {"Barang", "Jumlah", "Harga Satuan", "Subtotal", "Catatan"};

    private List<DetailTransaksi> data = new ArrayList<>();

    public void setData(List<DetailTransaksi> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
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
        DetailTransaksi d = data.get(baris);
        switch (kolom) {
            case 0: return d.getProduk().getNamaTampil();
            case 1: return d.getJumlah();
            case 2: return FormatRupiah.format(d.getHargaSatuan());
            case 3: return FormatRupiah.format(d.getSubtotal());
            case 4: return d.getCatatan() == null ? "" : d.getCatatan();
            default: return "";
        }
    }
}
