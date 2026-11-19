package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.model.Sesi;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FormMenuUtama extends javax.swing.JFrame {

    public FormMenuUtama() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Tampilan berbeda untuk admin dan kasir
        if (Sesi.isAdmin()) {
            lblSambutan.setText("Welcome, Admin!");
        } else {
            lblSambutan.setText("Happy Purchasing!");
            btnDataBarang.setVisible(false);           // kasir tidak boleh kelola data barang
            rapatkanTombol();                          // tombol di bawahnya naik mengisi tempat kosong
        }

        btnDataBarang.addActionListener(e -> {
            if (Sesi.isAdmin()) {                      // lapis keamanan kedua
                pindahKe(new FormProduk());
            }
        });
        btnKasir.addActionListener(e -> pindahKe(new FormKasir()));
        btnRiwayat.addActionListener(e -> pindahKe(new FormRiwayat()));
        btnLogout.addActionListener(e -> logout());
    }

    /** Menyusun ulang tombol yang terlihat dari atas ke bawah dengan jarak yang sama. */
    private void rapatkanTombol() {
        JButton[] urutan = {btnDataBarang, btnKasir, btnRiwayat, btnLogout};
        int langkah = btnKasir.getY() - btnDataBarang.getY();
        int y = btnDataBarang.getY();
        for (JButton b : urutan) {
            if (b.isVisible()) {
                b.setLocation(b.getX(), y);
                y += langkah;
            }
        }
    }

    /** Buka frame tujuan lalu tutup menu ini. */
    private void pindahKe(JFrame tujuan) {
        tujuan.setVisible(true);
        dispose();
    }

    private void logout() {
        int pilihan = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            Sesi.keluar();
            new FormLogin().setVisible(true);
            dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlUtama = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblSambutan = new javax.swing.JLabel();
        btnDataBarang = new javax.swing.JButton();
        btnKasir = new javax.swing.JButton();
        btnRiwayat = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TIRIZ - Menu Utama");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(800, 640));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.png"))); // NOI18N
        pnlUtama.add(lblLogo);
        lblLogo.setBounds(20, 10, 360, 101);

        lblSambutan.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        lblSambutan.setForeground(new java.awt.Color(46, 139, 87));
        lblSambutan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSambutan.setText("Welcome, Admin!");
        pnlUtama.add(lblSambutan);
        lblSambutan.setBounds(90, 110, 200, 30);

        btnDataBarang.setBackground(new java.awt.Color(143, 188, 143));
        btnDataBarang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDataBarang.setForeground(new java.awt.Color(255, 255, 255));
        btnDataBarang.setText("Data Barang");
        btnDataBarang.setBorderPainted(false);
        btnDataBarang.setFocusPainted(false);
        btnDataBarang.setOpaque(true);
        btnDataBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataBarangActionPerformed(evt);
            }
        });
        pnlUtama.add(btnDataBarang);
        btnDataBarang.setBounds(110, 160, 160, 30);

        btnKasir.setBackground(new java.awt.Color(143, 188, 143));
        btnKasir.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnKasir.setForeground(new java.awt.Color(255, 255, 255));
        btnKasir.setText("Kasir");
        btnKasir.setBorderPainted(false);
        btnKasir.setFocusPainted(false);
        btnKasir.setOpaque(true);
        pnlUtama.add(btnKasir);
        btnKasir.setBounds(40, 250, 80, 30);

        btnRiwayat.setBackground(new java.awt.Color(143, 188, 143));
        btnRiwayat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRiwayat.setForeground(new java.awt.Color(255, 255, 255));
        btnRiwayat.setText("Riwayat Transaksi");
        btnRiwayat.setBorderPainted(false);
        btnRiwayat.setFocusPainted(false);
        btnRiwayat.setOpaque(true);
        pnlUtama.add(btnRiwayat);
        btnRiwayat.setBounds(110, 200, 160, 30);

        btnLogout.setBackground(new java.awt.Color(143, 188, 143));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        pnlUtama.add(btnLogout);
        btnLogout.setBounds(270, 250, 80, 30);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnDataBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDataBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDataBarangActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDataBarang;
    private javax.swing.JButton btnKasir;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSambutan;
    private javax.swing.JPanel pnlUtama;
    // End of variables declaration//GEN-END:variables
}
