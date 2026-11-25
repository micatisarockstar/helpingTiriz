package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.model.DetailTableModel;
import com.mycompany.helpingtiriz.model.TransaksiTableModel;
import com.mycompany.helpingtiriz.repository.TransaksiDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

public class FormRiwayat extends javax.swing.JFrame {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final TransaksiTableModel transaksiModel = new TransaksiTableModel();
    private final DetailTableModel detailModel = new DetailTableModel();

    public FormRiwayat() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);   // tombol X ditangani di bawah

        tblTransaksi.setModel(transaksiModel);
        tblTransaksi.setDefaultRenderer(Object.class, new BarisRenderer());   // baris kuning = Belum
        tblTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDetail.setModel(detailModel);
        BarisRenderer.aturLebarKolom(tblTransaksi, 50, 160, 170, 110, 130, 130, 130, 160);
        BarisRenderer.aturLebarKolom(tblDetail, 330, 80, 160, 160, 310);

        cmbFilter.setModel(new DefaultComboBoxModel<>(new String[]{"Semua", "Belum", "Selesai"}));
        btnTandaiSelesai.setEnabled(false);

        cmbFilter.addActionListener(e -> muatTransaksi());
        tblTransaksi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                muatDetail();
            }
        });
        btnTandaiSelesai.addActionListener(e -> tandaiSelesai());
        btnKembali.addActionListener(e -> kembali());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                kembali();
            }
        });

        muatTransaksi();
    }

    private void muatTransaksi() {
        try {
            transaksiModel.setData(transaksiDAO.ambilSemua((String) cmbFilter.getSelectedItem()));
            detailModel.setData(new ArrayList<>());      // kosongkan detail
            btnTandaiSelesai.setEnabled(false);
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    /** Dipanggil saat user mengklik satu baris transaksi. */
    private void muatDetail() {
        Transaksi t = transaksiTerpilih();
        if (t == null) {
            btnTandaiSelesai.setEnabled(false);
            return;
        }
        try {
            detailModel.setData(transaksiDAO.ambilDetail(t.getIdTransaksi()));
            btnTandaiSelesai.setEnabled(!t.isSelesai());   // hanya aktif untuk yang masih Belum
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private void tandaiSelesai() {
        Transaksi t = transaksiTerpilih();
        if (t == null || t.isSelesai()) {
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Pesanan sudah diserahkan ke pelanggan?\n(Transaksi: " + t.getNamaKasir() + ", "
                        + transaksiModel.getValueAt(tblTransaksi.convertRowIndexToModel(tblTransaksi.getSelectedRow()), 1) + ")",
                "Tandai Selesai", JOptionPane.YES_NO_OPTION);
        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (transaksiDAO.tandaiSelesai(t.getIdTransaksi())) {
                muatTransaksi();                         // muat ulang supaya status & warna berubah
            } else {
                JOptionPane.showMessageDialog(this, "Pesanan ini sudah berstatus Selesai.");
                muatTransaksi();
            }
        } catch (SQLException ex) {
            tampilkanError(ex);
        }
    }

    private Transaksi transaksiTerpilih() {
        int baris = tblTransaksi.getSelectedRow();
        if (baris < 0) {
            return null;
        }
        return transaksiModel.getAt(tblTransaksi.convertRowIndexToModel(baris));
    }

    private void kembali() {
        new FormMenuUtama().setVisible(true);
        dispose();
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
        lblDaftar = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();
        spTransaksi = new javax.swing.JScrollPane();
        tblTransaksi = new javax.swing.JTable();
        lblDetail = new javax.swing.JLabel();
        spDetail = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();
        btnTandaiSelesai = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TIRIZ - Riwayat Transaksi");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(1100, 700));

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N

        lblDaftar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblDaftar.setText("Daftar Transaksi");

        lblFilter.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblFilter.setText("Status:");

        cmbFilter.setBackground(new java.awt.Color(200, 224, 200));
        cmbFilter.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cmbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterActionPerformed(evt);
            }
        });

        tblTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Tanggal", "Kasir", "Metode", "Total", "Bayar", "Kembalian", "Status"
            }
        ));
        tblTransaksi.setRowHeight(26);
        spTransaksi.setViewportView(tblTransaksi);

        lblDetail.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblDetail.setText("Detail Barang");

        tblDetail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Barang", "Jumlah", "Harga Satuan", "Subtotal", "Catatan"
            }
        ));
        tblDetail.setRowHeight(26);
        spDetail.setViewportView(tblDetail);

        btnTandaiSelesai.setBackground(new java.awt.Color(60, 179, 113));
        btnTandaiSelesai.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnTandaiSelesai.setForeground(new java.awt.Color(255, 255, 255));
        btnTandaiSelesai.setText("Tandai Selesai");
        btnTandaiSelesai.setBorderPainted(false);
        btnTandaiSelesai.setFocusPainted(false);
        btnTandaiSelesai.setOpaque(true);
        btnTandaiSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTandaiSelesaiActionPerformed(evt);
            }
        });

        btnKembali.setBackground(new java.awt.Color(143, 188, 143));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setText("Kembali");
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);
        btnKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlUtamaLayout = new javax.swing.GroupLayout(pnlUtama);
        pnlUtama.setLayout(pnlUtamaLayout);
        pnlUtamaLayout.setHorizontalGroup(
            pnlUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblLogo))
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblDaftar)
                .addGap(331, 331, 331)
                .addComponent(lblFilter)
                .addGap(4, 4, 4)
                .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(spTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblDetail))
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(spDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnTandaiSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(390, 390, 390)
                .addComponent(btnKembali))
        );
        pnlUtamaLayout.setVerticalGroup(
            pnlUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUtamaLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblLogo)
                .addGap(8, 8, 8)
                .addGroup(pnlUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDaftar)
                    .addComponent(lblFilter)
                    .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(spTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblDetail)
                .addGap(5, 5, 5)
                .addComponent(spDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(pnlUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTandaiSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKembali)))
        );

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbFilterActionPerformed

    private void btnTandaiSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTandaiSelesaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTandaiSelesaiActionPerformed

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKembaliActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnTandaiSelesai;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JLabel lblDaftar;
    private javax.swing.JLabel lblDetail;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JPanel pnlUtama;
    private javax.swing.JScrollPane spDetail;
    private javax.swing.JScrollPane spTransaksi;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTable tblTransaksi;
    // End of variables declaration//GEN-END:variables
}
