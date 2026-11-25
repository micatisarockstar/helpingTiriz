package com.mycompany.helpingtiriz.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public final class FormatRupiah {
    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));

    /** Angka polos (9000) atau berpemisah titik ribuan (9.000), boleh diawali "Rp". */
    private static final Pattern POLA = Pattern.compile("^(?:rp\\.?\\s*)?(\\d{1,3}(?:\\.\\d{3})+|\\d+)$",
            Pattern.CASE_INSENSITIVE);

    private FormatRupiah() {
    }

    /** 9000 menjadi "Rp 9.000". */
    public static String format(BigDecimal angka) {
        return "Rp " + NF.format(angka.setScale(0, RoundingMode.HALF_UP));
    }

    /**
     * Mengubah "9000", "9.000", atau "Rp 9.000" menjadi BigDecimal.
     * Teks huruf lain, minus, atau koma ditolak (NumberFormatException).
     */
    public static BigDecimal parse(String teks) {
        if (teks == null) {
            throw new NumberFormatException("kosong");
        }
        java.util.regex.Matcher m = POLA.matcher(teks.trim());
        if (!m.matches()) {
            throw new NumberFormatException("bukan angka rupiah: " + teks);
        }
        return new BigDecimal(m.group(1).replace(".", ""));
    }
}
