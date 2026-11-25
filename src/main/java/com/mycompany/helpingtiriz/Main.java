package com.mycompany.helpingtiriz;

import com.mycompany.helpingtiriz.view.FormLogin;
import com.mycompany.helpingtiriz.view.Tema;

public class Main {
    public static void main(String[] args) {
        Tema.siapkan();      // prep                            

        // Error handling apalah
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            error.printStackTrace();
            java.awt.EventQueue.invokeLater(() -> Tema.error(null, "Terjadi kesalahan:\n" + error));
        });

        java.awt.EventQueue.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
