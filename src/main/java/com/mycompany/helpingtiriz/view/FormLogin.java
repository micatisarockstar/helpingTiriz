package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.entity.Pengguna;
import com.mycompany.helpingtiriz.model.Sesi;
import com.mycompany.helpingtiriz.repository.PenggunaDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class FormLogin extends javax.swing.JFrame {

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    public FormLogin() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getRootPane().setDefaultButton(btnMasuk);      // tombol Enter = klik Masuk

        btnMasuk.addActionListener(e -> masuk());
        btnKeluar.addActionListener(e -> keluar());
    }

    private void masuk() {
        String username = txtUsername.getText().trim();
        String password = new String(pwdPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Pengguna pengguna = penggunaDAO.login(username, password);
            if (pengguna == null) {
                JOptionPane.showMessageDialog(this, "Username atau password salah.",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
                pwdPassword.setText("");
                pwdPassword.requestFocus();
                return;
            }
            Sesi.setPenggunaAktif(pengguna);           // simpan siapa yang login
            new FormMenuUtama().setVisible(true);      // admin & kasir sama-sama ke menu utama
            dispose();                                 // tutup form login
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Tidak bisa terhubung ke database.\nPastikan MySQL di Laragon sudah menyala.\n\n" + ex.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void keluar() {
        int pilihan = JOptionPane.showConfirmDialog(this, "Keluar dari aplikasi?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlUtama = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTagline = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        pwdPassword = new javax.swing.JPasswordField();
        btnMasuk = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TIRIZ - Login");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(800, 560));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N
        pnlUtama.add(lblLogo);
        lblLogo.setBounds(120, 0, 360, 101);

        lblTagline.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lblTagline.setForeground(new java.awt.Color(46, 139, 87));
        lblTagline.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTagline.setText("Your day with peace, love, and TIRIZ");
        pnlUtama.add(lblTagline);
        lblTagline.setBounds(20, 80, 351, 20);

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblUsername.setText("Username");
        pnlUtama.add(lblUsername);
        lblUsername.setBounds(10, 130, 74, 24);

        txtUsername.setBackground(new java.awt.Color(200, 224, 200));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pnlUtama.add(txtUsername);
        txtUsername.setBounds(140, 130, 220, 28);

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        lblPassword.setText("Password");
        pnlUtama.add(lblPassword);
        lblPassword.setBounds(10, 160, 90, 24);

        pwdPassword.setBackground(new java.awt.Color(200, 224, 200));
        pwdPassword.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pnlUtama.add(pwdPassword);
        pwdPassword.setBounds(140, 160, 220, 28);

        btnMasuk.setBackground(new java.awt.Color(60, 179, 113));
        btnMasuk.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnMasuk.setForeground(new java.awt.Color(255, 255, 255));
        btnMasuk.setText("Masuk");
        btnMasuk.setBorderPainted(false);
        btnMasuk.setFocusPainted(false);
        btnMasuk.setOpaque(true);
        btnMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMasukActionPerformed(evt);
            }
        });
        pnlUtama.add(btnMasuk);
        btnMasuk.setBounds(150, 220, 80, 29);

        btnKeluar.setBackground(new java.awt.Color(143, 188, 143));
        btnKeluar.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnKeluar.setText("Keluar");
        btnKeluar.setBorderPainted(false);
        btnKeluar.setFocusPainted(false);
        btnKeluar.setOpaque(true);
        pnlUtama.add(btnKeluar);
        btnKeluar.setBounds(150, 260, 79, 29);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMasukActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnMasuk;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTagline;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlUtama;
    private javax.swing.JPasswordField pwdPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
