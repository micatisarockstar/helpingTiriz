package com.mycompany.helpingtiriz.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public final class FormatRupiah {
    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));

    private FormatRupiah() {
    }

    /** 9000 menjadi "Rp 9.000". */
    public static String format(BigDecimal angka) {
        return "Rp " + NF.format(angka.setScale(0, RoundingMode.HALF_UP));
    }

    /**
     * Mengubah teks kek "9000", "9.000", atau "Rp 9.000" menjadi BigDecimal.
     * Melempar NumberFormatException bila tidak ada angka sama sekali.
     */
    public static BigDecimal parse(String teks) {
        return new BigDecimal(teks.replaceAll("[^0-9]", ""));
    }
}
