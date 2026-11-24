package com.mycompany.helpingtiriz.model;

import com.mycompany.helpingtiriz.entity.Transaksi;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Tabel "Daftar Transaksi" di FormRiwayat. */
public class TransaksiTableModel extends AbstractTableModel implements PewarnaBaris {
    private static final String[] KOLOM =
            {"No", "Tanggal", "Kasir", "Metode", "Total", "Bayar", "Kembalian", "Status"};
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final Color KUNING = new Color(0xFFF3CD);   // pesanan belum diterima pelanggan

    private List<Transaksi> data = new ArrayList<>();

    public void setData(List<Transaksi> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Transaksi getAt(int baris) {
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
        Transaksi t = data.get(baris);
        switch (kolom) {
            case 0: return baris + 1;                        // No = nomor urut, bukan id database
            case 1: return t.getTanggal().format(FORMAT_TANGGAL);
            case 2: return t.getNamaKasir();
            case 3: return t.getMetodePembayaran();
            case 4: return FormatRupiah.format(t.getTotal());
            case 5: return FormatRupiah.format(t.getBayar());
            case 6: return FormatRupiah.format(t.getKembalian());
            case 7: return t.getStatusPesanan();
            default: return "";
        }
    }

    @Override
    public Color warnaBaris(int barisModel) {
        return data.get(barisModel).isSelesai() ? null : KUNING;
    }
}
