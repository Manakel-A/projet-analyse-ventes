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

public class GestionMedicaments extends JFrame {

    DefaultTableModel model;

    public GestionMedicaments(){

        setTitle("Gestion des Médicaments");
        setSize(1000,600);
        setMinimumSize(new Dimension(700,500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== MENU LATERAL =====
        JPanel menu = new JPanel();
        menu.setBackground(new Color(40,100,220));
        menu.setPreferredSize(new Dimension(180,0));
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("💊");
        logo.setFont(new Font("Segoe UI",Font.PLAIN,30));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton med = new JButton("Médicaments");
        JButton ventes = new JButton("Ventes");

        styleMenu(med);
        styleMenu(ventes);

        menu.add(Box.createVerticalStrut(40));
        menu.add(logo);
        menu.add(Box.createVerticalStrut(40));
        menu.add(med);
        menu.add(Box.createVerticalStrut(10));
        menu.add(ventes);

        add(menu,BorderLayout.WEST);

        // ===== HEADER =====
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT,20,15));
        header.setBackground(new Color(50,130,240));

        JLabel title = new JLabel("Gestion des Médicaments");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,22));

        header.add(title);

        add(header,BorderLayout.NORTH);

        // ===== MAIN PANEL =====
        JPanel main = new JPanel(new BorderLayout(20,20));
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        main.setBackground(new Color(245,246,250));

        // ===== FORMULAIRE =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JComboBox<String> nom = new JComboBox<>(new String[]{
                "Paracétamol","Amoxicilline","Ibuprofène"
        });

        JTextField quantite = new JTextField();
        JTextField prix = new JTextField();
        JTextField date = new JTextField();

        c.gridx=0;c.gridy=0;
        form.add(new JLabel("Nom du médicament"),c);

        c.gridx=1;
        form.add(nom,c);

        c.gridx=0;c.gridy=1;
        form.add(new JLabel("Quantité"),c);

        c.gridx=1;
        form.add(quantite,c);

        c.gridx=0;c.gridy=2;
        form.add(new JLabel("Prix"),c);

        c.gridx=1;
        form.add(prix,c);

        c.gridx=0;c.gridy=3;
        form.add(new JLabel("Date de péremption"),c);

        c.gridx=1;
        form.add(date,c);

        JButton ajouter = new JButton("Ajouter");
        ajouter.setBackground(new Color(46,204,113));
        ajouter.setForeground(Color.WHITE);
        ajouter.setFont(new Font("Segoe UI",Font.BOLD,14));

        c.gridx=0;
        c.gridy=4;
        c.gridwidth=2;
        form.add(ajouter,c);

        main.add(form,BorderLayout.NORTH);

        // ===== TABLE =====
        String col[]={"Nom","Quantité","Prix","Péremption"};
        model = new DefaultTableModel(col,0);

        JTable table = new JTable(model);
        table.setRowHeight(30);

        JScrollPane scroll = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        tablePanel.add(scroll);

        main.add(tablePanel,BorderLayout.CENTER);

        add(main,BorderLayout.CENTER);

        // ===== ACTION =====
        ajouter.addActionListener(e->{

            model.addRow(new Object[]{
                    nom.getSelectedItem(),
                    quantite.getText(),
                    prix.getText(),
                    date.getText()
            });

            quantite.setText("");
            prix.setText("");
            date.setText("");
        });
    }

    void styleMenu(JButton b){
        b.setBackground(new Color(40,100,220));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public static void main(String[] args) {

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}

        new GestionMedicaments().setVisible(true);
    }
}