package com.mycompany.helpingtiriz.exception;

/**
 * Dipakai untuk all masalah yang perlu diberitahukan ke user (input salah, stok kurang, database mati, dll). 
 */
public class ValidasiException extends Exception {
    public ValidasiException(String pesan) {
        super(pesan);
    }
}
