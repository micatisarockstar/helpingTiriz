package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.controller.ProdukController;
import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.util.FormatRupiah;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** VIEW Data Barang: membaca isian, memanggil ProdukController, lalu menampilkan hasilnya. */
public class FormProduk extends javax.swing.JFrame {

    private final ProdukController controller = new ProdukController();
    private final DefaultTableModel model = Tema.modelTabel(
            "ID", "Kode", "Nama", "Kategori", "Ukuran", "Harga", "Stok", "Stok Min", "Status");
    private List<Produk> daftar = new ArrayList<>();      // data yang sedang tampil, urutannya sama dengan tabel

    public FormProduk() {
        initComponents();
        Tema.terapkan(getContentPane());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        tblProduk.setModel(model);
        tblProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.lebarKolom(tblProduk, 50, 130, 260, 120, 80, 130, 80, 90, 112);
        Tema.sorotBaris(tblProduk, 8, "Menipis", new Color(0xFFD6DC));   // baris merah muda = stok menipis

        cmbKategori.setModel(new DefaultComboBoxModel<>(ProdukController.KATEGORI));
        cmbUkuran.setModel(new DefaultComboBoxModel<>(ProdukController.UKURAN));
        lblKeterangan.setText("Baris merah muda = stok menipis (di batas minimum atau kurang)");

        btnCari.addActionListener(e -> muatData());
        txtCari.addActionListener(e -> muatData());
        cmbKategori.addActionListener(e -> aturUkuran());
        tblProduk.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnTambahStok.addActionListener(e -> tambahStok());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnKembali.addActionListener(e -> kembali());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                kembali();
            }
        });

        aturUkuran();
        bersihkanForm();
        muatData();
    }

    // ---------------- tombol ----------------

    private void tambah() {
        try {
            controller.tambah(txtKode.getText(), txtNama.getText(), (String) cmbKategori.getSelectedItem(),
                    (String) cmbUkuran.getSelectedItem(), txtHarga.getText(), txtStok.getText(), txtStokMin.getText());
            muatData();
            bersihkanForm();
            Tema.info(this, "Produk berhasil ditambahkan.");
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void ubah() {
        try {
            controller.ubah(produkTerpilih(), txtKode.getText(), txtNama.getText(),
                    (String) cmbKategori.getSelectedItem(), (String) cmbUkuran.getSelectedItem(),
                    txtHarga.getText(), txtStok.getText(), txtStokMin.getText());
            muatData();
            bersihkanForm();
            Tema.info(this, "Produk berhasil diubah.");
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void hapus() {
        Produk p = produkTerpilih();
        if (p != null && !Tema.konfirmasi(this, "Hapus " + p.getNamaTampil() + "?")) {
            return;
        }
        try {
            controller.hapus(p);
            muatData();
            bersihkanForm();
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void tambahStok() {
        Produk p = produkTerpilih();
        if (p == null) {
            Tema.peringatan(this, "Pilih produk di tabel yang stoknya akan ditambah.");
            return;
        }
        String jumlah = Tema.input(this, "Tambah Stok", "Jumlah stok yang ditambahkan untuk "
                + p.getNamaTampil() + "\n(stok sekarang: " + p.getStok() + ")");
        if (jumlah == null) {
            return;                                       // user menekan Batal
        }
        try {
            controller.tambahStok(p, jumlah);
            muatData();
            bersihkanForm();
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void kembali() {
        new FormMenuUtama().setVisible(true);
        dispose();
    }

    // ---------------- helper ----------------

    private void muatData() {
        try {
            daftar = controller.cari(txtCari.getText());
        } catch (ValidasiException ex) {
            Tema.error(this, ex.getMessage());
            return;
        }
        model.setRowCount(0);
        for (Produk p : daftar) {
            model.addRow(new Object[]{p.getIdProduk(), p.getKodeProduk(), p.getNamaProduk(), p.getKategori(),
                p.getUkuran() == null ? "-" : p.getUkuran(), FormatRupiah.format(p.getHarga()), p.getStok(),
                p.getStokMinimum(), p.isStokMenipis() ? "Menipis" : "Aman"});
        }
    }

    private void aturUkuran() {                           // ukuran hanya untuk Minuman
        boolean minuman = "Minuman".equals(cmbKategori.getSelectedItem());
        cmbUkuran.setEnabled(minuman);
        if (!minuman) {
            cmbUkuran.setSelectedItem("-");
        }
    }

    private Produk produkTerpilih() {
        int baris = tblProduk.getSelectedRow();
        return baris < 0 ? null : daftar.get(baris);
    }

    private void isiFormDariBaris() {
        Produk p = produkTerpilih();
        if (p == null) {
            return;
        }
        txtKode.setText(p.getKodeProduk());
        txtNama.setText(p.getNamaProduk());
        cmbKategori.setSelectedItem(p.getKategori());
        cmbUkuran.setSelectedItem(p.getUkuran() == null ? "-" : p.getUkuran());
        txtHarga.setText(p.getHarga().toBigInteger().toString());
        txtStok.setText(String.valueOf(p.getStok()));
        txtStokMin.setText(String.valueOf(p.getStokMinimum()));
    }

    private void bersihkanForm() {
        txtKode.setText("");
        txtNama.setText("");
        cmbKategori.setSelectedIndex(0);
        cmbUkuran.setSelectedIndex(0);
        txtHarga.setText("");
        txtStok.setText("");
        txtStokMin.setText("5");
        tblProduk.clearSelection();
        aturUkuran();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlUtama = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblCari = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        spProduk = new javax.swing.JScrollPane();
        tblProduk = new javax.swing.JTable();
        pnlForm = new javax.swing.JPanel();
        lblKode = new javax.swing.JLabel();
        txtKode = new javax.swing.JTextField();
        lblKategori = new javax.swing.JLabel();
        cmbKategori = new javax.swing.JComboBox<>();
        lblHarga = new javax.swing.JLabel();
        txtHarga = new javax.swing.JTextField();
        lblStokMin = new javax.swing.JLabel();
        txtStokMin = new javax.swing.JTextField();
        lblNama = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        lblUkuran = new javax.swing.JLabel();
        cmbUkuran = new javax.swing.JComboBox<>();
        lblStok = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnTambahStok = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        lblKeterangan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TIRIZ - Data Barang");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(1100, 680));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N

        pnlUtama.add(lblLogo);
        lblLogo.setBounds(24, 16, 150, 42);

        lblCari.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblCari.setForeground(new java.awt.Color(31, 61, 43));
        lblCari.setText("Cari (Kode/Nama):"); // NOI18N

        pnlUtama.add(lblCari);
        lblCari.setBounds(24, 72, 150, 36);

        txtCari.setBackground(new java.awt.Color(255, 255, 255));
        txtCari.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtCari.setForeground(new java.awt.Color(31, 61, 43));

        pnlUtama.add(txtCari);
        txtCari.setBounds(180, 72, 320, 36);

        btnCari.setBackground(new java.awt.Color(46, 139, 87));
        btnCari.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari"); // NOI18N
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.setOpaque(true);

        pnlUtama.add(btnCari);
        btnCari.setBounds(512, 72, 96, 36);


        tblProduk.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        tblProduk.setRowHeight(30);
        tblProduk.setFillsViewportHeight(true);
        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Kode", "Nama", "Kategori", "Ukuran", "Harga", "Stok", "Stok Min", "Status"
            }
        ));

        spProduk.setViewportView(tblProduk);

        pnlUtama.add(spProduk);
        spProduk.setBounds(24, 124, 1052, 232);

        pnlForm.setBackground(new java.awt.Color(240, 255, 240));
        pnlForm.setLayout(null);

        lblKode.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblKode.setForeground(new java.awt.Color(31, 61, 43));
        lblKode.setText("Kode"); // NOI18N

        pnlForm.add(lblKode);
        lblKode.setBounds(32, 16, 130, 36);

        txtKode.setBackground(new java.awt.Color(255, 255, 255));
        txtKode.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtKode.setForeground(new java.awt.Color(31, 61, 43));

        pnlForm.add(txtKode);
        txtKode.setBounds(168, 16, 320, 36);

        lblKategori.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblKategori.setForeground(new java.awt.Color(31, 61, 43));
        lblKategori.setText("Kategori"); // NOI18N

        pnlForm.add(lblKategori);
        lblKategori.setBounds(32, 60, 130, 36);

        cmbKategori.setBackground(new java.awt.Color(255, 255, 255));
        cmbKategori.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbKategori.setForeground(new java.awt.Color(31, 61, 43));
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minuman", "Makanan", "Topping" }));

        pnlForm.add(cmbKategori);
        cmbKategori.setBounds(168, 60, 320, 36);

        lblHarga.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblHarga.setForeground(new java.awt.Color(31, 61, 43));
        lblHarga.setText("Harga (Rp)"); // NOI18N

        pnlForm.add(lblHarga);
        lblHarga.setBounds(32, 104, 130, 36);

        txtHarga.setBackground(new java.awt.Color(255, 255, 255));
        txtHarga.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtHarga.setForeground(new java.awt.Color(31, 61, 43));

        pnlForm.add(txtHarga);
        txtHarga.setBounds(168, 104, 320, 36);

        lblStokMin.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblStokMin.setForeground(new java.awt.Color(31, 61, 43));
        lblStokMin.setText("Stok Minimum"); // NOI18N

        pnlForm.add(lblStokMin);
        lblStokMin.setBounds(32, 148, 130, 36);

        txtStokMin.setBackground(new java.awt.Color(255, 255, 255));
        txtStokMin.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtStokMin.setForeground(new java.awt.Color(31, 61, 43));

        pnlForm.add(txtStokMin);
        txtStokMin.setBounds(168, 148, 320, 36);

        lblNama.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblNama.setForeground(new java.awt.Color(31, 61, 43));
        lblNama.setText("Nama"); // NOI18N

        pnlForm.add(lblNama);
        lblNama.setBounds(552, 16, 130, 36);

        txtNama.setBackground(new java.awt.Color(255, 255, 255));
        txtNama.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtNama.setForeground(new java.awt.Color(31, 61, 43));

        pnlForm.add(txtNama);
        txtNama.setBounds(688, 16, 332, 36);

        lblUkuran.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblUkuran.setForeground(new java.awt.Color(31, 61, 43));
        lblUkuran.setText("Ukuran"); // NOI18N

        pnlForm.add(lblUkuran);
        lblUkuran.setBounds(552, 60, 130, 36);

        cmbUkuran.setBackground(new java.awt.Color(255, 255, 255));
        cmbUkuran.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbUkuran.setForeground(new java.awt.Color(31, 61, 43));
        cmbUkuran.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "S", "M", "L" }));

        pnlForm.add(cmbUkuran);
        cmbUkuran.setBounds(688, 60, 332, 36);

        lblStok.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblStok.setForeground(new java.awt.Color(31, 61, 43));
        lblStok.setText("Stok"); // NOI18N

        pnlForm.add(lblStok);
        lblStok.setBounds(552, 104, 130, 36);

        txtStok.setBackground(new java.awt.Color(255, 255, 255));
        txtStok.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtStok.setForeground(new java.awt.Color(31, 61, 43));

        pnlForm.add(txtStok);
        txtStok.setBounds(688, 104, 332, 36);

        pnlUtama.add(pnlForm);
        pnlForm.setBounds(24, 372, 1052, 196);

        btnTambah.setBackground(new java.awt.Color(46, 139, 87));
        btnTambah.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah"); // NOI18N
        btnTambah.setBorderPainted(false);
        btnTambah.setFocusPainted(false);
        btnTambah.setOpaque(true);

        pnlUtama.add(btnTambah);
        btnTambah.setBounds(24, 584, 120, 44);

        btnUbah.setBackground(new java.awt.Color(207, 229, 207));
        btnUbah.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnUbah.setForeground(new java.awt.Color(46, 139, 87));
        btnUbah.setText("Ubah"); // NOI18N
        btnUbah.setBorderPainted(false);
        btnUbah.setFocusPainted(false);
        btnUbah.setOpaque(true);

        pnlUtama.add(btnUbah);
        btnUbah.setBounds(156, 584, 120, 44);

        btnHapus.setBackground(new java.awt.Color(192, 57, 43));
        btnHapus.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus"); // NOI18N
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.setOpaque(true);

        pnlUtama.add(btnHapus);
        btnHapus.setBounds(288, 584, 120, 44);

        btnTambahStok.setBackground(new java.awt.Color(207, 229, 207));
        btnTambahStok.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnTambahStok.setForeground(new java.awt.Color(46, 139, 87));
        btnTambahStok.setText("Tambah Stok"); // NOI18N
        btnTambahStok.setBorderPainted(false);
        btnTambahStok.setFocusPainted(false);
        btnTambahStok.setOpaque(true);

        pnlUtama.add(btnTambahStok);
        btnTambahStok.setBounds(420, 584, 150, 44);

        btnBersihkan.setBackground(new java.awt.Color(207, 229, 207));
        btnBersihkan.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnBersihkan.setForeground(new java.awt.Color(46, 139, 87));
        btnBersihkan.setText("Bersihkan"); // NOI18N
        btnBersihkan.setBorderPainted(false);
        btnBersihkan.setFocusPainted(false);
        btnBersihkan.setOpaque(true);

        pnlUtama.add(btnBersihkan);
        btnBersihkan.setBounds(582, 584, 130, 44);

        btnKembali.setBackground(new java.awt.Color(207, 229, 207));
        btnKembali.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(46, 139, 87));
        btnKembali.setText("Kembali"); // NOI18N
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);

        pnlUtama.add(btnKembali);
        btnKembali.setBounds(956, 584, 120, 44);

        lblKeterangan.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblKeterangan.setForeground(new java.awt.Color(107, 131, 116));
        lblKeterangan.setText("Baris merah muda = stok menipis"); // NOI18N

        pnlUtama.add(lblKeterangan);
        lblKeterangan.setBounds(24, 640, 700, 22);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTambahStok;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JComboBox<String> cmbUkuran;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblHarga;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblKeterangan;
    private javax.swing.JLabel lblKode;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNama;
    private javax.swing.JLabel lblStok;
    private javax.swing.JLabel lblStokMin;
    private javax.swing.JLabel lblUkuran;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlUtama;
    private javax.swing.JScrollPane spProduk;
    private javax.swing.JTable tblProduk;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtStokMin;
    // End of variables declaration//GEN-END:variables
}
