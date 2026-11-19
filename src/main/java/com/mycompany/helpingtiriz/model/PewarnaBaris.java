package com.mycompany.helpingtiriz.model;

import java.awt.Color;

/** Diimplementasikan TableModel yang ingin mewarnai baris tertentu. */
public interface PewarnaBaris {
    /** Warna latar baris (indeks model), atau null untuk warna biasa. */
    Color warnaBaris(int barisModel);
}
