package com.mycompany.helpingtiriz.repository;

/** Dilempar saat checkout bila stok di database ternyata kurang dari jumlah beli. */
public class StokTidakCukupException extends Exception {
    private final String namaProduk;

    public StokTidakCukupException(String namaProduk) {
        super("Stok " + namaProduk + " tidak cukup.");
        this.namaProduk = namaProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }
}
