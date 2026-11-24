package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.model.FormatRupiah;
import com.mycompany.helpingtiriz.model.KeranjangTableModel;
import com.mycompany.helpingtiriz.model.ProdukTableModel;
import com.mycompany.helpingtiriz.model.Sesi;
import com.mycompany.helpingtiriz.repository.ProdukDAO;
import com.mycompany.helpingtiriz.repository.StokTidakCukupException;
import com.mycompany.helpingtiriz.repository.TransaksiDAO;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FormKasir extends javax.swing.JFrame {

    private static final String TANPA_TOPPING = "(Tanpa topping)";

    private final ProdukDAO produkDAO = new ProdukDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final ProdukTableModel produkModel = new ProdukTableModel();
    private final KeranjangTableModel keranjangModel = new KeranjangTableModel();

    public FormKasir() {
        initComponents();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);   // tombol X ditangani di bawah

        tblProduk.setModel(produkModel);
        tblProduk.setDefaultRenderer(Object.class, new BarisRenderer());
        tblProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKeranjang.setModel(keranjangModel);
        tblKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        BarisRenderer.aturLebarKolom(tblProduk, 30, 95, 140, 65, 50, 80, 40, 55, 65);
        BarisRenderer.aturLebarKolom(tblKeranjang, 170, 55, 85, 90, 170);

        spnJumlah.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        cmbMetode.setModel(new DefaultComboBoxModel<>(new String[]{"Tunai", "QRIS", "Transfer"}));
        txtTotal.setEditable(false);
        txtKembalian.setEditable(false);
        cmbTopping1.setVisible(false);                   // Diperbaiki dari cmbToppin1

        pasangEvent();
        muatTopping();
        muatProduk("");
        aturTopping();
        perbaruiTotal();
    }

    private void pasangEvent() {
        btnCari.addActionListener(e -> muatProduk(txtCari.getText().trim()));
        txtCari.addActionListener(e -> muatProduk(txtCari.getText().trim()));
        tblProduk.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                aturTopping();
            }
        });

        btnTambahTopping.addActionListener(e -> {
            cmbTopping1.setVisible(true);
            btnTambahTopping.setEnabled(false);          // maksimal 2 topping gratis
        });
        btnTambahKeranjang.addActionListener(e -> tambahKeKeranjang());
        btnHapusItem.addActionListener(e -> hapusItem());
        btnKosongkan.addActionListener(e -> kosongkanKeranjang());

        cmbMetode.addActionListener(e -> aturPembayaran());
        txtUangBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override public void removeUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override public void changedUpdate(DocumentEvent e) { hitungKembalian(); }
        });

        btnCetak.addActionListener(e -> cetakTransaksi());
        btnKembali.addActionListener(e -> kembali());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                kembali();
            }
        });
    }

    // ---------------- keranjang ----------------

   private void tambahKeKeranjang() {
        Produk p = produkTerpilih();
        if (p == null) {
            peringatan("Pilih barang di tabel terlebih dahulu.");
            return;
        }
        int jumlah = (Integer) spnJumlah.getValue();

        // Validasi stok: jumlah di keranjang + jumlah baru tidak boleh melebihi stok
        int sudahAda = keranjangModel.jumlahDiKeranjang(p.getIdProduk());
        if (sudahAda + jumlah > p.getStok()) {
            String pesan = "Stok " + p.getNamaTampil() + " tidak cukup.\nSisa stok: " + p.getStok();
            if (sudahAda > 0) {
                pesan += " (" + sudahAda + " sudah ada di keranjang)";
            }
            JOptionPane.showMessageDialog(this, pesan, "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
            return;                                      // barang TIDAK masuk keranjang
        }

        // Ambil 1 topping saja (pastikan hanya menggunakan cmbTopping1)
        String topping = (cmbTopping1.isVisible() && cmbTopping1.getSelectedIndex() > 0)
                ? (String) cmbTopping1.getSelectedItem() : null;

        // Catatan = "Topping: Caramel Crumble | catatan bebas"
        List<String> bagian = new ArrayList<>();
        if (topping != null) {
            bagian.add("Topping: " + topping);
        }
        if (!txtCatatan.getText().trim().isEmpty()) {
            bagian.add(txtCatatan.getText().trim());
        }
        String catatan = String.join(" | ", bagian);
        if (catatan.length() > 200) {
            catatan = catatan.substring(0, 200);         // batas kolom di database
        }

        DetailTransaksi d = new DetailTransaksi();
        d.setProduk(p);
        d.setJumlah(jumlah);
        d.setHargaSatuan(p.getHarga());
        d.setCatatan(catatan);
        d.hitungSubtotal();
        keranjangModel.tambah(d);

        perbaruiTotal();
        resetInputBarang();
    }
   
   private void hapusItem() {
        int baris = tblKeranjang.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih baris di keranjang yang akan dihapus.");
            return;
        }
        keranjangModel.hapus(tblKeranjang.convertRowIndexToModel(baris));
        perbaruiTotal();
    }

    private void kosongkanKeranjang() {
        if (keranjangModel.getRowCount() == 0) {
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this, "Kosongkan seluruh keranjang?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            keranjangModel.kosongkan();
            perbaruiTotal();
        }
    }

    // ---------------- pembayaran ----------------

    private void cetakTransaksi() {
        if (keranjangModel.getRowCount() == 0) {
            peringatan("Keranjang masih kosong.");
            return;
        }

        BigDecimal total = keranjangModel.hitungTotal();
        String metode = (String) cmbMetode.getSelectedItem();
        BigDecimal bayar;
        if ("Tunai".equals(metode)) {
            try {
                bayar = FormatRupiah.parse(txtUangBayar.getText());
            } catch (NumberFormatException ex) {
                peringatan("Uang bayar harus diisi dengan angka.");
                return;
            }
            if (bayar.compareTo(total) < 0) {
                peringatan("Uang bayar kurang " + FormatRupiah.format(total.subtract(bayar)) + ".");
                return;
            }
        } else {
            bayar = total;                               // QRIS / Transfer: pas
        }

        Transaksi t = new Transaksi();
        t.setIdPengguna(Sesi.getPenggunaAktif().getIdPengguna());
        t.setMetodePembayaran(metode);
        t.setDaftarDetail(new ArrayList<>(keranjangModel.getItems()));
        t.setTotal(t.hitungTotal());
        t.setBayar(bayar);
        t.setKembalian(t.hitungKembalian());

        try {
            int idTransaksi = transaksiDAO.simpanTransaksi(t);
            t.setIdTransaksi(idTransaksi);
            tampilkanStruk(t);

            keranjangModel.kosongkan();
            txtUangBayar.setText("");
            perbaruiTotal();
            muatProduk(txtCari.getText().trim());        // supaya stok terbaru kelaiatan
        } catch (StokTidakCukupException ex) {
            peringatan(ex.getMessage() + "\nTransaksi dibatalkan, stok sudah berubah. Cek ulang keranjang.");
            muatProduk(txtCari.getText().trim());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi:\n" + ex.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanStruk(Transaksi t) {
        String garis = "------------------------------------------\n";
        StringBuilder sb = new StringBuilder();
        sb.append("                 TIRIZ\n");
        sb.append("  Your day with peace, love, and TIRIZ\n");
        sb.append(garis);
        sb.append("No Transaksi : ").append(t.getIdTransaksi()).append('\n');
        sb.append("Tanggal      : ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append('\n');
        sb.append("Kasir        : ").append(Sesi.getPenggunaAktif().getNamaLengkap()).append('\n');
        sb.append("Metode       : ").append(t.getMetodePembayaran()).append('\n');
        sb.append(garis);
        for (DetailTransaksi d : t.getDaftarDetail()) {
            sb.append(d.getProduk().getNamaTampil()).append('\n');
            sb.append("  ").append(d.getJumlah()).append(" x ")
                    .append(FormatRupiah.format(d.getHargaSatuan())).append(" = ")
                    .append(FormatRupiah.format(d.getSubtotal())).append('\n');
            if (d.getCatatan() != null && !d.getCatatan().isEmpty()) {
                sb.append("  * ").append(d.getCatatan()).append('\n');
            }
        }
        sb.append(garis);
        sb.append(String.format("%-12s: %s%n", "Total", FormatRupiah.format(t.getTotal())));
        sb.append(String.format("%-12s: %s%n", "Bayar", FormatRupiah.format(t.getBayar())));
        sb.append(String.format("%-12s: %s%n", "Kembalian", FormatRupiah.format(t.getKembalian())));
        sb.append(garis);
        sb.append("Status pesanan: Belum diserahkan\n");
        sb.append("(ubah jadi Selesai di menu Riwayat Transaksi)\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, area, "Struk Transaksi", JOptionPane.PLAIN_MESSAGE);
    }

    private void aturPembayaran() {
        boolean tunai = "Tunai".equals(cmbMetode.getSelectedItem());
        txtUangBayar.setEnabled(tunai);
        if (tunai) {
            txtUangBayar.setText("");
        } else {
            txtUangBayar.setText(keranjangModel.hitungTotal().toBigInteger().toString());
        }
    }

    private void perbaruiTotal() {
        BigDecimal total = keranjangModel.hitungTotal();
        txtTotal.setText(FormatRupiah.format(total));
        if (!"Tunai".equals(cmbMetode.getSelectedItem())) {
            txtUangBayar.setText(total.toBigInteger().toString());
        }
        hitungKembalian();
    }

    private void hitungKembalian() {
        try {
            BigDecimal selisih = FormatRupiah.parse(txtUangBayar.getText()).subtract(keranjangModel.hitungTotal());
            if (selisih.signum() >= 0) {
                txtKembalian.setText(FormatRupiah.format(selisih));
            } else {
                txtKembalian.setText("Kurang " + FormatRupiah.format(selisih.negate()));
            }
        } catch (NumberFormatException ex) {
            txtKembalian.setText("");                    // belum ada angka
        }
    }

    // ---------------- helper ----------------

    private void muatProduk(String keyword) {
        try {
            produkModel.setData(produkDAO.ambilUntukJual(keyword));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat produk:\n" + ex.getMessage(),
                    "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatTopping() {
    try {
        List<String> nama = new ArrayList<>();
        nama.add(TANPA_TOPPING);
        for (Produk p : produkDAO.ambilTopping()) {
            nama.add(p.getNamaProduk());
        }
        cmbTopping1.setModel(new DefaultComboBoxModel<>(nama.toArray(new String[0])));
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Gagal memuat topping:\n" + ex.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
    }
}

    private void aturTopping() {
    Produk p = produkTerpilih();
    boolean minuman = p != null && "Minuman".equals(p.getKategori());
    cmbTopping1.setEnabled(minuman);
    btnTambahTopping.setEnabled(minuman && !cmbTopping1.isVisible());
    if (!minuman) {
        cmbTopping1.setSelectedIndex(0);
        cmbTopping1.setVisible(false);
    }
}

private void resetInputBarang() {
    spnJumlah.setValue(1);
    cmbTopping1.setSelectedIndex(0);
    cmbTopping1.setVisible(false);
    txtCatatan.setText("");
    aturTopping();
}

    private Produk produkTerpilih() {
        int baris = tblProduk.getSelectedRow();
        if (baris < 0) {
            return null;
        }
        return produkModel.getAt(tblProduk.convertRowIndexToModel(baris));
    }

    private void kembali() {
        if (keranjangModel.getRowCount() > 0) {
            int pilihan = JOptionPane.showConfirmDialog(this,
                    "Keranjang belum disimpan dan akan hilang. Tetap kembali?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (pilihan != JOptionPane.YES_OPTION) {
                return;
            }
        }
        new FormMenuUtama().setVisible(true);
        dispose();
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
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
        lblJumlah = new javax.swing.JLabel();
        spnJumlah = new javax.swing.JSpinner();
        lblTopping = new javax.swing.JLabel();
        cmbTopping1 = new javax.swing.JComboBox<>();
        btnTambahTopping = new javax.swing.JButton();
        lblCatatan = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        btnTambahKeranjang = new javax.swing.JButton();
        pnlKeranjang = new javax.swing.JPanel();
        spKeranjang = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        btnHapusItem = new javax.swing.JButton();
        btnKosongkan = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        lblMetode = new javax.swing.JLabel();
        cmbMetode = new javax.swing.JComboBox<>();
        lblUangBayar = new javax.swing.JLabel();
        txtUangBayar = new javax.swing.JTextField();
        lblKembalian = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JTextField();
        btnCetak = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TIRIZ - Kasir");
        setResizable(false);

        pnlUtama.setBackground(new java.awt.Color(255, 255, 255));
        pnlUtama.setPreferredSize(new java.awt.Dimension(1280, 680));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N
        pnlUtama.add(lblLogo);
        lblLogo.setBounds(10, 10, 150, 42);

        lblCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblCari.setText("Cari (Kode/Nama) :");
        pnlUtama.add(lblCari);
        lblCari.setBounds(10, 60, 132, 22);

        txtCari.setBackground(new java.awt.Color(200, 224, 200));
        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        pnlUtama.add(txtCari);
        txtCari.setBounds(150, 60, 390, 27);

        btnCari.setBackground(new java.awt.Color(143, 188, 143));
        btnCari.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari");
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.setOpaque(true);
        pnlUtama.add(btnCari);
        btnCari.setBounds(560, 60, 72, 27);

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
        spProduk.setBounds(10, 100, 650, 140);

        lblJumlah.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblJumlah.setText("Jumlah:");
        pnlUtama.add(lblJumlah);
        lblJumlah.setBounds(10, 250, 53, 22);

        spnJumlah.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(spnJumlah);
        spnJumlah.setBounds(70, 250, 64, 27);

        lblTopping.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblTopping.setText("Topping:");
        pnlUtama.add(lblTopping);
        lblTopping.setBounds(160, 250, 60, 22);

        cmbTopping1.setBackground(new java.awt.Color(200, 224, 200));
        cmbTopping1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(cmbTopping1);
        cmbTopping1.setBounds(230, 250, 320, 27);

        btnTambahTopping.setBackground(new java.awt.Color(143, 188, 143));
        btnTambahTopping.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambahTopping.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahTopping.setText("+ Topping");
        btnTambahTopping.setBorderPainted(false);
        btnTambahTopping.setFocusPainted(false);
        btnTambahTopping.setOpaque(true);
        pnlUtama.add(btnTambahTopping);
        btnTambahTopping.setBounds(560, 250, 100, 27);

        lblCatatan.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblCatatan.setText("Catatan:");
        pnlUtama.add(lblCatatan);
        lblCatatan.setBounds(10, 290, 56, 22);

        txtCatatan.setBackground(new java.awt.Color(200, 224, 200));
        txtCatatan.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlUtama.add(txtCatatan);
        txtCatatan.setBounds(80, 290, 580, 27);

        btnTambahKeranjang.setBackground(new java.awt.Color(60, 179, 113));
        btnTambahKeranjang.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnTambahKeranjang.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahKeranjang.setText("Tambah Ke Keranjang");
        btnTambahKeranjang.setBorderPainted(false);
        btnTambahKeranjang.setFocusPainted(false);
        btnTambahKeranjang.setOpaque(true);
        pnlUtama.add(btnTambahKeranjang);
        btnTambahKeranjang.setBounds(240, 350, 206, 31);

        pnlKeranjang.setBackground(new java.awt.Color(240, 255, 240));
        pnlKeranjang.setLayout(null);

        tblKeranjang.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nama", "Jumlah", "Harga", "Subtotal", "Catatan"
            }
        ));
        tblKeranjang.setRowHeight(26);
        spKeranjang.setViewportView(tblKeranjang);

        pnlKeranjang.add(spKeranjang);
        spKeranjang.setBounds(0, 0, 452, 402);

        btnHapusItem.setBackground(new java.awt.Color(143, 188, 143));
        btnHapusItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapusItem.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusItem.setBorderPainted(false);
        btnHapusItem.setFocusPainted(false);
        btnHapusItem.setOpaque(true);
        btnHapusItem.setText("Hapus Item");
        pnlKeranjang.add(btnHapusItem);
        btnHapusItem.setBounds(0, 0, 106, 27);

        btnKosongkan.setBackground(new java.awt.Color(143, 188, 143));
        btnKosongkan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnKosongkan.setForeground(new java.awt.Color(255, 255, 255));
        btnKosongkan.setBorderPainted(false);
        btnKosongkan.setFocusPainted(false);
        btnKosongkan.setOpaque(true);
        btnKosongkan.setText("Kosongkan");
        pnlKeranjang.add(btnKosongkan);
        btnKosongkan.setBounds(0, 0, 104, 27);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblTotal.setText("Total");
        pnlKeranjang.add(lblTotal);
        lblTotal.setBounds(0, 0, 34, 22);

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pnlKeranjang.add(txtTotal);
        txtTotal.setBounds(0, 0, 64, 28);

        lblMetode.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblMetode.setText("Metode Pembayaran");
        pnlKeranjang.add(lblMetode);
        lblMetode.setBounds(0, 0, 144, 22);

        cmbMetode.setBackground(new java.awt.Color(200, 224, 200));
        cmbMetode.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        pnlKeranjang.add(cmbMetode);
        cmbMetode.setBounds(0, 0, 72, 27);

        lblUangBayar.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblUangBayar.setText("Uang Bayar");
        pnlKeranjang.add(lblUangBayar);
        lblUangBayar.setBounds(0, 0, 80, 22);

        txtUangBayar.setBackground(new java.awt.Color(200, 224, 200));
        txtUangBayar.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pnlKeranjang.add(txtUangBayar);
        txtUangBayar.setBounds(0, 0, 64, 28);

        lblKembalian.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblKembalian.setText("Kembalian");
        pnlKeranjang.add(lblKembalian);
        lblKembalian.setBounds(0, 0, 73, 22);

        txtKembalian.setEditable(false);
        txtKembalian.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pnlKeranjang.add(txtKembalian);
        txtKembalian.setBounds(0, 0, 64, 28);

        btnCetak.setBackground(new java.awt.Color(60, 179, 113));
        btnCetak.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnCetak.setBorderPainted(false);
        btnCetak.setFocusPainted(false);
        btnCetak.setOpaque(true);
        btnCetak.setText("Cetak Transaksi");
        pnlKeranjang.add(btnCetak);
        btnCetak.setBounds(0, 0, 163, 32);

        btnKembali.setBackground(new java.awt.Color(143, 188, 143));
        btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(255, 255, 255));
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);
        btnKembali.setText("Kembali");
        pnlKeranjang.add(btnKembali);
        btnKembali.setBounds(0, 0, 93, 29);

        pnlUtama.add(pnlKeranjang);
        pnlKeranjang.setBounds(0, 0, 0, 0);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnKosongkan;
    private javax.swing.JButton btnTambahKeranjang;
    private javax.swing.JButton btnTambahTopping;
    private javax.swing.JComboBox<String> cmbMetode;
    private javax.swing.JComboBox<String> cmbTopping1;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMetode;
    private javax.swing.JLabel lblTopping;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUangBayar;
    private javax.swing.JPanel pnlKeranjang;
    private javax.swing.JPanel pnlUtama;
    private javax.swing.JScrollPane spKeranjang;
    private javax.swing.JScrollPane spProduk;
    private javax.swing.JSpinner spnJumlah;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JTable tblProduk;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCatatan;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtUangBayar;
    // End of variables declaration//GEN-END:variables
}
