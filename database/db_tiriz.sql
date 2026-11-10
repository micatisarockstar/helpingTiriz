CREATE DATABASE IF NOT EXISTS db_tiriz
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_tiriz;

DROP TABLE IF EXISTS detail_transaksi;
DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS produk;
DROP TABLE IF EXISTS pengguna;

CREATE TABLE pengguna (
    id_pengguna  INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,              -- hash SHA-256, bukan teks asli
    peran        ENUM('admin','kasir') NOT NULL DEFAULT 'kasir'
) ENGINE=InnoDB;

CREATE TABLE produk (
    id_produk    INT AUTO_INCREMENT PRIMARY KEY,
    kode_produk  VARCHAR(20)  NOT NULL UNIQUE,
    nama_produk  VARCHAR(100) NOT NULL,
    kategori     ENUM('Minuman','Makanan','Topping') NOT NULL,
    ukuran       VARCHAR(10)  NULL,                  -- S/M/L untuk minuman, NULL untuk makanan/topping
    harga        DECIMAL(12,2) NOT NULL,
    stok         INT NOT NULL DEFAULT 0,
    stok_minimum INT NOT NULL DEFAULT 5,
    CONSTRAINT chk_stok_tidak_negatif CHECK (stok >= 0)
) ENGINE=InnoDB;

CREATE TABLE transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    tanggal      DATETIME      NOT NULL,
    total        DECIMAL(12,2) NOT NULL,
    bayar        DECIMAL(12,2) NOT NULL,
    kembalian    DECIMAL(12,2) NOT NULL,
    id_pengguna  INT NOT NULL,
    CONSTRAINT fk_transaksi_pengguna FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
) ENGINE=InnoDB;

CREATE TABLE detail_transaksi (
    id_detail    INT AUTO_INCREMENT PRIMARY KEY,
    id_transaksi INT NOT NULL,
    id_produk    INT NOT NULL,
    jumlah       INT NOT NULL,
    harga_satuan DECIMAL(12,2) NOT NULL,             -- harga saat transaksi (riwayat aman kl harga produk diubah)
    subtotal     DECIMAL(12,2) NOT NULL,
    catatan      VARCHAR(200)  NULL,                -- topping
    CONSTRAINT fk_detail_transaksi FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi) ON DELETE CASCADE,
    CONSTRAINT fk_detail_produk    FOREIGN KEY (id_produk)    REFERENCES produk(id_produk)
) ENGINE=InnoDB;


-- Akun login (password disimpan sebagai hash SHA-256):
--   admin / admin123   -> boleh kelola databarang
--   kasir / kasir123   -> hanya kasir dan riwaayat
INSERT INTO pengguna (nama_lengkap, username, password, peran) VALUES
  ('Admin Tiriz', 'admin', SHA2('admin123', 256), 'admin'),
  ('Kasir Tiriz',   'kasir', SHA2('kasir123', 256), 'kasir');

-- Menu Tiriz 
INSERT INTO produk (kode_produk, nama_produk, kategori, ukuran, harga, stok, stok_minimum) VALUES
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
  ('MLK-MLN-S','Milk Melon','Minuman','S',9000,4,5),
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
  ('ICE-CUP','Ice Cream Cup','Camilan',NULL,8000,40,5),
  ('ICE-CON','Ice Cream Cone','Camilan',NULL,10000,40,5),
  ('ICE-BWL','Ice Cream Bowl','Camilan',NULL,15000,5,5),
  ('CKI-ORI','Cookies Original','Camilan',NULL,10000,40,5),
  ('CKI-MTC','Cookies Matcha','Camilan',NULL,13000,40,5),
  ('CKI-RVL','Cookies Red Velvet','Camilan',NULL,13000,40,5),
  ('DCC-ORI','Dubai Chewy Cookie Original','Camilan',NULL,26000,40,5),
  ('DCC-UBE','Dubai Chewy Cookie Ube','Camilan',NULL,26000,3,5),
  ('TOP-CFL','Corn Flakes','Topping',NULL,0,100,5),
  ('TOP-CHC','Choco Crunch','Topping',NULL,0,100,5),
  ('TOP-CRC','Caramel Crumble','Topping',NULL,0,100,5),
  ('TOP-ORE','Oreo','Topping',NULL,0,100,5),
  ('TOP-RVL','Red Velvet','Topping',NULL,0,100,5),
  ('TOP-SPR','Sprinkle','Topping',NULL,0,100,5),
  ('TOP-STR','Saus Strawberry','Topping',NULL,0,100,5),
  ('TOP-CRM','Saus Caramel','Topping',NULL,0,100,5),
  ('TOP-CKL','Saus Cokelat','Topping',NULL,0,100,5);
