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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.DBconnexion;

public class Connexion extends JFrame {

    private static final Color BLEU      = new Color(25, 90, 160);
    private static final Color BLEU_DARK = new Color(18, 65, 125);
    private static final Color BLANC     = Color.WHITE;
    private static final Color GRIS_FOND = new Color(245, 247, 250);
    private static final Color GRIS_BRD  = new Color(200, 210, 225);
    private static final Color GRIS_TXT  = new Color(90, 100, 120);
    private static final Color ROUGE     = new Color(200, 50, 50);
    private static final Color VERT      = new Color(34, 150, 70);

    private JTextField champEmail;
    private JPasswordField champMotDePasse;
    private JCheckBox chkVoir;
    private JLabel lblMsg;
    private JButton btnOK;

    public Connexion() {
        buildUI();
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("logo.png")).getImage());
    }

    private void buildUI() {

        setTitle("PharmacieL2M — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(430, 620); // fenêtre plus grande
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);

        root.add(panelHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelForm());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        root.add(scroll, BorderLayout.CENTER);

        root.add(panelFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel panelHeader() {

        JPanel p = new JPanel();
        p.setBackground(BLEU);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(32, 20, 28, 20));

        JLabel ic = label("✚", 46, Font.BOLD, BLANC);
        JLabel ti = label("L2M", 24, Font.BOLD, BLANC);
        JLabel st = label("Système de Gestion de Pharmacie", 12, Font.PLAIN, new Color(175,210,255));

        for (JLabel l : new JLabel[]{ic,ti,st}) {
            l.setAlignmentX(.5f);
            p.add(l);
        }

        return p;
    }

    private JPanel panelForm() {

        JPanel p = new JPanel();
        p.setBackground(BLANC);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(26,40,18,40));

        p.add(label("Connexion",18,Font.BOLD,BLEU));
        p.add(Box.createVerticalStrut(3));
        p.add(label("Entrez votre e-mail et mot de passe",12,Font.PLAIN,GRIS_TXT));
        p.add(Box.createVerticalStrut(20));

        p.add(separateur());
        p.add(Box.createVerticalStrut(20));

        p.add(label("Adresse e-mail *",12,Font.BOLD,new Color(45,60,85)));
        p.add(Box.createVerticalStrut(5));

        champEmail = champTexte("ex: l2m@gmail.com");
        p.add(champEmail);

        p.add(Box.createVerticalStrut(15));

        p.add(label("Mot de passe *",12,Font.BOLD,new Color(45,60,85)));
        p.add(Box.createVerticalStrut(5));

        champMotDePasse = champMdp();
        p.add(champMotDePasse);

        p.add(Box.createVerticalStrut(8));

        chkVoir = new JCheckBox("Afficher le mot de passe");
        chkVoir.setBackground(BLANC);
        chkVoir.setFont(new Font("Segoe UI",Font.PLAIN,12));
        chkVoir.setForeground(GRIS_TXT);
        chkVoir.setAlignmentX(0f);

        chkVoir.addActionListener(e ->
                champMotDePasse.setEchoChar(chkVoir.isSelected() ? (char)0 : '●'));

        p.add(chkVoir);

        p.add(Box.createVerticalStrut(14));

        lblMsg = new JLabel(" ");
        lblMsg.setFont(new Font("Segoe UI",Font.PLAIN,12));
        lblMsg.setForeground(ROUGE);
        lblMsg.setAlignmentX(0f);
        p.add(lblMsg);

        p.add(Box.createVerticalStrut(10));

        btnOK = boutonPrincipal("Se connecter");
        btnOK.addActionListener(e -> authentifier());

        champEmail.addActionListener(e -> authentifier());
        champMotDePasse.addActionListener(e -> authentifier());

        p.add(btnOK);
        p.add(Box.createVerticalStrut(10));

            JButton btnInscrire = new JButton("S'inscrire");
            btnInscrire.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btnInscrire.setForeground(BLEU);
            btnInscrire.setBackground(BLANC);
            btnInscrire.setBorderPainted(false);
            btnInscrire.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnInscrire.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Action quand on clique
            btnInscrire.addActionListener(e -> ouvrirInscription());

            p.add(btnInscrire);

        return p;
    }

    private JPanel panelFooter() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(8,20,14,20));

        p.add(separateur(),BorderLayout.NORTH);

        JLabel v = new JLabel("PharmacieL2M v1.0  —  © 2025",SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI",Font.PLAIN,11));
        v.setForeground(new Color(170,180,195));

        p.add(v,BorderLayout.SOUTH);

        return p;
    }

    private void authentifier() {

        String email = champEmail.getText().trim();
        String mdp = new String(champMotDePasse.getPassword());

        if(email.isEmpty() || mdp.isEmpty()) {
            showMsg("Veuillez remplir tous les champs.",ROUGE);
            return;
        }

        btnOK.setEnabled(false);
        btnOK.setText("Vérification...");

        new SwingWorker<String[],Void>() {

            protected String[] doInBackground() throws Exception {

                String sql="SELECT id_pharmacien,nom,prenom,role FROM \"Pharmacien\" WHERE email=? AND motdepasse=?";

                try(Connection conn=DBconnexion.getConnection();
                    PreparedStatement ps=conn.prepareStatement(sql)){

                    ps.setString(1,email);
                    ps.setString(2,mdp);

                    ResultSet rs=ps.executeQuery();

                    if(rs.next()) {

                        return new String[]{
                                rs.getString("id_pharmacien"),
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getString("role")
                        };
                    }
                }

                return null;
            }

            protected void done() {

                try {

                    String[] data=get();

                    if(data!=null) {

                        showMsg("✓ Bienvenue "+data[2]+" "+data[1]+" !",VERT);

                        Timer t=new Timer(800,ev->{

                            dispose();
                            new Tableaudebord(data[0],data[1],data[2],data[3]);

                        });

                        t.setRepeats(false);
                        t.start();

                    } else {

                        showMsg("✗ Email ou mot de passe incorrect.",ROUGE);
                        champMotDePasse.setText("");
                        secouer(champMotDePasse);

                    }

                } catch(Exception ex){

                    showMsg("✗ Erreur BDD : "+ex.getMessage(),ROUGE);

                } finally {

                    btnOK.setEnabled(true);
                    btnOK.setText("Se connecter");

                }

            }

        }.execute();

    }

    private JLabel label(String t,int sz,int style,Color c){

        JLabel l=new JLabel(t);
        l.setFont(new Font("Segoe UI",style,sz));
        l.setForeground(c);
        return l;
    }

    private JSeparator separateur(){

        JSeparator s=new JSeparator();
        s.setForeground(GRIS_BRD);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        return s;
    }

    private JTextField champTexte(String placeholder){

        JTextField f=new JTextField(){

            protected void paintComponent(Graphics g){

                super.paintComponent(g);

                if(getText().isEmpty() && !isFocusOwner()){

                    g.setColor(new Color(175,188,205));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(placeholder,12,getHeight()/2+5);

                }
            }
        };

        styliserChamp(f);
        return f;
    }

    private JPasswordField champMdp(){

        JPasswordField f=new JPasswordField();
        f.setEchoChar('●');
        styliserChamp(f);
        return f;
    }

    private void styliserChamp(JTextField f){

        f.setFont(new Font("Segoe UI",Font.PLAIN,14));
        f.setBackground(BLANC);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        f.setAlignmentX(0f);

        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BRD,1,true),
                BorderFactory.createEmptyBorder(7,11,7,11)));

        f.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent e){

                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BLEU,2,true),
                        BorderFactory.createEmptyBorder(6,10,6,10)));

            }

            public void focusLost(FocusEvent e){

                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GRIS_BRD,1,true),
                        BorderFactory.createEmptyBorder(7,11,7,11)));

            }
        });
    }

    private JButton boutonPrincipal(String txt){

        JButton b=new JButton(txt);

        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setForeground(Color.WHITE);
        b.setBackground(BLEU);

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);

        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        return b;
    }

    private void showMsg(String t,Color c){

        lblMsg.setText(t);
        lblMsg.setForeground(c);
    }

    private void secouer(JComponent comp){

        int ox=comp.getX();
        int[] d={-7,7,-5,5,-3,3,0};

        Timer t=new Timer(40,null);

        final int[] i={0};

        t.addActionListener(e->{

            if(i[0]<d.length)
                comp.setLocation(ox+d[i[0]++],comp.getY());
            else{
                comp.setLocation(ox,comp.getY());
                t.stop();
            }

        });

        t.start();
    }
    private void ouvrirInscription() {
    dispose(); // ferme la page connexion
    new Inscription(); // ouvre la page inscription
    }
    

    public static void main(String[] args){

        try{

            for(UIManager.LookAndFeelInfo lf:UIManager.getInstalledLookAndFeels())

                if("Nimbus".equals(lf.getName())){
                    UIManager.setLookAndFeel(lf.getClassName());
                    break;
                }

        }catch(Exception ignored){}

        SwingUtilities.invokeLater(Connexion::new);
    }
}