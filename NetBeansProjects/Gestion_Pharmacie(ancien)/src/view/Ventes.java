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
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Ventes extends JFrame {

    JComboBox<String> comboMedicament;
    JTextField champQuantite;

    JTextField totalJour;
    JTextField totalMois;
    JTextField benefice;

    JTable table;
    DefaultTableModel model;

    double sommeJour = 0;

    public Ventes(){

        setTitle("Pharmacie - Ventes");
        setSize(900,550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // =====================
        // BARRE BLEUE
        // =====================

        JPanel top = new JPanel();
        top.setBackground(new Color(40,95,200));
        top.setPreferredSize(new Dimension(900,70));
        top.setLayout(new FlowLayout(FlowLayout.CENTER,60,20));

        JLabel ventes = new JLabel("Ventes");
        ventes.setFont(new Font("Segoe UI",Font.BOLD,22));
        ventes.setForeground(Color.WHITE);

        JLabel point = new JLabel("Point Financier");
        point.setFont(new Font("Segoe UI",Font.BOLD,22));
        point.setForeground(Color.WHITE);

        top.add(ventes);
        top.add(point);

        add(top,BorderLayout.NORTH);

        // =====================
        // PANEL PRINCIPAL
        // =====================

        JPanel main = new JPanel();
        main.setBackground(new Color(235,240,250));
        main.setLayout(new GridLayout(1,2,30,30));
        main.setBorder(BorderFactory.createEmptyBorder(30,40,30,40));

        add(main,BorderLayout.CENTER);

        // =====================
        // CARTE VENTES
        // =====================

        JPanel carteVente = new JPanel();
        carteVente.setBackground(Color.WHITE);
        carteVente.setLayout(new GridLayout(6,1,10,10));
        carteVente.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titreVente = new JLabel("Ventes");
        titreVente.setFont(new Font("Segoe UI",Font.BOLD,18));

        String medicaments[]={"Paracetamol","Amoxicilline","Ibuprofene"};

        comboMedicament = new JComboBox<>(medicaments);

        champQuantite = new JTextField();

        JButton btnVendre = new JButton("Enregistrer vente");
        btnVendre.setBackground(new Color(0,170,110));
        btnVendre.setForeground(Color.WHITE);

        carteVente.add(titreVente);
        carteVente.add(new JLabel("Médicament"));
        carteVente.add(comboMedicament);
        carteVente.add(new JLabel("Quantité vendue"));
        carteVente.add(champQuantite);
        carteVente.add(btnVendre);

        main.add(carteVente);

        // =====================
        // CARTE POINT FINANCIER
        // =====================

        JPanel carteFinance = new JPanel();
        carteFinance.setBackground(Color.WHITE);
        carteFinance.setLayout(new GridLayout(7,1,10,10));
        carteFinance.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titreFinance = new JLabel("Point Financier");
        titreFinance.setFont(new Font("Segoe UI",Font.BOLD,18));

        totalJour = new JTextField("0 FCFA");
        totalJour.setEditable(false);

        totalMois = new JTextField("0 FCFA");
        totalMois.setEditable(false);

        benefice = new JTextField("0 FCFA");
        benefice.setEditable(false);

        carteFinance.add(titreFinance);
        carteFinance.add(new JLabel("Total ventes du jour"));
        carteFinance.add(totalJour);
        carteFinance.add(new JLabel("Total ventes du mois"));
        carteFinance.add(totalMois);
        carteFinance.add(new JLabel("Bénéfice"));
        carteFinance.add(benefice);

        main.add(carteFinance);

        // =====================
        // TABLE VENTES
        // =====================

        String colonnes[]={"Médicament","Quantité","Prix","Total"};

        model = new DefaultTableModel(colonnes,0);

        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        scroll.setPreferredSize(new Dimension(850,180));

        add(scroll,BorderLayout.SOUTH);

        // =====================
        // ACTION BOUTON
        // =====================

        btnVendre.addActionListener(e -> {

            String medicament = comboMedicament.getSelectedItem().toString();
            String qteText = champQuantite.getText();

            if(!qteText.isEmpty()){

                int qte = Integer.parseInt(qteText);

                double prix = 2;

                double total = qte * prix;

                model.addRow(new Object[]{medicament,qte,prix,total});

                sommeJour = sommeJour + total;

                totalJour.setText(sommeJour+" FCFA");
                totalMois.setText(sommeJour+" FCFA");
                benefice.setText((sommeJour*0.3)+" FCFA");

                champQuantite.setText("");

            }

        });

    }

    public static void main(String[] args){

        new Ventes().setVisible(true);

    }

}