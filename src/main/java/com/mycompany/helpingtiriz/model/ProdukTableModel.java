package com.mycompany.helpingtiriz.model;

import com.mycompany.helpingtiriz.entity.Produk;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Dipakai tabel produk di FormProduk dan FormKasir. */
public class ProdukTableModel extends AbstractTableModel implements PewarnaBaris {
    private static final String[] KOLOM =
            {"ID", "Kode", "Nama", "Kategori", "Ukuran", "Harga", "Stok", "Stok Min", "Status"};
    private static final Color MERAH_MUDA = new Color(0xFFD6DC);

    private List<Produk> data = new ArrayList<>();

    public void setData(List<Produk> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Produk getAt(int baris) {
        return data.get(baris);
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
        Produk p = data.get(baris);
        switch (kolom) {
            case 0: return p.getIdProduk();
            case 1: return p.getKodeProduk();
            case 2: return p.getNamaProduk();
            case 3: return p.getKategori();
            case 4: return p.getUkuran() == null ? "-" : p.getUkuran();
            case 5: return FormatRupiah.format(p.getHarga());
            case 6: return p.getStok();
            case 7: return p.getStokMinimum();
            case 8: return p.isStokMenipis() ? "Menipis" : "Aman";
            default: return "";
        }
    }

    @Override
    public Color warnaBaris(int barisModel) {
        return data.get(barisModel).isStokMenipis() ? MERAH_MUDA : null;
    }
}
