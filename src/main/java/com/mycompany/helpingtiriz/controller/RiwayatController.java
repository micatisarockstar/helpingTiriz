package com.mycompany.helpingtiriz.controller;

import com.mycompany.helpingtiriz.entity.DetailTransaksi;
import com.mycompany.helpingtiriz.entity.Transaksi;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.repository.TransaksiDAO;
import com.mycompany.helpingtiriz.util.Validasi;
import java.sql.SQLException;
import java.util.List;

/** CONTROLLER Riwayat Transaksi. */
public class RiwayatController {
    public static final String[] FILTER = {"Semua", Transaksi.STATUS_BELUM, Transaksi.STATUS_SELESAI};

    private final TransaksiDAO dao = new TransaksiDAO();

    public List<Transaksi> muat(String filter) throws ValidasiException {
        try {
            return dao.ambilSemua("Semua".equals(filter) ? null : filter);
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "memuat riwayat");
        }
    }

    public List<DetailTransaksi> ambilDetail(Transaksi t) throws ValidasiException {
        try {
            return dao.ambilDetail(t.getIdTransaksi());
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "memuat detail transaksi");
        }
    }

    public void tandaiSelesai(Transaksi t) throws ValidasiException {
        if (t == null) {
            throw new ValidasiException("Pilih transaksi yang akan ditandai selesai.");
        }
        if (t.isSelesai()) {
            throw new ValidasiException("Pesanan ini sudah berstatus Selesai.");
        }
        try {
            dao.tandaiSelesai(t.getIdTransaksi());
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "menandai pesanan selesai");
        }
    }
}
