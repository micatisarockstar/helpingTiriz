package com.mycompany.helpingtiriz.controller;

import com.mycompany.helpingtiriz.entity.Pengguna;
import com.mycompany.helpingtiriz.exception.ValidasiException;
import com.mycompany.helpingtiriz.repository.PenggunaDAO;
import com.mycompany.helpingtiriz.util.Sesi;
import com.mycompany.helpingtiriz.util.Validasi;
import java.sql.SQLException;

/** CONTROLLER Login: periksa isian, tanya ke database lewat DAO, simpan sesi. */
public class LoginController {
    private final PenggunaDAO dao = new PenggunaDAO();

    public void masuk(String username, String password) throws ValidasiException {
        String user = Validasi.wajib("Username", username, 50);
        if (password == null || password.isEmpty()) {
            throw new ValidasiException("Password wajib diisi.");
        }
        Pengguna pengguna;
        try {
            pengguna = dao.login(user, password);
        } catch (SQLException e) {
            throw Validasi.errorDb(e, "login");
        }
        if (pengguna == null) {
            throw new ValidasiException("Username atau password salah.");
        }
        Sesi.setPenggunaAktif(pengguna);
    }
}
