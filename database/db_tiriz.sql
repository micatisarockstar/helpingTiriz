CREATE DATABASE IF NOT EXISTS db_tiriz
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE db_tiriz;

DROP TABLE IF EXISTS detail_transaksi;
DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS produk;
DROP TABLE IF EXISTS pengguna;

CREATE TABLE pengguna (
  id_pengguna  INT AUTO_INCREMENT PRIMARY KEY,
  nama_lengkap VARCHAR(100) NOT NULL,
  username     VARCHAR(50)  NOT NULL UNIQUE,
  password     VARCHAR(255) NOT NULL,           -- hash SHA-256 (hex)
  peran        ENUM('admin','kasir') NOT NULL
);

CREATE TABLE produk (
  id_produk    INT AUTO_INCREMENT PRIMARY KEY,
  kode_produk  VARCHAR(20)  NOT NULL UNIQUE,
  nama_produk  VARCHAR(100) NOT NULL,
  kategori     ENUM('Minuman','Makanan','Topping') NOT NULL,  -- cemilan udh diganti ya ke makanan
  ukuran       VARCHAR(10)  NULL,               -- S/M/L untuk minuman, NULL untuk lainnya
  harga        DECIMAL(12,2) NOT NULL,
  stok         INT NOT NULL DEFAULT 0,
  stok_minimum INT NOT NULL DEFAULT 5,
  CONSTRAINT chk_stok CHECK (stok >= 0)
);

CREATE TABLE transaksi (
  id_transaksi      INT AUTO_INCREMENT PRIMARY KEY,
  tanggal           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total             DECIMAL(12,2) NOT NULL,
  metode_pembayaran ENUM('Tunai','QRIS','Transfer') NOT NULL DEFAULT 'Tunai',  -- transaction method
  bayar             DECIMAL(12,2) NOT NULL,
  kembalian         DECIMAL(12,2) NOT NULL,
  status_pesanan    ENUM('Belum','Selesai') NOT NULL DEFAULT 'Belum',  -- tracking: barang sudah diterima pelanggan ap blm?
  waktu_selesai     DATETIME NULL,                                     -- diisi saat ditandai Selesai
  id_pengguna       INT NOT NULL,
  CONSTRAINT fk_trx_pengguna FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
);

CREATE TABLE detail_transaksi (
  id_detail    INT AUTO_INCREMENT PRIMARY KEY,
  id_transaksi INT NOT NULL,
  id_produk    INT NOT NULL,
  jumlah       INT NOT NULL,
  harga_satuan DECIMAL(12,2) NOT NULL,
  subtotal     DECIMAL(12,2) NOT NULL,
  catatan      VARCHAR(200) NULL,
  CONSTRAINT fk_det_trx    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi) ON DELETE CASCADE,
  CONSTRAINT fk_det_produk FOREIGN KEY (id_produk)    REFERENCES produk(id_produk)
);

-- Akun awal (password di-hash SHA-256, sama dengan hash yang dibuat di Java)
INSERT INTO pengguna (nama_lengkap, username, password, peran) VALUES
('Admin Tiriz', 'admin', SHA2('admin123', 256), 'admin'),
('Kasir Tiriz', 'kasir', SHA2('kasir123', 256), 'kasir');

-- Menu Tiriz 
INSERT INTO produk (kode_produk, nama_produk, kategori, ukuran, harga, stok, stok_minimum) VALUES
('ICR-CUP','Ice Cream Cup','Makanan',NULL,8000,30,5),
('ICR-CON','Ice Cream Cone','Makanan',NULL,10000,30,5),
('ICR-BWL','Ice Cream Bowl','Makanan',NULL,15000,30,5),
('CKS-ORI','Cookies Original','Makanan',NULL,10000,30,5),
('CKS-MTC','Cookies Matcha','Makanan',NULL,13000,30,5),
('CKS-RVL','Cookies Red Velvet','Makanan',NULL,13000,30,5),
('DCC-ORI','Dubai Chewy Cookie Original','Makanan',NULL,26000,30,5),
('DCC-UBE','Dubai Chewy Cookie Ube','Makanan',NULL,26000,30,5),
('MTC-ORI-S','Matcha Original','Minuman','S',9000,50,5),
('MTC-ORI-M','Matcha Original','Minuman','M',11000,50,5),
('MTC-ORI-L','Matcha Original','Minuman','L',14000,50,5),
('MTC-STR-S','Matcha Strawberry','Minuman','S',11000,50,5),
('MTC-STR-M','Matcha Strawberry','Minuman','M',13000,50,5),
('MTC-STR-L','Matcha Strawberry','Minuman','L',16000,50,5),
('MLK-ORI-S','Milk Original','Minuman','S',8000,50,5),
('MLK-ORI-M','Milk Original','Minuman','M',10000,50,5),
('MLK-ORI-L','Milk Original','Minuman','L',13000,50,5),
('MLK-STR-S','Milk Strawberry','Minuman','S',9000,50,5),
('MLK-STR-M','Milk Strawberry','Minuman','M',11000,50,5),
('MLK-STR-L','Milk Strawberry','Minuman','L',14000,50,5),
('MLK-CKL-S','Milk Coklat','Minuman','S',8000,50,5),
('MLK-CKL-M','Milk Coklat','Minuman','M',10000,50,5),
('MLK-CKL-L','Milk Coklat','Minuman','L',13000,50,5),
('MLK-ORE-S','Milk Oreo','Minuman','S',8000,50,5),
('MLK-ORE-M','Milk Oreo','Minuman','M',10000,50,5),
('MLK-ORE-L','Milk Oreo','Minuman','L',13000,50,5),
('MLK-MLN-S','Milk Melon','Minuman','S',9000,50,5),
('MLK-MLN-M','Milk Melon','Minuman','M',11000,50,5),
('MLK-MLN-L','Milk Melon','Minuman','L',14000,50,5),
('MLK-RVL-S','Milk Red Velvet','Minuman','S',8000,50,5),
('MLK-RVL-M','Milk Red Velvet','Minuman','M',10000,50,5),
('MLK-RVL-L','Milk Red Velvet','Minuman','L',13000,50,5),
('MLK-PSG-S','Milk Pisang','Minuman','S',9000,50,5),
('MLK-PSG-M','Milk Pisang','Minuman','M',11000,50,5),
('MLK-PSG-L','Milk Pisang','Minuman','L',14000,50,5),
('MLK-ARN-S','Milk Aren','Minuman','S',9000,50,5),
('MLK-ARN-M','Milk Aren','Minuman','M',11000,50,5),
('MLK-ARN-L','Milk Aren','Minuman','L',14000,50,5),
('TPG-CFL','Corn Flakes','Topping',NULL,0,100,10),
('TPG-CCR','Choco Crunch','Topping',NULL,0,100,10),
('TPG-CCM','Caramel Crumble','Topping',NULL,0,100,10),
('TPG-ORE','Oreo','Topping',NULL,0,100,10),
('TPG-RVL','Red Velvet','Topping',NULL,0,100,10),
('TPG-SPR','Sprinkle','Topping',NULL,0,100,10),
('TPG-SST','Saus Strawberry','Topping',NULL,0,100,10),
('TPG-SSC','Saus Caramel','Topping',NULL,0,100,10),
('TPG-SSK','Saus Cokelat','Topping',NULL,0,100,10);
