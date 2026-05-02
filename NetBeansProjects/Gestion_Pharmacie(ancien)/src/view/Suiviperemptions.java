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
import javax.swing.table.*;
import java.awt.*;

public class Suiviperemptions extends JFrame {

    public Suiviperemptions() {

        setTitle("Suivi des Péremptions");
        setSize(950,520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= MENU LATERAL =================
        JPanel menu = new GradientPanel();
        menu.setPreferredSize(new Dimension(180,0));
        menu.setLayout(new BoxLayout(menu,BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("💊");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setFont(new Font("Segoe UI",Font.PLAIN,28));
        logo.setForeground(Color.WHITE);

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


        // ================= HEADER =================
        JPanel header = new GradientHeader();
        header.setPreferredSize(new Dimension(0,70));
        header.setLayout(new FlowLayout(FlowLayout.LEFT,25,20));

        JLabel title = new JLabel("Suivi des Péremptions");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,22));

        header.add(title);

        add(header,BorderLayout.NORTH);


        // ================= MAIN =================
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(240,242,246));
        main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ================= CARD TABLE =================
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        String cols[]={"Médicament","Date de péremption","Statut"};

        DefaultTableModel model = new DefaultTableModel(cols,0);

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI",Font.PLAIN,14));
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));

        // renderer couleur statut
        table.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());

        JScrollPane scroll = new JScrollPane(table);

        card.add(scroll);

        main.add(card,BorderLayout.CENTER);

        add(main,BorderLayout.CENTER);


        // ================= DONNEES =================
        model.addRow(new Object[]{"Paracétamol","10/05/2026","OK"});
        model.addRow(new Object[]{"Amoxicilline","01/05/2026","Expire bientôt"});
        model.addRow(new Object[]{"Ibuprofène","28/04/2024","Expiré"});
        model.addRow(new Object[]{"Doliprane 500mg","10/03/2024","Expire bientôt"});
    }


    // ================= STYLE MENU =================
    void styleMenu(JButton b){

        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0,0,0,0));
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
    }


    // ================= MENU DEGRADE =================
    class GradientPanel extends JPanel{
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;

            GradientPaint gp = new GradientPaint(
                    0,0,new Color(35,95,210),
                    0,getHeight(),new Color(70,140,255)
            );

            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }


    // ================= HEADER DEGRADE =================
    class GradientHeader extends JPanel{
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;

            GradientPaint gp = new GradientPaint(
                    0,0,new Color(40,110,230),
                    getWidth(),0,new Color(90,150,255)
            );

            g2.setPaint(gp);
            g2.fillRect(0,0,getWidth(),getHeight());
        }
    }


    // ================= COULEUR STATUT =================
    class StatusRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(
                JTable table,Object value,boolean isSelected,
                boolean hasFocus,int row,int column){

            Component c = super.getTableCellRendererComponent(
                    table,value,isSelected,hasFocus,row,column);

            String status = value.toString();

            if(status.equals("OK")){
                c.setForeground(new Color(40,170,90));
            }
            else if(status.equals("Expire bientôt")){
                c.setForeground(new Color(230,120,20));
            }
            else if(status.equals("Expiré")){
                c.setForeground(new Color(220,60,60));
            }

            return c;
        }
    }


    public static void main(String[] args) {

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}

        new Suiviperemptions().setVisible(true);
    }
}