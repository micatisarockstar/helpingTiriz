package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.model.PewarnaBaris;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** Mewarnai baris JTable sesuai warnaBaris() dari TableModel-nya (stok nipis, pesanan belum selesai). */
public class BarisRenderer extends DefaultTableCellRenderer {

    /** Mengatur lebar awal kolom JTable, urut dari kolom pertama (angka = lebar relatif). */
    public static void aturLebarKolom(JTable tabel, int... lebar) {
        for (int i = 0; i < lebar.length && i < tabel.getColumnModel().getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            Color warna = null;
            if (table.getModel() instanceof PewarnaBaris) {
                warna = ((PewarnaBaris) table.getModel()).warnaBaris(table.convertRowIndexToModel(row));
            }
            c.setBackground(warna != null ? warna : table.getBackground());
        }
        return c;
    }
}
