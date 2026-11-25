package com.mycompany.helpingtiriz.util;

import com.mycompany.helpingtiriz.exception.ValidasiException;
import java.math.BigDecimal;
import java.sql.SQLException;

/** Pemeriksa input sederhana yang dipakai semua controller. Gagal -> lempar ValidasiException. */
public final class Validasi {

    private Validasi() {
    }

    /** Teks wajib diisi dan ga boleh lebih panjang dari batas. hasil dah di-trim. */
    public static String wajib(String label, String nilai, int maks) throws ValidasiException {
        String t = nilai == null ? "" : nilai.trim();
        if (t.isEmpty()) {
            throw new ValidasiException(label + " wajib diisi.");
        }
        if (t.length() > maks) {
            throw new ValidasiException(label + " maksimal " + maks + " karakter.");
        }
        return t;
    }

    /** Angka bulat (hanya digit) dalam rentang min..maks. */
    public static int bulat(String label, String nilai, int min, int maks) throws ValidasiException {
        String t = nilai == null ? "" : nilai.trim();
        if (t.isEmpty()) {
            throw new ValidasiException(label + " wajib diisi.");
        }
        if (!t.matches("\\d{1,9}")) {
            throw new ValidasiException(label + " harus berupa angka bulat positif (tanpa huruf atau tanda lain).");
        }
        int angka = Integer.parseInt(t);
        if (angka < min || angka > maks) {
            throw new ValidasiException(label + " harus antara " + min + " dan " + maks + ".");
        }
        return angka;
    }

    /** Nominal rupiah ("9000" atau "9.000") dalam rentang min..maks. */
    public static BigDecimal rupiah(String label, String nilai, long min, long maks) throws ValidasiException {
        if (nilai == null || nilai.trim().isEmpty()) {
            throw new ValidasiException(label + " wajib diisi.");
        }
        BigDecimal angka;
        try {
            angka = FormatRupiah.parse(nilai);
        } catch (NumberFormatException e) {
            throw new ValidasiException(label + " harus berupa angka valid (contoh: 9000).");
        }
        if (angka.compareTo(BigDecimal.valueOf(min)) < 0 || angka.compareTo(BigDecimal.valueOf(maks)) > 0) {
            throw new ValidasiException(label + " harus antara " + FormatRupiah.format(BigDecimal.valueOf(min))
                    + " dan " + FormatRupiah.format(BigDecimal.valueOf(maks)) + ".");
        }
        return angka;
    }

    /** Mengubah error database menjadi pesan yang mudah dipahami. */
    public static ValidasiException errorDb(SQLException e, String aktivitas) {
        String state = e.getSQLState();
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if ((state != null && state.startsWith("08")) || e.getErrorCode() == 1049
                || msg.contains("No suitable driver") || msg.contains("Communications link failure")) {
            return new ValidasiException("Tidak bisa terhubung ke database.\nPastikan MySQL di Laragon sudah menyala (Start All).");
        }
        if (e.getErrorCode() == 1451) {
            return new ValidasiException("Data ini masih dipakai data lain, jadi tidak bisa dihapus.");
        }
        return new ValidasiException("Gagal " + aktivitas + ": " + msg);
    }
}
