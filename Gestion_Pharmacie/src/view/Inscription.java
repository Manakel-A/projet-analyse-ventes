/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author DELL
 */


import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.DBconnexion;

public class Inscription extends JFrame {

    private JTextField email;
    private JPasswordField password;
    private JTextField nom;
    private JTextField prenom;

    public Inscription() {
        setTitle("Inscription Pharmacien");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6,1,10,10));

        nom = new JTextField();
        prenom = new JTextField();
        email = new JTextField();
        password = new JPasswordField();

        JButton btn = new JButton("Créer compte");

        add(new JLabel("Nom"));
        add(nom);
        add(new JLabel("Prénom"));
        add(prenom);
        add(new JLabel("Email"));
        add(email);
        add(new JLabel("Mot de passe"));
        add(password);
        add(btn);

        btn.addActionListener(e -> inscrire());

        setVisible(true);
    }

    private void inscrire() {

        try(Connection conn = DBconnexion.getConnection()) {

            String sql = "INSERT INTO \"Pharmacien\" (nom, prenom, email, motdepasse, role) VALUES (?, ?, ?, ?, 'admin')";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nom.getText());
            ps.setString(2, prenom.getText());
            ps.setString(3, email.getText());
            ps.setString(4, new String(password.getPassword()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Compte créé avec succès");

            dispose();
            new Connexion();

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}
