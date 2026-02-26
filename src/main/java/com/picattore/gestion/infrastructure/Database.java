package com.picattore.gestion.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    public static Connection conectar() {
        Connection conn = null;
        try {
            // "jdbc:sqlite:mi_data.db" creará el archivo en la misma carpeta que el JAR
            String url = "jdbc:sqlite:mi_data.db";
            conn = DriverManager.getConnection(url);

            // Crear tablas iniciales
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS notas (id INTEGER PRIMARY KEY, contenido TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Idiomas (Id INTEGER PRIMARY KEY, codigo TEXT, nombre TEXT)");

        } catch (Exception e) {
            System.err.println("Error DB: " + e.getMessage());
        }
        return conn;
    }
}
