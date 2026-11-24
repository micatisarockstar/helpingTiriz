package com.mycompany.helpingtiriz.entity;

public class Pengguna {
    private int idPengguna;
    private String namaLengkap;
    private String username;
    private String peran;          // "admin" atau "kasir"

    public Pengguna() {
    }

    public Pengguna(int idPengguna, String namaLengkap, String username, String peran) {
        this.idPengguna = idPengguna;
        this.namaLengkap = namaLengkap;
        this.username = username;
        this.peran = peran;
    }

    public int getIdPengguna() { return idPengguna; }
    public void setIdPengguna(int idPengguna) { this.idPengguna = idPengguna; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; }

    public boolean isAdmin() {
        return "admin".equals(peran);
    }
}
