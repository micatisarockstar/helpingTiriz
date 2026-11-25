package com.mycompany.helpingtiriz.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Pengaturan tampilan sederhana: font Poppins, warna tabel, dan kotak dialog.
 * Posisi, ukuran, teks, warna, dan font tiap komponen tetap diatur di NetBeans Design.
 */
public final class Tema {
    private static final Color TEKS = new Color(0x1F3D2B);
    private static final Color GARIS = new Color(0xA9CBA9);
    private static final Color HEADER = new Color(0xDCEBDC);
    private static final Color SELEKSI = new Color(0xBFE3CB);
    private static final Color BARIS_GENAP = new Color(0xF6FBF6);

    private Tema() {
    }

    /** Dipanggil sekali di Main: memuat font Poppins dan mengatur font bawaan semua komponen. */
    public static void siapkan() {
        for (String nama : new String[]{"Regular", "Medium", "Bold"}) {
            try (InputStream in = Tema.class.getResourceAsStream("/fonts/Poppins-" + nama + ".ttf")) {
                if (in != null) {
                    GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .registerFont(Font.createFont(Font.TRUETYPE_FONT, in));
                }
            } catch (Exception e) {
                System.err.println("Font Poppins-" + nama + " gagal dimuat: " + e);
            }
        }
        try {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Look and Feel gagal diatur: " + e);
        }
        Font biasa = new Font("Poppins", Font.PLAIN, 13);
        for (String kunci : new String[]{"Label.font", "Button.font", "TextField.font", "PasswordField.font",
            "ComboBox.font", "Spinner.font", "Table.font", "TableHeader.font", "TextArea.font",
            "OptionPane.messageFont", "OptionPane.buttonFont", "List.font", "CheckBox.font"}) {
            UIManager.put(kunci, new FontUIResource(biasa));
        }
        UIManager.put("OptionPane.messageForeground", TEKS);
    }

    /** Dipanggil tiap form setelah initComponents(): memoles tombol, isian, dan tabel. */
    public static void terapkan(Container c) {
        for (Component k : c.getComponents()) {
            if (k instanceof JButton) {
                efekHover((JButton) k);
            } else if (k instanceof JTextField) {
                ((JTextField) k).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GARIS), BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            } else if (k instanceof JScrollPane) {
                ((JScrollPane) k).setBorder(BorderFactory.createLineBorder(GARIS));
                Component isi = ((JScrollPane) k).getViewport().getView();
                if (isi instanceof JTable) {
                    gayaTabel((JTable) isi, -1, null, null);
                }
            } else if (k instanceof Container) {
                terapkan((Container) k);
            }
        }
    }

    private static void efekHover(JButton b) {
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final Color asli = b.getBackground();
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(asli.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(asli);
            }
        });
    }

    // ---------------- tabel ----------------

    /** Model tabel yang barisnya tidak bisa diketik/diedit user. */
    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(new Object[0][0], kolom) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
    }

    public static void lebarKolom(JTable t, int... lebar) {
        for (int i = 0; i < lebar.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    /** Baris yang kolom statusnya bernilai "teks" diberi warna (mis. stok menipis). */
    public static void sorotBaris(JTable t, int kolomStatus, String teks, Color warna) {
        gayaTabel(t, kolomStatus, teks, warna);
    }

    /** Kolom teks panjang (mis. catatan) ditampilkan bertingkat ke bawah, tidak terpotong. */
    public static void bungkusKolom(JTable t, int kolom) {
        final JTextArea area = areaBungkus(t);
        t.getColumnModel().getColumn(kolom).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object nilai, boolean dipilih,
                                                           boolean fokus, int baris, int kol) {
                area.setFont(tbl.getFont());
                area.setText(nilai == null ? "" : nilai.toString());
                area.setBackground(dipilih ? SELEKSI : (baris % 2 == 0 ? Color.WHITE : BARIS_GENAP));
                return area;
            }
        });
    }

    /** Menyesuaikan tinggi tiap baris dengan banyaknya baris teks di kolom bungkus. Panggil setelah isi tabel diubah. */
    public static void sesuaikanTinggi(JTable t, int kolom) {
        JTextArea area = areaBungkus(t);
        int lebar = t.getColumnModel().getColumn(kolom).getWidth();
        for (int baris = 0; baris < t.getRowCount(); baris++) {
            Object nilai = t.getValueAt(baris, kolom);
            area.setText(nilai == null ? "" : nilai.toString());
            area.setSize(lebar, Short.MAX_VALUE);
            t.setRowHeight(baris, Math.max(30, area.getPreferredSize().height));
        }
    }

    private static JTextArea areaBungkus(JTable t) {
        JTextArea area = new JTextArea();
        area.setFont(t.getFont());
        area.setForeground(TEKS);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return area;
    }

    private static void gayaTabel(JTable t, int kolomStatus, String teks, Color warna) {
        t.setRowHeight(30);
        t.setGridColor(new Color(0xE3EDE3));
        t.setSelectionBackground(SELEKSI);
        t.setSelectionForeground(TEKS);
        t.getTableHeader().setBackground(HEADER);
        t.getTableHeader().setForeground(TEKS);
        t.getTableHeader().setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object nilai, boolean dipilih,
                                                           boolean fokus, int baris, int kolom) {
                Component c = super.getTableCellRendererComponent(tbl, nilai, dipilih, false, baris, kolom);
                c.setForeground(TEKS);
                if (dipilih) {
                    c.setBackground(SELEKSI);
                } else if (kolomStatus >= 0 && teks.equals(tbl.getValueAt(baris, kolomStatus))) {
                    c.setBackground(warna);
                } else {
                    c.setBackground(baris % 2 == 0 ? Color.WHITE : BARIS_GENAP);
                }
                return c;
            }
        });
    }

    // ---------------- dialog ----------------

    public static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void peringatan(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Periksa Kembali", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    /** @return true bila user menekan "Ya" */
    public static boolean konfirmasi(Component induk, String pesan) {
        return JOptionPane.showOptionDialog(induk, pesan, "Konfirmasi", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Ya", "Tidak"}, "Ya") == 0;
    }

    /** @return teks yang diketik user, atau null bila dibatalkan */
    public static String input(Component induk, String judul, String pesan) {
        return JOptionPane.showInputDialog(induk, pesan, judul, JOptionPane.QUESTION_MESSAGE);
    }
}
