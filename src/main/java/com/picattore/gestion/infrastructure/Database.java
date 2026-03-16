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
            
            // Tabla Idiomas
            stmt.execute("CREATE TABLE IF NOT EXISTS Idiomas (Id INTEGER PRIMARY KEY, codigo TEXT, nombre TEXT)");
            
            try {
                stmt.execute("ALTER TABLE Idiomas ADD COLUMN principal INTEGER DEFAULT 0");
            } catch (Exception e) { }

            stmt.execute("INSERT OR IGNORE INTO Idiomas (Id, codigo, nombre, principal) VALUES (1, 'ES', 'Español', 1)");

            // Tabla Epocas
            stmt.execute("CREATE TABLE IF NOT EXISTS Epocas (" +
                    "id_epoca INTEGER PRIMARY KEY, " +
                    "codigo TEXT, " +
                    "anio_inicio INTEGER, " +
                    "anio_fin INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS EpocasTr (" +
                    "id INTEGER PRIMARY KEY, " +
                    "id_epoca INTEGER, " +
                    "id_idioma INTEGER, " +
                    "nombre TEXT, " +
                    "descripcion TEXT, " +
                    "FOREIGN KEY(id_epoca) REFERENCES Epocas(id_epoca), " +
                    "FOREIGN KEY(id_idioma) REFERENCES Idiomas(Id))");

            // Insertar Épocas por defecto
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (1, 'I', 1830, 1920)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 1, 1, 'Época I', 'El Vapor Primitivo (1830 – 1920)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 1 AND id_idioma = 1)");
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (2, 'II', 1920, 1945)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 2, 1, 'Época II', 'La Unificación (1920 – 1945)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 2 AND id_idioma = 1)");
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (3, 'III', 1945, 1970)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 3, 1, 'Época III', 'La \"Transición\" (1945 – 1970)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 3 AND id_idioma = 1)");
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (4, 'IV', 1970, 1990)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 4, 1, 'Época IV', 'La Numeración UIC (1970 – 1990)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 4 AND id_idioma = 1)");
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (5, 'V', 1990, 2005)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 5, 1, 'Época V', 'La Era de los Sectores (1990 – 2005)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 5 AND id_idioma = 1)");
            stmt.execute("INSERT OR IGNORE INTO Epocas (id_epoca, codigo, anio_inicio, anio_fin) VALUES (6, 'VI', 2005, NULL)");
            stmt.execute("INSERT INTO EpocasTr (id_epoca, id_idioma, nombre, descripcion) SELECT 6, 1, 'Época VI', 'Liberalización (2005 – Actualidad)' WHERE NOT EXISTS (SELECT 1 FROM EpocasTr WHERE id_epoca = 6 AND id_idioma = 1)");

            // Tabla Paises
            stmt.execute("CREATE TABLE IF NOT EXISTS Paises (" +
                    "id_pais INTEGER PRIMARY KEY, " +
                    "codigo TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS PaisesTr (" +
                    "id INTEGER PRIMARY KEY, " +
                    "id_pais INTEGER, " +
                    "id_idioma INTEGER, " +
                    "nombre TEXT, " +
                    "FOREIGN KEY(id_pais) REFERENCES Paises(id_pais), " +
                    "FOREIGN KEY(id_idioma) REFERENCES Idiomas(Id))");

            // Tabla Escalas
            stmt.execute("CREATE TABLE IF NOT EXISTS Escalas (" +
                    "id_escala INTEGER PRIMARY KEY, " +
                    "codigo TEXT, " +
                    "escala TEXT)");

            stmt.execute("INSERT OR IGNORE INTO Escalas (id_escala, codigo, escala) VALUES (1, 'H0', '1:87')");
            stmt.execute("INSERT OR IGNORE INTO Escalas (id_escala, codigo, escala) VALUES (2, 'N', '1:160')");

            // Tabla Tipo_vehiculo
            stmt.execute("CREATE TABLE IF NOT EXISTS Tipo_vehiculo (" +
                    "id_tipo_vehiculo INTEGER PRIMARY KEY, " +
                    "codigo TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Tipo_vehiculo_tr (" +
                    "id INTEGER PRIMARY KEY, " +
                    "id_tipo_vehiculo INTEGER, " +
                    "id_idioma INTEGER, " +
                    "nombre TEXT, " +
                    "descripcion TEXT, " +
                    "FOREIGN KEY(id_tipo_vehiculo) REFERENCES Tipo_vehiculo(id_tipo_vehiculo), " +
                    "FOREIGN KEY(id_idioma) REFERENCES Idiomas(Id))");

            // Tabla Operadoras
            stmt.execute("CREATE TABLE IF NOT EXISTS Operadoras (" +
                    "id_operadora INTEGER PRIMARY KEY, " +
                    "codigo TEXT, " +
                    "nombre TEXT, " +
                    "informacion TEXT, " +
                    "anio_creacion INTEGER, " +
                    "anio_disolucion INTEGER, " +
                    "id_pais INTEGER, " +
                    "FOREIGN KEY(id_pais) REFERENCES Paises(id_pais))");
            
            try {
                stmt.execute("ALTER TABLE Operadoras ADD COLUMN id_pais INTEGER REFERENCES Paises(id_pais)");
            } catch (Exception e) { }

            // Tabla Operadoras_Relacion
            stmt.execute("CREATE TABLE IF NOT EXISTS Operadoras_Relacion (" +
                    "id_predecesora INTEGER, " +
                    "id_sucesora INTEGER, " +
                    "PRIMARY KEY (id_predecesora, id_sucesora), " +
                    "FOREIGN KEY(id_predecesora) REFERENCES Operadoras(id_operadora), " +
                    "FOREIGN KEY(id_sucesora) REFERENCES Operadoras(id_operadora))");

            // Tabla Esquema_pintura
            stmt.execute("CREATE TABLE IF NOT EXISTS Esquema_pintura (" +
                    "id_esquema_pintura INTEGER PRIMARY KEY, " +
                    "id_pais INTEGER, " +
                    "id_operadora INTEGER, " +
                    "nombre TEXT, " +
                    "anio_inicio INTEGER, " +
                    "anio_fin INTEGER, " +
                    "FOREIGN KEY(id_pais) REFERENCES Paises(id_pais), " +
                    "FOREIGN KEY(id_operadora) REFERENCES Operadoras(id_operadora))");

            // Tabla Esquema_pintura_tr
            stmt.execute("CREATE TABLE IF NOT EXISTS Esquema_pintura_tr (" +
                    "id INTEGER PRIMARY KEY, " +
                    "id_esquema_pintura INTEGER, " +
                    "id_idioma INTEGER, " +
                    "descripcion TEXT, " +
                    "codigo_colores TEXT, " +
                    "colores TEXT, " +
                    "FOREIGN KEY(id_esquema_pintura) REFERENCES Esquema_pintura(id_esquema_pintura), " +
                    "FOREIGN KEY(id_idioma) REFERENCES Idiomas(Id))");

            // Tabla Fabricantes
            stmt.execute("CREATE TABLE IF NOT EXISTS Fabricantes (" +
                    "id_fabricante INTEGER PRIMARY KEY, " +
                    "nombre TEXT, " +
                    "descripcion TEXT, " +
                    "id_pais INTEGER, " +
                    "pagina_web TEXT, " +
                    "telefono TEXT, " +
                    "email TEXT, " +
                    "fecha_alta TEXT, " +
                    "fecha_baja TEXT, " +
                    "FOREIGN KEY(id_pais) REFERENCES Paises(id_pais))");

            // --- NUEVAS TABLAS PARA TIPO MODELO ---
            stmt.execute("CREATE TABLE IF NOT EXISTS Tipo_modelo (" +
                    "id_tipo_modelo INTEGER PRIMARY KEY, " +
                    "codigo TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Tipo_modelo_tr (" +
                    "id INTEGER PRIMARY KEY, " +
                    "id_tipo_modelo INTEGER, " +
                    "id_idioma INTEGER, " +
                    "nombre TEXT, " +
                    "descripcion TEXT, " +
                    "FOREIGN KEY(id_tipo_modelo) REFERENCES Tipo_modelo(id_tipo_modelo), " +
                    "FOREIGN KEY(id_idioma) REFERENCES Idiomas(Id))");

        } catch (Exception e) {
            System.err.println("Error DB: " + e.getMessage());
        }
        return conn;
    }
}
