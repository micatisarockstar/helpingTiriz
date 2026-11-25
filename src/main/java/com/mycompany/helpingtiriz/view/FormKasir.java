package com.mycompany.helpingtiriz.view;

import com.mycompany.helpingtiriz.controller.KasirController;
import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Produk;
import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.util.FormatRupiah;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** VIEW Kasir: aturan stok, topping, dan pembayaran ada di KasirController; form hanya menampilkan. */
public class FormKasir extends javax.swing.JFrame {

    private static final String TANPA_TOPPING = "(Tanpa topping)";

    private final KasirController controller = new KasirController();
    private final DefaultTableModel modelProduk = Tema.modelTabel(
            "ID", "Kode", "Nama", "Kategori", "Ukuran", "Harga", "Stok", "Stok Min", "Status");
    private final DefaultTableModel modelKeranjang = Tema.modelTabel(
            "Nama", "Jumlah", "Harga", "Subtotal", "Catatan");
    private List<Produk> daftarProduk = new ArrayList<>();   // urutannya sama dengan tabel produk

    public FormKasir() {
        initComponents();
        Tema.terapkan(getContentPane());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        tblProduk.setModel(modelProduk);
        tblProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProduk.setFont(tblProduk.getFont().deriveFont(12f));
        Tema.lebarKolom(tblProduk, 28, 84, 120, 70, 50, 76, 38, 56, 58);
        Tema.sorotBaris(tblProduk, 8, "Menipis", new Color(0xFFD6DC));
        tblKeranjang.setModel(modelKeranjang);
        tblKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.lebarKolom(tblKeranjang, 130, 50, 86, 86, 184);
        Tema.bungkusKolom(tblKeranjang, 4);                // catatan panjang turun ke bawah

        cmbMetode.setModel(new DefaultComboBoxModel<>(KasirController.METODE));
        txtTotal.setEditable(false);
        txtKembalian.setEditable(false);

        btnCari.addActionListener(e -> muatProduk());
        txtCari.addActionListener(e -> muatProduk());
        tblProduk.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                aturTopping();
            }
        });
        cmbTopping1.addActionListener(e -> toppingPertamaBerubah());
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

        muatTopping();
        muatProduk();
        aturTopping();
        aturPembayaran();
        perbaruiKeranjang();
    }

    // ---------------- topping ----------------

    /** Dua kotak topping, kotak ke-2 aktif setelah topping pertama dipilih (topping boleh sama). */
    private void aturTopping() {
        Produk p = produkTerpilih();
        boolean bisa = p != null;                          // minuman dan makanan boleh diberi topping
        if (!bisa) {
            cmbTopping1.setSelectedIndex(0);
            cmbTopping2.setSelectedIndex(0);
        }
        cmbTopping1.setEnabled(bisa);
        cmbTopping2.setEnabled(bisa && cmbTopping1.getSelectedIndex() > 0);
    }

    private void toppingPertamaBerubah() {
        if (cmbTopping1.getSelectedIndex() <= 0) {
            cmbTopping2.setSelectedIndex(0);              // tanpa topping 1, topping 2 ikut kosong
        }
        aturTopping();
    }

    private String pilihanTopping(javax.swing.JComboBox<String> kotak) {
        return kotak.isEnabled() && kotak.getSelectedIndex() > 0 ? (String) kotak.getSelectedItem() : null;
    }

    // ---------------- keranjang ----------------

    private void tambahKeKeranjang() {
        try {
            spnJumlah.commitEdit();                       // pastikan angka yang diketik terbaca
        } catch (ParseException ex) {
            Tema.peringatan(this, "Jumlah harus berupa angka 1 sampai 999.");
            return;
        }
        try {
            controller.tambahKeKeranjang(produkTerpilih(), ((Number) spnJumlah.getValue()).intValue(),
                    pilihanTopping(cmbTopping1), pilihanTopping(cmbTopping2), txtCatatan.getText());
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
            return;
        }
        perbaruiKeranjang();
        spnJumlah.setValue(1);
        cmbTopping1.setSelectedIndex(0);
        cmbTopping2.setSelectedIndex(0);
        txtCatatan.setText("");
        aturTopping();
    }

    private void hapusItem() {
        try {
            controller.hapusItem(tblKeranjang.getSelectedRow());
            perbaruiKeranjang();
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void kosongkanKeranjang() {
        if (!controller.getKeranjang().isEmpty() && Tema.konfirmasi(this, "Kosongkan seluruh keranjang?")) {
            controller.kosongkan();
            perbaruiKeranjang();
        }
    }

    // ---------------- pembayaran ----------------//
    private void cetakTransaksi() {
        try {
            Transaksi t = controller.bayarDanSimpan((String) cmbMetode.getSelectedItem(), txtUangBayar.getText());
            JTextArea struk = new JTextArea(controller.buatStruk(t));
            struk.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            struk.setEditable(false);
            javax.swing.JOptionPane.showMessageDialog(this, struk, "Struk Transaksi",
                    javax.swing.JOptionPane.PLAIN_MESSAGE);
            txtUangBayar.setText("");
            perbaruiKeranjang();
            muatProduk();                                 // supaya stok terbaru kelihatan
        } catch (ValidasiException ex) {
            Tema.peringatan(this, ex.getMessage());
        }
    }

    private void aturPembayaran() {
        boolean tunai = "Tunai".equals(cmbMetode.getSelectedItem());
        txtUangBayar.setEnabled(tunai);
        txtUangBayar.setText(tunai ? "" : controller.total().toBigInteger().toString());
        hitungKembalian();
    }

    private void hitungKembalian() {
        BigDecimal selisih = controller.selisihBayar(txtUangBayar.getText());
        if (selisih == null) {
            txtKembalian.setText("");
        } else if (selisih.signum() >= 0) {
            txtKembalian.setText(FormatRupiah.format(selisih));
        } else {
            txtKembalian.setText("Kurang " + FormatRupiah.format(selisih.negate()));
        }
    }

    // ---------------- helper ---------------- //

    /** gambar ulang tabel keranjang dan total dari isi keranjang di controller. */
    private void perbaruiKeranjang() {
        modelKeranjang.setRowCount(0);
        for (DetailTransaksi d : controller.getKeranjang()) {
            modelKeranjang.addRow(new Object[]{d.getProduk().getNamaTampil(), d.getJumlah(),
                FormatRupiah.format(d.getHargaSatuan()), FormatRupiah.format(d.getSubtotal()),
                d.getCatatan() == null ? "" : d.getCatatan()});
        }
        Tema.sesuaikanTinggi(tblKeranjang, 4);
        txtTotal.setText(FormatRupiah.format(controller.total()));
        aturPembayaran();
    }

    private void muatProduk() {
        try {
            daftarProduk = controller.cariProduk(txtCari.getText());
        } catch (ValidasiException ex) {
            Tema.error(this, ex.getMessage());
            return;
        }
        modelProduk.setRowCount(0);
        for (Produk p : daftarProduk) {
            modelProduk.addRow(new Object[]{p.getIdProduk(), p.getKodeProduk(), p.getNamaProduk(), p.getKategori(),
                p.getUkuran() == null ? "-" : p.getUkuran(), FormatRupiah.format(p.getHarga()), p.getStok(),
                p.getStokMinimum(), p.isStokMenipis() ? "Menipis" : "Aman"});
        }
    }

    private void muatTopping() {
        List<String> nama = new ArrayList<>();
        nama.add(TANPA_TOPPING);
        try {
            nama.addAll(controller.muatTopping());
        } catch (ValidasiException ex) {
            Tema.error(this, ex.getMessage());
        }
        cmbTopping1.setModel(new DefaultComboBoxModel<>(nama.toArray(new String[0])));
        cmbTopping2.setModel(new DefaultComboBoxModel<>(nama.toArray(new String[0])));
    }

    private Produk produkTerpilih() {
        int baris = tblProduk.getSelectedRow();
        return baris < 0 ? null : daftarProduk.get(baris);
    }

    private void kembali() {
        if (!controller.getKeranjang().isEmpty()
                && !Tema.konfirmasi(this, "Keranjang belum disimpan dan akan hilang. Tetap kembali?")) {
            return;
        }
        new FormMenuUtama().setVisible(true);
        dispose();
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
        pnlInput = new javax.swing.JPanel();
        lblJumlah = new javax.swing.JLabel();
        spnJumlah = new javax.swing.JSpinner();
        lblTopping1 = new javax.swing.JLabel();
        cmbTopping1 = new javax.swing.JComboBox<>();
        lblTopping2 = new javax.swing.JLabel();
        cmbTopping2 = new javax.swing.JComboBox<>();
        lblCatatan = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        btnTambahKeranjang = new javax.swing.JButton();
        pnlKeranjang = new javax.swing.JPanel();
        lblKeranjang = new javax.swing.JLabel();
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
        pnlUtama.setPreferredSize(new java.awt.Dimension(1280, 640));
        pnlUtama.setLayout(null);

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo_kecil.png"))); // NOI18N

        pnlUtama.add(lblLogo);
        lblLogo.setBounds(24, 16, 150, 42);

        lblCari.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblCari.setForeground(new java.awt.Color(31, 61, 43));
        lblCari.setText("Cari (Kode/Nama):"); // NOI18N

        pnlUtama.add(lblCari);
        lblCari.setBounds(24, 70, 150, 36);

        txtCari.setBackground(new java.awt.Color(255, 255, 255));
        txtCari.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtCari.setForeground(new java.awt.Color(31, 61, 43));

        pnlUtama.add(txtCari);
        txtCari.setBounds(180, 70, 372, 36);

        btnCari.setBackground(new java.awt.Color(46, 139, 87));
        btnCari.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        btnCari.setForeground(new java.awt.Color(255, 255, 255));
        btnCari.setText("Cari"); // NOI18N
        btnCari.setBorderPainted(false);
        btnCari.setFocusPainted(false);
        btnCari.setOpaque(true);

        pnlUtama.add(btnCari);
        btnCari.setBounds(564, 70, 100, 36);


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
        spProduk.setBounds(24, 120, 640, 240);

        pnlInput.setBackground(new java.awt.Color(240, 255, 240));
        pnlInput.setLayout(null);

        lblJumlah.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        lblJumlah.setForeground(new java.awt.Color(31, 61, 43));
        lblJumlah.setText("Jumlah"); // NOI18N

        pnlInput.add(lblJumlah);
        lblJumlah.setBounds(16, 12, 120, 24);

        spnJumlah.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        spnJumlah.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));

        pnlInput.add(spnJumlah);
        spnJumlah.setBounds(16, 40, 120, 36);

        lblTopping1.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        lblTopping1.setForeground(new java.awt.Color(31, 61, 43));
        lblTopping1.setText("Topping 1"); // NOI18N

        pnlInput.add(lblTopping1);
        lblTopping1.setBounds(152, 12, 228, 24);

        cmbTopping1.setBackground(new java.awt.Color(255, 255, 255));
        cmbTopping1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbTopping1.setForeground(new java.awt.Color(31, 61, 43));
        cmbTopping1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(Tanpa topping)" }));

        pnlInput.add(cmbTopping1);
        cmbTopping1.setBounds(152, 40, 228, 36);

        lblTopping2.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        lblTopping2.setForeground(new java.awt.Color(31, 61, 43));
        lblTopping2.setText("Topping 2"); // NOI18N

        pnlInput.add(lblTopping2);
        lblTopping2.setBounds(396, 12, 228, 24);

        cmbTopping2.setBackground(new java.awt.Color(255, 255, 255));
        cmbTopping2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbTopping2.setForeground(new java.awt.Color(31, 61, 43));
        cmbTopping2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(Tanpa topping)" }));

        pnlInput.add(cmbTopping2);
        cmbTopping2.setBounds(396, 40, 228, 36);

        lblCatatan.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        lblCatatan.setForeground(new java.awt.Color(31, 61, 43));
        lblCatatan.setText("Catatan (opsional)"); // NOI18N

        pnlInput.add(lblCatatan);
        lblCatatan.setBounds(16, 92, 300, 24);

        txtCatatan.setBackground(new java.awt.Color(255, 255, 255));
        txtCatatan.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtCatatan.setForeground(new java.awt.Color(31, 61, 43));

        pnlInput.add(txtCatatan);
        txtCatatan.setBounds(16, 120, 608, 36);

        btnTambahKeranjang.setBackground(new java.awt.Color(46, 139, 87));
        btnTambahKeranjang.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnTambahKeranjang.setForeground(new java.awt.Color(255, 255, 255));
        btnTambahKeranjang.setText("Tambah Ke Keranjang"); // NOI18N
        btnTambahKeranjang.setBorderPainted(false);
        btnTambahKeranjang.setFocusPainted(false);
        btnTambahKeranjang.setOpaque(true);

        pnlInput.add(btnTambahKeranjang);
        btnTambahKeranjang.setBounds(16, 176, 608, 48);

        pnlUtama.add(pnlInput);
        pnlInput.setBounds(24, 376, 640, 240);

        pnlKeranjang.setBackground(new java.awt.Color(240, 255, 240));
        pnlKeranjang.setLayout(null);

        lblKeranjang.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblKeranjang.setForeground(new java.awt.Color(46, 139, 87));
        lblKeranjang.setText("Keranjang"); // NOI18N

        pnlKeranjang.add(lblKeranjang);
        lblKeranjang.setBounds(16, 12, 300, 32);


        tblKeranjang.setFont(new java.awt.Font("Poppins", 0, 13)); // NOI18N
        tblKeranjang.setRowHeight(30);
        tblKeranjang.setFillsViewportHeight(true);
        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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

        spKeranjang.setViewportView(tblKeranjang);

        pnlKeranjang.add(spKeranjang);
        spKeranjang.setBounds(16, 52, 536, 196);

        btnHapusItem.setBackground(new java.awt.Color(192, 57, 43));
        btnHapusItem.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        btnHapusItem.setForeground(new java.awt.Color(255, 255, 255));
        btnHapusItem.setText("Hapus Item"); // NOI18N
        btnHapusItem.setBorderPainted(false);
        btnHapusItem.setFocusPainted(false);
        btnHapusItem.setOpaque(true);

        pnlKeranjang.add(btnHapusItem);
        btnHapusItem.setBounds(16, 260, 128, 36);

        btnKosongkan.setBackground(new java.awt.Color(207, 229, 207));
        btnKosongkan.setFont(new java.awt.Font("Poppins Medium", 0, 13)); // NOI18N
        btnKosongkan.setForeground(new java.awt.Color(46, 139, 87));
        btnKosongkan.setText("Kosongkan"); // NOI18N
        btnKosongkan.setBorderPainted(false);
        btnKosongkan.setFocusPainted(false);
        btnKosongkan.setOpaque(true);

        pnlKeranjang.add(btnKosongkan);
        btnKosongkan.setBounds(156, 260, 128, 36);

        lblTotal.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(31, 61, 43));
        lblTotal.setText("Total"); // NOI18N

        pnlKeranjang.add(lblTotal);
        lblTotal.setBounds(16, 316, 160, 36);

        txtTotal.setBackground(new java.awt.Color(255, 255, 255));
        txtTotal.setFont(new java.awt.Font("Poppins", 0, 15)); // NOI18N
        txtTotal.setForeground(new java.awt.Color(31, 61, 43));
        txtTotal.setEditable(false);

        pnlKeranjang.add(txtTotal);
        txtTotal.setBounds(184, 316, 368, 36);

        lblMetode.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblMetode.setForeground(new java.awt.Color(31, 61, 43));
        lblMetode.setText("Metode Pembayaran"); // NOI18N

        pnlKeranjang.add(lblMetode);
        lblMetode.setBounds(16, 364, 160, 36);

        cmbMetode.setBackground(new java.awt.Color(255, 255, 255));
        cmbMetode.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        cmbMetode.setForeground(new java.awt.Color(31, 61, 43));
        cmbMetode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tunai", "QRIS", "Transfer" }));

        pnlKeranjang.add(cmbMetode);
        cmbMetode.setBounds(184, 364, 368, 36);

        lblUangBayar.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblUangBayar.setForeground(new java.awt.Color(31, 61, 43));
        lblUangBayar.setText("Uang Bayar"); // NOI18N

        pnlKeranjang.add(lblUangBayar);
        lblUangBayar.setBounds(16, 412, 160, 36);

        txtUangBayar.setBackground(new java.awt.Color(255, 255, 255));
        txtUangBayar.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtUangBayar.setForeground(new java.awt.Color(31, 61, 43));

        pnlKeranjang.add(txtUangBayar);
        txtUangBayar.setBounds(184, 412, 368, 36);

        lblKembalian.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblKembalian.setForeground(new java.awt.Color(31, 61, 43));
        lblKembalian.setText("Kembalian"); // NOI18N

        pnlKeranjang.add(lblKembalian);
        lblKembalian.setBounds(16, 460, 160, 36);

        txtKembalian.setBackground(new java.awt.Color(255, 255, 255));
        txtKembalian.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtKembalian.setForeground(new java.awt.Color(31, 61, 43));
        txtKembalian.setEditable(false);

        pnlKeranjang.add(txtKembalian);
        txtKembalian.setBounds(184, 460, 368, 36);

        btnCetak.setBackground(new java.awt.Color(46, 139, 87));
        btnCetak.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnCetak.setText("Cetak Transaksi"); // NOI18N
        btnCetak.setBorderPainted(false);
        btnCetak.setFocusPainted(false);
        btnCetak.setOpaque(true);

        pnlKeranjang.add(btnCetak);
        btnCetak.setBounds(16, 520, 368, 52);

        btnKembali.setBackground(new java.awt.Color(207, 229, 207));
        btnKembali.setFont(new java.awt.Font("Poppins Medium", 0, 16)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(46, 139, 87));
        btnKembali.setText("Kembali"); // NOI18N
        btnKembali.setBorderPainted(false);
        btnKembali.setFocusPainted(false);
        btnKembali.setOpaque(true);

        pnlKeranjang.add(btnKembali);
        btnKembali.setBounds(396, 520, 156, 52);

        pnlUtama.add(pnlKeranjang);
        pnlKeranjang.setBounds(688, 16, 568, 600);

        getContentPane().add(pnlUtama, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnKosongkan;
    private javax.swing.JButton btnTambahKeranjang;
    private javax.swing.JComboBox<String> cmbMetode;
    private javax.swing.JComboBox<String> cmbTopping1;
    private javax.swing.JComboBox<String> cmbTopping2;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblKeranjang;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMetode;
    private javax.swing.JLabel lblTopping1;
    private javax.swing.JLabel lblTopping2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUangBayar;
    private javax.swing.JPanel pnlInput;
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
