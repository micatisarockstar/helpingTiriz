package com.mycompany.helpingtiriz.util;

import com.mycompany.helpingtiriz.entity.Pengguna;

/** Menyimpan siapa yang sedang login selama aplikasi berjalan. */
public final class Sesi {
    private static Pengguna penggunaAktif;

    private Sesi() {
    }

    public static Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }

    public static void setPenggunaAktif(Pengguna pengguna) {
        penggunaAktif = pengguna;
    }

    public static boolean sudahLogin() {
        return penggunaAktif != null;
    }

    public static boolean isAdmin() {
        return penggunaAktif != null && penggunaAktif.isAdmin();
    }

    public static void keluar() {
        penggunaAktif = null;
    }
}
