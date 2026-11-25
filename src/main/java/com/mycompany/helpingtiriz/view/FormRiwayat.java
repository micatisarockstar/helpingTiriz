package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.controller.RiwayatController;
import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.util.FormatRupiah;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** VIEW Riwayat Transaksi: daftar transaksi, detail barang, dan tombol Tandai Selesai. */
public class FormRiwayat extends javax.swing.JFrame {

    private final RiwayatController controller = new RiwayatController();
    private final DefaultTableModel modelTransaksi = Tema.modelTabel(
            "No", "Tanggal", "Kasir", "Metode", "Total", "Bayar", "Kembalian", "Status");
    private final DefaultTableModel modelDetail = Tema.modelTabel(
            "Barang", "Jumlah", "Harga Satuan", "Subtotal", "Catatan");
    private List<Transaksi> daftar = new ArrayList<>();   // urutannya sama dengan tabel transaksi

    public FormRiwayat() {
        initComponents();
        Tema.terapkan(getContentPane());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        tblTransaksi.setModel(modelTransaksi);
        tblTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDetail.setModel(modelDetail);
        Tema.lebarKolom(tblTransaksi, 50, 160, 170, 110, 130, 130, 130, 160);
        Tema.lebarKolom(tblDetail, 300, 80, 150, 150, 330);
        Tema.bungkusKolom(tblDetail, 4);                   // catatan panjang turun ke bawah
        Tema.sorotBaris(tblTransaksi, 7, Transaksi.STATUS_BELUM, new Color(0xFFF3CD));   // kuning = belum diserahkan

        cmbFilter.setModel(new DefaultComboBoxModel<>(RiwayatController.FILTER));
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
            daftar = controller.muat((String) cmbFilter.getSelectedItem());
        } catch (ValidasiException ex) {
            Tema.error(this, ex.getMessage());
            return;
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        modelTransaksi.setRowCount(0);
        modelDetail.setRowCount(0);
        btnTandaiSelesai.setEnabled(false);
        int no = 1;
        for (Transaksi t : daftar) {
            modelTransaksi.addRow(new Object[]{no++, t.getTanggal().format(format), t.getNamaKasir(),
                t.getMetodePembayaran(), FormatRupiah.format(t.getTotal()), FormatRupiah.format(t.getBayar()),
                FormatRupiah.format(t.getKembalian()), t.getStatusPesanan()});
        }
    }

    /** Dipanggil saat user mengklik satu baris transaksi. */
    private void muatDetail() {
        Transaksi t = transaksiTerpilih();
        if (t == null) {
            return;
        }
        try {
            List<DetailTransaksi> detail = controller.ambilDetail(t);
            modelDetail.setRowCount(0);
            for (DetailTransaksi d : detail) {
                modelDetail.addRow(new Object[]{d.getProduk().getNamaTampil(), d.getJumlah(),
                    FormatRupiah.format(d.getHargaSatuan()), FormatRupiah.format(d.getSubtotal()),
                    d.getCatatan() == null ? "" : d.getCatatan()});
            }
            Tema.sesuaikanTinggi(tblDetail, 4);
            btnTandaiSelesai.setEnabled(!t.isSelesai());
        } catch (ValidasiException ex) {
            Tema.error(this, ex.getMessage());
        }
    }

    private void tandaiSelesai() {
        Transaksi t = transaksiTerpilih();
        if (t == null) {
            Tema.peringatan(this, "Pilih transaksi yang akan ditandai selesai.");
            return;
        }
        if (!Tema.konfirmasi(this, "Pesanan sudah diserahkan ke pelanggan?\n(Kasir: " + t.getNamaKasir() + ")")) {
            return;
        }
        try {
            controller.tandaiSelesai(t);
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
        muatTransaksi();                                  // muat ulang supaya status dan warna berubah
    }

    private Transaksi transaksiTerpilih() {
        int baris = tblTransaksi.getSelectedRow();
        return baris < 0 ? null : daftar.get(baris);
    }

    private void kembali() {
        new FormMenuUtama().setVisible(true);
        dispose();
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
        pnlUtama.setPreferredSize(new java.awt.Dimension(1100, 660));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N

        pnlUtama.add(lblLogo);
        lblLogo.setBounds(24, 16, 150, 42);

        lblDaftar.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblDaftar.setForeground(new java.awt.Color(46, 139, 87));
        lblDaftar.setText("Daftar Transaksi"); // NOI18N

        pnlUtama.add(lblDaftar);
        lblDaftar.setBounds(24, 72, 300, 32);

        lblFilter.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblFilter.setForeground(new java.awt.Color(31, 61, 43));
        lblFilter.setText("Status:"); // NOI18N
        lblFilter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        pnlUtama.add(lblFilter);
        lblFilter.setBounds(760, 70, 110, 36);

        cmbFilter.setBackground(new java.awt.Color(255, 255, 255));
        cmbFilter.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbFilter.setForeground(new java.awt.Color(31, 61, 43));
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Belum", "Selesai" }));

        pnlUtama.add(cmbFilter);
        cmbFilter.setBounds(876, 70, 200, 36);


        tblTransaksi.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        tblTransaksi.setRowHeight(30);
        tblTransaksi.setFillsViewportHeight(true);
        tblTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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

        spTransaksi.setViewportView(tblTransaksi);

        pnlUtama.add(spTransaksi);
        spTransaksi.setBounds(24, 116, 1052, 232);

        lblDetail.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblDetail.setForeground(new java.awt.Color(46, 139, 87));
        lblDetail.setText("Detail Barang"); // NOI18N

        pnlUtama.add(lblDetail);
        lblDetail.setBounds(24, 364, 300, 32);


        tblDetail.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        tblDetail.setRowHeight(30);
        tblDetail.setFillsViewportHeight(true);
        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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

        spDetail.setViewportView(tblDetail);

        pnlUtama.add(spDetail);
        spDetail.setBounds(24, 404, 1052, 168);

        btnTandaiSelesai.setBackground(new java.awt.Color(46, 139, 87));
        btnTandaiSelesai.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnTandaiSelesai.setForeground(new java.awt.Color(255, 255, 255));
        btnTandaiSelesai.setText("Tandai Selesai"); // NOI18N
        btnTandaiSelesai.setBorderPainted(false);
        btnTandaiSelesai.setFocusPainted(false);
        btnTandaiSelesai.setOpaque(true);

        pnlUtama.add(btnTandaiSelesai);
        btnTandaiSelesai.setBounds(24, 592, 200, 44);

        btnKembali.setBackground(new java.awt.Color(207, 229, 207));
        btnKembali.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(46, 139, 87));
        btnKembali.setText("Kembali"); // NOI18N
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);

        pnlUtama.add(btnKembali);
        btnKembali.setBounds(956, 592, 120, 44);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
