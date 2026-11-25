package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.entity.Pengguna;
import com.mycompany.helpingtiriz.util.Sesi;
import javax.swing.JButton;
import javax.swing.JFrame;

/** VIEW Menu Utama. Admin melihat 4 tombol, kasir tidak melihat Data Barang. */
public class FormMenuUtama extends javax.swing.JFrame {

    public FormMenuUtama() {
        initComponents();
        Tema.terapkan(getContentPane());

        Pengguna p = Sesi.getPenggunaAktif();
        lblSambutan.setText(Sesi.isAdmin() ? "Welcome, Admin!" : "Happy Purchasing!");
        lblPengguna.setText(p == null ? "" : "Masuk sebagai " + p.getNamaLengkap() + " (" + p.getPeran() + ")");
        if (!Sesi.isAdmin()) {
            btnDataBarang.setVisible(false);             // kasir tidak boleh kelola data barang
            rapatkanTombol();
        }
        pack();
        setLocationRelativeTo(null);

        btnDataBarang.addActionListener(e -> pindahKe(new FormProduk()));
        btnKasir.addActionListener(e -> pindahKe(new FormKasir()));
        btnRiwayat.addActionListener(e -> pindahKe(new FormRiwayat()));
        btnLogout.addActionListener(e -> {
            if (Tema.konfirmasi(this, "Yakin ingin logout?")) {
                Sesi.keluar();
                pindahKe(new FormLogin());
            }
        });
    }

    /** Tombol yang terlihat disusun naik mengisi tempat tombol yang disembunyikan. */
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
        lblPengguna.setLocation(lblPengguna.getX(), lblPengguna.getY() - langkah);
        pnlUtama.setPreferredSize(new java.awt.Dimension(pnlUtama.getPreferredSize().width,
                pnlUtama.getPreferredSize().height - langkah));
    }

    private void pindahKe(JFrame tujuan) {
        tujuan.setVisible(true);
        dispose();
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
        lblPengguna = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TIRIZ - Menu Utama");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(720, 540));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.png"))); // NOI18N

        pnlUtama.add(lblLogo);
        lblLogo.setBounds(180, 40, 360, 101);

        lblSambutan.setFont(new java.awt.Font("Poppins Medium", 0, 20)); // NOI18N
        lblSambutan.setForeground(new java.awt.Color(46, 139, 87));
        lblSambutan.setText("Welcome, Admin!"); // NOI18N
        lblSambutan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        pnlUtama.add(lblSambutan);
        lblSambutan.setBounds(0, 152, 720, 32);

        btnDataBarang.setBackground(new java.awt.Color(46, 139, 87));
        btnDataBarang.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnDataBarang.setForeground(new java.awt.Color(255, 255, 255));
        btnDataBarang.setText("Data Barang"); // NOI18N
        btnDataBarang.setBorderPainted(false);
        btnDataBarang.setFocusPainted(false);
        btnDataBarang.setOpaque(true);

        pnlUtama.add(btnDataBarang);
        btnDataBarang.setBounds(180, 212, 360, 52);

        btnKasir.setBackground(new java.awt.Color(46, 139, 87));
        btnKasir.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnKasir.setForeground(new java.awt.Color(255, 255, 255));
        btnKasir.setText("Kasir"); // NOI18N
        btnKasir.setBorderPainted(false);
        btnKasir.setFocusPainted(false);
        btnKasir.setOpaque(true);

        pnlUtama.add(btnKasir);
        btnKasir.setBounds(180, 280, 360, 52);

        btnRiwayat.setBackground(new java.awt.Color(46, 139, 87));
        btnRiwayat.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnRiwayat.setForeground(new java.awt.Color(255, 255, 255));
        btnRiwayat.setText("Riwayat Transaksi"); // NOI18N
        btnRiwayat.setBorderPainted(false);
        btnRiwayat.setFocusPainted(false);
        btnRiwayat.setOpaque(true);

        pnlUtama.add(btnRiwayat);
        btnRiwayat.setBounds(180, 348, 360, 52);

        btnLogout.setBackground(new java.awt.Color(207, 229, 207));
        btnLogout.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(46, 139, 87));
        btnLogout.setText("Logout"); // NOI18N
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setOpaque(true);

        pnlUtama.add(btnLogout);
        btnLogout.setBounds(180, 416, 360, 52);

        lblPengguna.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblPengguna.setForeground(new java.awt.Color(107, 131, 116));
        lblPengguna.setText("Masuk sebagai"); // NOI18N
        lblPengguna.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        pnlUtama.add(lblPengguna);
        lblPengguna.setBounds(0, 492, 720, 24);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDataBarang;
    private javax.swing.JButton btnKasir;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRiwayat;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPengguna;
    private javax.swing.JLabel lblSambutan;
    private javax.swing.JPanel pnlUtama;
    // End of variables declaration//GEN-END:variables
}
