package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.model.FormatRupiah;
import com.mycompany.helpingtiriz.model.ProdukTableModel;
import com.mycompany.helpingtiriz.repository.ProdukDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

public class FormProduk extends javax.swing.JFrame {

    private final ProdukDAO produkDAO = new ProdukDAO();
    private final ProdukTableModel tableModel = new ProdukTableModel();

    public FormProduk() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // tombol X ditangani di bawah

        tblProduk.setModel(tableModel);
        tblProduk.setDefaultRenderer(Object.class, new BarisRenderer());   // baris merah muda
        tblProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        BarisRenderer.aturLebarKolom(tblProduk, 50, 130, 260, 120, 80, 130, 80, 90, 120);

        cmbKategori.setModel(new DefaultComboBoxModel<>(new String[]{"Minuman", "Makanan", "Topping"}));
        cmbUkuran.setModel(new DefaultComboBoxModel<>(new String[]{"-", "S", "M", "L"}));
        lblKeterangan.setText("Baris merah muda = stok menipis (di batas minimum atau kurang)");

        pasangEvent();
        aturUkuran();
        muatData("");
    }

    private void pasangEvent() {
        btnCari.addActionListener(e -> muatData(txtCari.getText().trim()));
        txtCari.addActionListener(e -> muatData(txtCari.getText().trim()));   // Enter di kolom cari
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
    }

    // ---------------- aksi tombol ----------------

    private void tambah() {
        try {
            Produk p = bacaForm();
            if (produkDAO.kodeSudahAda(p.getKodeProduk(), 0)) {
                peringatan("Kode produk " + p.getKodeProduk() + " sudah dipakai.");
                return;
            }
            produkDAO.tambah(p);
            muatData(txtCari.getText().trim());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan.");
        } catch (IllegalArgumentException ex) {
            peringatan(ex.getMessage());
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void ubah() {
        Produk terpilih = produkTerpilih();
        if (terpilih == null) {
            peringatan("Pilih produk di tabel yang akan diubah.");
            return;
        }
        try {
            Produk p = bacaForm();
            p.setIdProduk(terpilih.getIdProduk());
            if (produkDAO.kodeSudahAda(p.getKodeProduk(), p.getIdProduk())) {
                peringatan("Kode produk " + p.getKodeProduk() + " sudah dipakai produk lain.");
                return;
            }
            produkDAO.ubah(p);
            muatData(txtCari.getText().trim());
            bersihkanForm();
            JOptionPane.showMessageDialog(this, "Produk berhasil diubah.");
        } catch (IllegalArgumentException ex) {
            peringatan(ex.getMessage());
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void hapus() {
        Produk terpilih = produkTerpilih();
        if (terpilih == null) {
            peringatan("Pilih produk di tabel yang akan dihapus.");
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Hapus " + terpilih.getNamaTampil() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (produkDAO.sudahPernahTerjual(terpilih.getIdProduk())) {
                peringatan("Produk ini sudah pernah terjual, tidak bisa dihapus.\n"
                        + "Ubah stoknya menjadi 0 bila sudah tidak dijual.");
                return;
            }
            produkDAO.hapus(terpilih.getIdProduk());
            muatData(txtCari.getText().trim());
            bersihkanForm();
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void tambahStok() {
        Produk terpilih = produkTerpilih();
        if (terpilih == null) {
            peringatan("Pilih produk di tabel yang stoknya akan ditambah.");
            return;
        }
        String input = JOptionPane.showInputDialog(this,
                "Jumlah stok yang ditambahkan untuk " + terpilih.getNamaTampil()
                        + "\n(stok sekarang: " + terpilih.getStok() + ")",
                "Tambah Stok", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;                                    // user menekan Cancel
        }
        try {
            int jumlah = Integer.parseInt(input.trim());
            if (jumlah <= 0) {
                peringatan("Jumlah harus lebih dari 0.");
                return;
            }
            produkDAO.tambahStok(terpilih.getIdProduk(), jumlah);
            muatData(txtCari.getText().trim());
            bersihkanForm();
        } catch (NumberFormatException ex) {
            peringatan("Jumlah harus berupa angka bulat.");
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void kembali() {
        new FormMenuUtama().setVisible(true);
        dispose();
    }

    // ---------------- helper ----------------

    private void muatData(String keyword) {
        try {
            tableModel.setData(produkDAO.cari(keyword));
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    /** Ukuran hanya dipakai untuk Minuman. */
    private void aturUkuran() {
        boolean minuman = "Minuman".equals(cmbKategori.getSelectedItem());
        cmbUkuran.setEnabled(minuman);
        if (!minuman) {
            cmbUkuran.setSelectedItem("-");
        }
    }

    private Produk produkTerpilih() {
        int baris = tblProduk.getSelectedRow();
        if (baris < 0) {
            return null;
        }
        return tableModel.getAt(tblProduk.convertRowIndexToModel(baris));
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

    /** Membaca & memvalidasi isi form. Melempar IllegalArgumentException berisi pesan untuk user. */
    private Produk bacaForm() {
        String kode = txtKode.getText().trim().toUpperCase();
        String nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) {
            throw new IllegalArgumentException("Kode dan nama wajib diisi.");
        }

        String kategori = (String) cmbKategori.getSelectedItem();
        String ukuran = null;
        if ("Minuman".equals(kategori)) {
            ukuran = (String) cmbUkuran.getSelectedItem();
            if ("-".equals(ukuran)) {
                throw new IllegalArgumentException("Minuman wajib punya ukuran (S, M, atau L).");
            }
        }

        BigDecimal harga;
        int stok;
        int stokMin;
        try {
            harga = FormatRupiah.parse(txtHarga.getText());
            stok = Integer.parseInt(txtStok.getText().trim());
            stokMin = Integer.parseInt(txtStokMin.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Harga, stok, dan stok minimum harus berupa angka.");
        }
        if (stok < 0 || stokMin < 0) {
            throw new IllegalArgumentException("Stok dan stok minimum tidak boleh negatif.");
        }

        Produk p = new Produk();
        p.setKodeProduk(kode);
        p.setNamaProduk(nama);
        p.setKategori(kategori);
        p.setUkuran(ukuran);
        p.setHarga(harga);
        p.setStok(stok);
        p.setStokMinimum(stokMin);
        return p;
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void tampilkanError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan database:\n" + ex.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
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
        pnlUtama.setPreferredSize(new java.awt.Dimension(1100, 690));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N
        pnlUtama.add(lblLogo);
        lblLogo.setBounds(10, 10, 150, 42);

        lblCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblCari.setText("Cari (Kode/Nama) :");
        pnlUtama.add(lblCari);
        lblCari.setBounds(20, 60, 132, 22);

        txtCari.setBackground(new java.awt.Color(200, 224, 200));
        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtCari);
        txtCari.setBounds(160, 60, 280, 27);

        btnCari.setBackground(new java.awt.Color(143, 188, 143));
        btnCari.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari");
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.setOpaque(true);
        pnlUtama.add(btnCari);
        btnCari.setBounds(460, 60, 72, 27);

        tblProduk.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblProduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
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
        tblProduk.setRowHeight(26);
        spProduk.setViewportView(tblProduk);

        pnlUtama.add(spProduk);
        spProduk.setBounds(20, 100, 690, 150);

        lblKode.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblKode.setText("Kode");
        pnlUtama.add(lblKode);
        lblKode.setBounds(50, 260, 35, 22);

        txtKode.setBackground(new java.awt.Color(200, 224, 200));
        txtKode.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtKode);
        txtKode.setBounds(170, 260, 170, 27);

        lblKategori.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblKategori.setText("Kategori");
        pnlUtama.add(lblKategori);
        lblKategori.setBounds(50, 300, 58, 22);

        cmbKategori.setBackground(new java.awt.Color(200, 224, 200));
        cmbKategori.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKategoriActionPerformed(evt);
            }
        });
        pnlUtama.add(cmbKategori);
        cmbKategori.setBounds(170, 300, 170, 27);

        lblHarga.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblHarga.setText("Harga");
        pnlUtama.add(lblHarga);
        lblHarga.setBounds(50, 340, 42, 22);

        txtHarga.setBackground(new java.awt.Color(200, 224, 200));
        txtHarga.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaActionPerformed(evt);
            }
        });
        pnlUtama.add(txtHarga);
        txtHarga.setBounds(170, 340, 170, 27);

        lblStokMin.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblStokMin.setText("Stok Minimum");
        pnlUtama.add(lblStokMin);
        lblStokMin.setBounds(50, 380, 103, 22);

        txtStokMin.setBackground(new java.awt.Color(200, 224, 200));
        txtStokMin.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtStokMin);
        txtStokMin.setBounds(170, 380, 170, 27);

        lblNama.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblNama.setText("Nama");
        pnlUtama.add(lblNama);
        lblNama.setBounds(380, 260, 42, 22);

        txtNama.setBackground(new java.awt.Color(200, 224, 200));
        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtNama);
        txtNama.setBounds(460, 260, 160, 27);

        lblUkuran.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblUkuran.setText("Ukuran");
        pnlUtama.add(lblUkuran);
        lblUkuran.setBounds(380, 300, 51, 22);

        cmbUkuran.setBackground(new java.awt.Color(200, 224, 200));
        cmbUkuran.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(cmbUkuran);
        cmbUkuran.setBounds(460, 300, 160, 27);

        lblStok.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblStok.setText("Stok");
        pnlUtama.add(lblStok);
        lblStok.setBounds(380, 340, 31, 22);

        txtStok.setBackground(new java.awt.Color(200, 224, 200));
        txtStok.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtStok);
        txtStok.setBounds(460, 340, 160, 27);

        btnTambah.setBackground(new java.awt.Color(60, 179, 113));
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.setBorderPainted(false);
        btnTambah.setFocusPainted(false);
        btnTambah.setOpaque(true);
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        pnlUtama.add(btnTambah);
        btnTambah.setBounds(20, 430, 84, 27);

        btnUbah.setBackground(new java.awt.Color(143, 188, 143));
        btnUbah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUbah.setForeground(new java.awt.Color(255, 255, 255));
        btnUbah.setText("Ubah");
        btnUbah.setBorderPainted(false);
        btnUbah.setFocusPainted(false);
        btnUbah.setOpaque(true);
        pnlUtama.add(btnUbah);
        btnUbah.setBounds(110, 430, 72, 27);

        btnHapus.setBackground(new java.awt.Color(143, 188, 143));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.setBorderPainted(false);
        btnHapus.setFocusPainted(false);
        btnHapus.setOpaque(true);
        pnlUtama.add(btnHapus);
        btnHapus.setBounds(190, 430, 72, 27);

        btnTambahStok.setBackground(new java.awt.Color(143, 188, 143));
        btnTambahStok.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambahStok.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahStok.setText("Tambah Stok");
        btnTambahStok.setBorderPainted(false);
        btnTambahStok.setFocusPainted(false);
        btnTambahStok.setOpaque(true);
        pnlUtama.add(btnTambahStok);
        btnTambahStok.setBounds(270, 430, 118, 27);

        btnBersihkan.setBackground(new java.awt.Color(143, 188, 143));
        btnBersihkan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBersihkan.setForeground(new java.awt.Color(255, 255, 255));
        btnBersihkan.setText("Bersihkan");
        btnBersihkan.setBorderPainted(false);
        btnBersihkan.setFocusPainted(false);
        btnBersihkan.setOpaque(true);
        pnlUtama.add(btnBersihkan);
        btnBersihkan.setBounds(400, 430, 95, 27);

        btnKembali.setBackground(new java.awt.Color(143, 188, 143));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setText("Kembali");
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);
        pnlUtama.add(btnKembali);
        btnKembali.setBounds(600, 430, 85, 27);

        lblKeterangan.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblKeterangan.setText("Baris merah muda = stok menipis (di batas minimum atau kurang)");
        pnlUtama.add(lblKeterangan);
        lblKeterangan.setBounds(20, 460, 385, 18);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahActionPerformed

    private void txtHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaActionPerformed

    private void cmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKategoriActionPerformed

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
