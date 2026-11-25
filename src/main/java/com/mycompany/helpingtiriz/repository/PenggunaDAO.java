package com.mycompany.helpingtiriz.repository;

import com.mycompany.helpingtiriz.entity.Pengguna;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PenggunaDAO {

    /** Mengembalikan user apbila username & password benar, selain itu null. */
    public Pengguna login(String username, String password) throws SQLException {
        String sql = "SELECT id_pengguna, nama_lengkap, username, peran "
                + "FROM pengguna WHERE username = ? AND password = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pengguna(rs.getInt("id_pengguna"), rs.getString("nama_lengkap"),
                            rs.getString("username"), rs.getString("peran"));
                }
            }
        }
        return null;
    }

    /** SHA-256 heksadesimal huruf kecil, sama dengan SHA2(teks, 256) kl di mysql */
    private String hash(String teks) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(teks.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
