package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.controller.LoginController;
import com.mycompany.helpingtiriz.exception.ValidasiException;

/** VIEW Login: hanya menampilkan dan meneruskan klik ke LoginController. */
public class FormLogin extends javax.swing.JFrame {

    private final LoginController controller = new LoginController();

    public FormLogin() {
        initComponents();
        Tema.terapkan(getContentPane());
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnMasuk);        // Enter = klik Masuk

        btnMasuk.addActionListener(e -> masuk());
        btnKeluar.addActionListener(e -> {
            if (Tema.konfirmasi(this, "Keluar dari aplikasi?")) {
                System.exit(0);
            }
        });
        chkTampilkan.addActionListener(e ->
                pwdPassword.setEchoChar(chkTampilkan.isSelected() ? (char) 0 : '•'));
    }

    private void masuk() {
        try {
            controller.masuk(txtUsername.getText(), new String(pwdPassword.getPassword()));
            new FormMenuUtama().setVisible(true);
            dispose();
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
            pwdPassword.setText("");
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
        chkTampilkan = new javax.swing.JCheckBox();
        btnMasuk = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TIRIZ - Login");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(720, 480));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.png"))); // NOI18N

        pnlUtama.add(lblLogo);
        lblLogo.setBounds(180, 40, 360, 101);

        lblTagline.setFont(new java.awt.Font("Poppins", 0, 16)); // NOI18N
        lblTagline.setForeground(new java.awt.Color(46, 139, 87));
        lblTagline.setText("Your day with peace, love, and TIRIZ"); // NOI18N
        lblTagline.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        pnlUtama.add(lblTagline);
        lblTagline.setBounds(0, 152, 720, 28);

        lblUsername.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(31, 61, 43));
        lblUsername.setText("Username"); // NOI18N

        pnlUtama.add(lblUsername);
        lblUsername.setBounds(150, 222, 110, 36);

        txtUsername.setBackground(new java.awt.Color(255, 255, 255));
        txtUsername.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(31, 61, 43));

        pnlUtama.add(txtUsername);
        txtUsername.setBounds(270, 222, 300, 36);

        lblPassword.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(31, 61, 43));
        lblPassword.setText("Password"); // NOI18N

        pnlUtama.add(lblPassword);
        lblPassword.setBounds(150, 274, 110, 36);

        pwdPassword.setBackground(new java.awt.Color(255, 255, 255));
        pwdPassword.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        pwdPassword.setForeground(new java.awt.Color(31, 61, 43));

        pnlUtama.add(pwdPassword);
        pwdPassword.setBounds(270, 274, 300, 36);

        chkTampilkan.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        chkTampilkan.setForeground(new java.awt.Color(31, 61, 43));
        chkTampilkan.setText("Tampilkan password"); // NOI18N
        chkTampilkan.setFocusPainted(false);

        pnlUtama.add(chkTampilkan);
        chkTampilkan.setBounds(270, 320, 220, 24);

        btnMasuk.setBackground(new java.awt.Color(46, 139, 87));
        btnMasuk.setFont(new java.awt.Font("Poppins Medium", 0, 15)); // NOI18N
        btnMasuk.setForeground(new java.awt.Color(255, 255, 255));
        btnMasuk.setText("Masuk"); // NOI18N
        btnMasuk.setBorderPainted(false);
        btnMasuk.setFocusPainted(false);
        btnMasuk.setOpaque(true);

        pnlUtama.add(btnMasuk);
        btnMasuk.setBounds(270, 368, 144, 44);

        btnKeluar.setBackground(new java.awt.Color(207, 229, 207));
        btnKeluar.setFont(new java.awt.Font("Poppins Medium", 0, 15)); // NOI18N
        btnKeluar.setForeground(new java.awt.Color(46, 139, 87));
        btnKeluar.setText("Keluar"); // NOI18N
        btnKeluar.setBorderPainted(false);
        btnKeluar.setFocusPainted(false);
        btnKeluar.setOpaque(true);

        pnlUtama.add(btnKeluar);
        btnKeluar.setBounds(426, 368, 144, 44);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnMasuk;
    private javax.swing.JCheckBox chkTampilkan;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTagline;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlUtama;
    private javax.swing.JPasswordField pwdPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
