/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnexion {
    private static final String URL      = "jdbc:postgresql://localhost:5432/pharmacies_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "Manakel";

    private static Connection instance = null;

    /** Retourne une connexion unique (Singleton) */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion PostgreSQL établie.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL introuvable. Ajoutez postgresql-xx.jar au classpath.");
            }
        }
        return instance;
    }

    /** Ferme proprement la connexion */
    public static void fermer() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("✓ Connexion fermée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
