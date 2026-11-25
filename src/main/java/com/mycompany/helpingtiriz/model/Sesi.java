package com.mycompany.helpingtiriz.model;

import com.mycompany.helpingtiriz.entity.Pengguna;

/** Menyimpan siapa yang sedang login selama aplikasi berjalan. */
public class Sesi {
    private static Pengguna penggunaAktif;

    public static Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }

    public static void setPenggunaAktif(Pengguna pengguna) {
        penggunaAktif = pengguna;
    }

    public static boolean isAdmin() {
        return penggunaAktif != null && penggunaAktif.isAdmin();
    }

    public static void keluar() {
        penggunaAktif = null;
    }
}
