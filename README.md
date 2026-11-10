# helpingTiriz
## 1. Ide Proyek

Proyek ini membuat aplikasi untuk membantu Tiriz, pelaku UMKM di daerah Jatinangor, mencatat penjualan dan mengelola stok barang secara terkomputerisasi.

### 1.1 Profil Tiriz

Tiriz adalah kedai minuman dan makanan di daerah Jatinangor dengan slogan "Your day with peace, love, and TIRIZ".

#### Snacks

| Kelompok | Varian | Harga (Rp ribu) |
| --- | --- | --- |
| Ice Cream | Cup | 8 |
| Ice Cream | Cone | 10 |
| Ice Cream | Bowl | 15 |
| Cookies | Original | 10 |
| Cookies | Matcha | 13 |
| Cookies | Red Velvet | 13 |
| Dubai Chewy Cookie | Original | 26 |
| Dubai Chewy Cookie | Ube | 26 |

#### Drinks (ukuran S, M, L)

| Kelompok | Varian | S | M | L |
| --- | --- | --- | --- | --- |
| Matcha | Original | 9 | 11 | 14 |
| Matcha | Strawberry | 11 | 13 | 16 |
| Milk | Original | 8 | 10 | 13 |
| Milk | Strawberry | 9 | 11 | 14 |
| Milk | Coklat | 8 | 10 | 13 |
| Milk | Oreo | 8 | 10 | 13 |
| Milk | Melon | 9 | 11 | 14 |
| Milk | Red Velvet | 8 | 10 | 13 |
| Milk | Pisang | 9 | 11 | 14 |
| Milk | Aren | 9 | 11 | 14 |

**Topping (gratis pilih 2):** Corn Flakes, Choco Crunch, Caramel Crumble, Oreo, Red Velvet, Sprinkle, Saus Strawberry, Saus Caramel, Saus Cokelat.

### 1.2 Latar Belakang

UMKM adalah tulang punggung ekonomi lokal, tetapi banyak yang masih mengelola usahanya dengan cara manual. Dari observasi kami ke Tiriz, ditemukan dua masalah utama:
- Pendataan masih manual. Penjualan dan barang dicatat di buku atau secara lisan, sehingga rawan salah hitung, hilang, dan sulit direkap.
- Pengelolaan stok masih kurang. Tidak ada catatan jumlah barang yang selalu diperbarui, sehingga Tiriz sulit mengetahui barang yang hampir habis dan bisa menjual barang yang sebenarnya sudah kosong.

### 1.3 Kebutuhan Tiriz (Hasil Observasi)

1. Mencatat transaksi penjualan dengan cepat dan akurat.
2. Mengetahui sisa stok setiap barang secara langsung.
3. Mendapat peringatan bila pembeli memesan lebih banyak dari stok yang ada.
4. Melihat rekap penjualan tanpa menghitung ulang buku catatan.

### 1.4 Ide Solusi

Kami mengusulkan aplikasi kasir yang terhubung langsung dengan data stok. Tiriz memasukkan data barang dan stok awal ke database. Setiap kali kasir menyimpan transaksi, stok barang berkurang otomatis. Jika jumlah yang dibeli melebihi stok, aplikasi menampilkan peringatan dan transaksi tidak diproses.

### 1.5 Tujuan

- Menggantikan pencatatan manual dengan database.
- Menjaga stok tetap akurat tanpa perhitungan manual.
- Mencegah penjualan melebihi stok yang tersedia.

---

## 2. Gambaran Aplikasi

Aplikasi ini adalah sistem kasir dan manajemen stok untuk Tiriz yang berjalan di komputer, dibuat dengan Java Swing di NetBeans dan terhubung ke database MySQL lewat JDBC.

### 2.1 Deskripsi

Aplikasi memiliki dua kegiatan utama. Pertama, Tiriz mengelola data barang dan stok. Kedua, kasir melayani penjualan: barang dipilih, jumlah diisi, total dihitung otomatis, lalu transaksi disimpan dan stok berkurang secara otomatis.

### 2.2 Teknologi

| Komponen | Pilihan | Keterangan |
| --- | --- | --- |
| Bahasa | Java (JDK 8 atau lebih baru) | Sesuai ketentuan praktikum |
| Antarmuka | Java Swing (JFrame, JTable, JDialog) | Dibuat dengan GUI Builder NetBeans |
| IDE | NetBeans | Sesuai ketentuan praktikum |
| Database | MySQL | Terhubung lewat MySQL Connector/J (JDBC) |

### 2.3 Fungsionalitas

| Modul | Fungsi | Jenis |
| --- | --- | --- |
| Login | Masuk dengan username dan password | Autentikasi |
| Data Barang | Tambah, lihat, ubah, hapus barang beserta harga dan stok | CRUD |
| Tambah Stok | Menambah jumlah stok saat barang baru datang | Update |
| Kasir | Pilih barang, isi jumlah, hitung total dan kembalian, simpan transaksi | Create |
| Validasi Stok | Peringatan bila jumlah beli melebihi stok | Validasi |
| Pengurangan Stok Otomatis | Stok berkurang setelah transaksi tersimpan | Update otomatis |
| Riwayat Transaksi | Lihat daftar transaksi dan detail barang yang dibeli | Read |
| Peringatan Stok Menipis | Tanda pada barang dengan stok di bawah batas minimum | Read |

### 2.4 Alur Kerja

**Alur pengelolaan oleh Tiriz:**
1. Tiriz login ke aplikasi.
2. Tiriz mengisi data barang dan stok awal di menu Data Barang.
3. Saat barang baru datang, Tiriz menambah stok lewat menu Data Barang.

**Alur transaksi di kasir:**
1. Kasir membuka menu Kasir.
2. Kasir memilih menu (minuman beserta ukurannya, atau makanan), mengisi jumlah beli, dan mencatat topping pada kolom catatan.
3. Aplikasi membandingkan jumlah beli dengan stok di database.
4. Jika jumlah beli melebihi stok, aplikasi menampilkan peringatan berisi sisa stok dan barang tidak masuk keranjang. Kasir dapat mengubah jumlah.
5. Jika stok cukup, barang masuk ke keranjang dan total dihitung otomatis.
6. Setelah semua barang dipilih, kasir mengisi uang bayar. Aplikasi menghitung kembalian.
7. Saat kasir menekan Simpan, aplikasi menyimpan data ke tabel transaksi dan detail transaksi, lalu mengurangi stok setiap barang.
8. Struk atau ringkasan transaksi ditampilkan.

---

## 3. Rancangan Database

Database terdiri dari empat tabel: pengguna, produk, transaksi, dan detail_transaksi. Jumlah ini melampaui syarat minimal tiga tabel.

### 3.1 Daftar Entitas

| Entitas | Fungsi |
| --- | --- |
| pengguna | Akun yang boleh masuk ke aplikasi (admin atau kasir) |
| produk | Data barang dagangan beserta harga dan stok |
| transaksi | Data induk setiap penjualan (waktu, total, bayar, kembalian) |
| detail_transaksi | Barang apa saja dan berapa banyak dalam satu transaksi |

### 3.2 Struktur Tabel

#### Tabel pengguna

| Kolom | Tipe | Keterangan |
| --- | --- | --- |
| id_pengguna | INT, AUTO_INCREMENT | Primary key |
| nama_lengkap | VARCHAR(100) | Nama pengguna |
| username | VARCHAR(50) | Unik, untuk login |
| password | VARCHAR(255) | Disimpan dalam bentuk hash |
| peran | ENUM('admin','kasir') | Hak akses |

#### Tabel produk

| Kolom | Tipe | Keterangan |
| --- | --- | --- |
| id_produk | INT, AUTO_INCREMENT | Primary key |
| kode_produk | VARCHAR(20) | Unik, contoh MLK-ORI-M |
| nama_produk | VARCHAR(100) | Nama menu, contoh Milk Original |
| kategori | ENUM('Minuman','Makanan','Topping') | Kelompok menu |
| ukuran | VARCHAR(10) | S, M, atau L untuk minuman. Kosong untuk makanan dan topping |
| harga | DECIMAL(12,2) | Harga jual per satuan |
| stok | INT | Sisa stok dalam porsi atau buah, tidak boleh kurang dari 0 |
| stok_minimum | INT | Batas peringatan stok menipis, bawaan 5 |

#### Tabel transaksi

| Kolom | Tipe | Keterangan |
| --- | --- | --- |
| id_transaksi | INT, AUTO_INCREMENT | Primary key |
| tanggal | DATETIME | Waktu transaksi |
| total | DECIMAL(12,2) | Total belanja |
| bayar | DECIMAL(12,2) | Uang dari pembeli |
| kembalian | DECIMAL(12,2) | Bayar dikurangi total |
| id_pengguna | INT | Foreign key ke pengguna (kasir yang melayani) |

#### Tabel detail_transaksi

| Kolom | Tipe | Keterangan |
| --- | --- | --- |
| id_detail | INT, AUTO_INCREMENT | Primary key |
| id_transaksi | INT | Foreign key ke transaksi |
| id_produk | INT | Foreign key ke produk |
| jumlah | INT | Banyak barang dibeli |
| harga_satuan | DECIMAL(12,2) | Harga saat transaksi, supaya riwayat tidak berubah bila harga produk diubah |
| subtotal | DECIMAL(12,2) | jumlah × harga_satuan |
| catatan | VARCHAR(200) | Topping pilihan pembeli, contoh: Oreo, Sprinkle |

### 3.3 Relasi Antar Tabel

| Tabel asal | Relasi | Tabel tujuan | Penjelasan |
| --- | --- | --- | --- |
| pengguna | 1 ke banyak | transaksi | Satu kasir dapat melayani banyak transaksi |
| transaksi | 1 ke banyak | detail_transaksi | Satu transaksi berisi banyak baris barang |
| produk | 1 ke banyak | detail_transaksi | Satu produk dapat muncul di banyak transaksi |

Tabel detail_transaksi menjadi penghubung antara transaksi dan produk (relasi banyak ke banyak).

---

## 4. Rancangan Class

### 4.1 Struktur Package

| Package | Isi | Tugas |
| --- | --- | --- |
| tiriz.model | Pengguna, Produk, Transaksi, DetailTransaksi | Menyimpan data (atribut, getter, setter) |
| tiriz.dao | PenggunaDAO, ProdukDAO, TransaksiDAO | Menjalankan query SQL ke database |
| tiriz.view | FormLogin, FormMenuUtama, FormProduk, FormKasir, FormRiwayat | Tampilan Java Swing |
| tiriz.util | Koneksi, Sesi | Koneksi database dan penyimpan data pengguna yang sedang login |

### 4.2 Class Model

| Class | Atribut | Method |
| --- | --- | --- |
| Pengguna | idPengguna, namaLengkap, username, password, peran | getter dan setter |
| Produk | idProduk, kodeProduk, namaProduk, kategori, ukuran, harga, stok, stokMinimum | getter dan setter, isStokMenipis() |
| Transaksi | idTransaksi, tanggal, total, bayar, kembalian, idPengguna, daftarDetail (List) | getter dan setter, hitungTotal(), hitungKembalian() |
| DetailTransaksi | idDetail, idTransaksi, produk, jumlah, hargaSatuan, subtotal, catatan | getter dan setter, hitungSubtotal() |

### 4.3 Class DAO dan Util

| Class | Method utama | Keterangan |
| --- | --- | --- |
| Koneksi | getConnection() | Membuka koneksi JDBC ke db_tiriz |
| PenggunaDAO | login(username, password) | Mengembalikan objek Pengguna atau null |
| ProdukDAO | tambah(), ubah(), hapus(), ambilSemua(), cari(keyword), cekStok(idProduk, jumlah), tambahStok(idProduk, jumlah) | CRUD produk dan pengecekan stok |
| TransaksiDAO | simpanTransaksi(Transaksi), ambilSemua(), ambilDetail(idTransaksi) | Menyimpan transaksi dan mengurangi stok dalam satu transaksi database |
| Sesi | penggunaAktif, getPenggunaAktif(), setPenggunaAktif() | Menyimpan siapa yang sedang login |

### 4.4 Class View (Form Swing)

| Form | Komponen utama | Fungsi |
| --- | --- | --- |
| FormLogin | JTextField, JPasswordField, JButton | Memeriksa akun lewat PenggunaDAO |
| FormMenuUtama | JMenuBar atau tombol menu | Pintu masuk ke semua form |
| FormProduk | JTable, JTextField, tombol Tambah, Ubah, Hapus, Tambah Stok | CRUD data barang dan penambahan stok |
| FormKasir | JComboBox atau JTable pilihan barang, JSpinner jumlah, JTable keranjang, JLabel total, JTextField bayar | Transaksi penjualan dan peringatan stok (JOptionPane) |
| FormRiwayat | JTable transaksi, JTable detail | Melihat riwayat penjualan |

### 4.5 Hubungan Antar Class

- Form di package view memanggil class DAO, tidak mengakses database langsung.
- DAO memakai Koneksi dan membuat atau membaca objek model.
- Transaksi memiliki banyak DetailTransaksi, dan setiap DetailTransaksi merujuk ke satu Produk.
- FormKasir membaca Sesi untuk mengisi id kasir pada transaksi.

