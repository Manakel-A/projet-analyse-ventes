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

public class Alertes extends JFrame {

    public Alertes() {

        setTitle("Alertes - Pharmacie");
        setSize(950,520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // =========================
        // BARRE BLEUE EN HAUT
        // =========================

        JPanel top = new JPanel();
        top.setBackground(new Color(40,95,200));
        top.setPreferredSize(new Dimension(900,70));
        top.setLayout(new FlowLayout(FlowLayout.CENTER,50,20));

        JLabel titre = new JLabel("Alertes Pharmacie");
        titre.setFont(new Font("Segoe UI",Font.BOLD,24));
        titre.setForeground(Color.WHITE);

        top.add(titre);

        add(top,BorderLayout.NORTH);

        // =========================
        // PANEL PRINCIPAL
        // =========================

        JPanel main = new JPanel();
        main.setBackground(new Color(235,240,250));
        main.setLayout(new GridLayout(1,3,20,20));
        main.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        add(main,BorderLayout.CENTER);

        // =========================
        // CARTE 1 : PEREMPTION
        // =========================

        JPanel peremptionPanel = new JPanel(new BorderLayout());
        peremptionPanel.setBackground(Color.WHITE);
        peremptionPanel.setBorder(BorderFactory.createTitledBorder("⚠ Médicaments proches de péremption"));

        String col1[]={"Médicament","Date","Alerte"};

        DefaultTableModel model1 = new DefaultTableModel(col1,0);

        JTable table1 = new JTable(model1);

        model1.addRow(new Object[]{"Amoxicilline","05/05/2026","5 jours"});
        model1.addRow(new Object[]{"Doliprane","03/05/2026","3 jours"});

        peremptionPanel.add(new JScrollPane(table1));

        main.add(peremptionPanel);

        // =========================
        // CARTE 2 : STOCK FAIBLE
        // =========================

        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createTitledBorder("❗ Stock faible"));

        String col2[]={"Médicament","Stock","Statut"};

        DefaultTableModel model2 = new DefaultTableModel(col2,0);

        JTable table2 = new JTable(model2);

        model2.addRow(new Object[]{"Paracetamol","2","Stock faible"});
        model2.addRow(new Object[]{"Ibuprofène","0","Rupture"});

        stockPanel.add(new JScrollPane(table2));

        main.add(stockPanel);

        // =========================
        // CARTE 3 : RESUME ALERTES
        // =========================

        JPanel resumePanel = new JPanel();
        resumePanel.setBackground(Color.WHITE);
        resumePanel.setLayout(new GridLayout(3,1,10,10));
        resumePanel.setBorder(BorderFactory.createTitledBorder("Résumé des alertes"));

        JLabel exp = new JLabel("Médicaments expirant bientôt : 2");
        exp.setFont(new Font("Segoe UI",Font.BOLD,14));

        JLabel stock = new JLabel("Stock faible : 2");
        stock.setFont(new Font("Segoe UI",Font.BOLD,14));

        JLabel total = new JLabel("Total alertes : 4");
        total.setFont(new Font("Segoe UI",Font.BOLD,16));
        total.setForeground(Color.RED);

        resumePanel.add(exp);
        resumePanel.add(stock);
        resumePanel.add(total);

        main.add(resumePanel);

        // =========================
        // BOUTON RETOUR
        // =========================

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(235,240,250));

        JButton retour = new JButton("Retour");
        retour.setBackground(new Color(0,170,110));
        retour.setForeground(Color.WHITE);

        retour.addActionListener(e -> dispose());

        bottom.add(retour);

        add(bottom,BorderLayout.SOUTH);
    }

    public static void main(String[] args) {

        new Alertes().setVisible(true);

    }
}