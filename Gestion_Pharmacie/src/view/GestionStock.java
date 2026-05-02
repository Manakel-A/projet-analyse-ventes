/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author DELL
 */

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import database.DBconnexion;

/**
 * GestionStock.java
 * Tables : Stock (id_stock | quantite_disponible | seuil_alerte | id_medicament)
 *          Lot_Medicament (id_lot | numero_lot | date_fabrication | quantite | id_medicament)
 *          Alerte (id_alerte | type_alerte | message | date_alerte | est_lue | id_medicament)
 */
public class GestionStock extends JFrame {

    private static final Color BLEU      = new Color(25, 90, 160);
    private static final Color BLEU_CL   = new Color(235, 243, 255);
    private static final Color BLANC     = Color.WHITE;
    private static final Color GRIS_FOND = new Color(245, 247, 250);
    private static final Color GRIS_LIG  = new Color(230, 235, 242);
    private static final Color GRIS_BRD  = new Color(200, 210, 225);
    private static final Color GRIS_TXT  = new Color(90, 100, 120);
    private static final Color VERT      = new Color(35, 155, 75);
    private static final Color ROUGE     = new Color(200, 50, 50);
    private static final Color ORANGE    = new Color(210, 120, 0);
    private static final Color JAUNE     = new Color(255, 193, 7);

    // Onglets
    private JTabbedPane onglets;

    // ── Stock ──
    private JTable          tableStock;
    private DefaultTableModel modeleStock;
    private JTextField       rechercheStock;

    // ── Lots ──
    private JTable          tableLots;
    private DefaultTableModel modeleLots;
    private JTextField      fNumLot, fQteLot;
    private JDateChooser fDateFab;
    private JComboBox<String[]> cbMedLot;
    private boolean         modeEditLot = false;
    private String          idLotEnCours;

    // ── Alertes ──
    private JTable          tableAlertes;
    private DefaultTableModel modeleAlertes;
    private JLabel          lblNbAlertes;

    public GestionStock() {
        
        buildUI();
        chargerStock();
        chargerLots();
        genererAlertesStock();
        genererAlertesPeremption();
        chargerAlertes();
        chargerMedicamentsPourLot();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Stock & Lots");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 700);
        setMinimumSize(new Dimension(980, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);
        root.add(topBar(), BorderLayout.NORTH);

        onglets = new JTabbedPane();
        onglets.setFont(new Font("Segoe UI", Font.BOLD, 13));
        onglets.setBackground(GRIS_FOND);
        onglets.addTab("📦  Stock", panelStock());
        onglets.addTab("🏷  Lots & Péremptions", panelLots());
        onglets.addTab("⚠  Alertes", panelAlertes());
        root.add(onglets, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 140, 75));
        p.setBorder(new EmptyBorder(13, 22, 13, 22));
        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("📦"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Gestion du Stock & des Lots");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17)); ti.setForeground(BLANC);
        g.add(ic); g.add(ti);
        p.add(g, BorderLayout.WEST);
        JButton bActu = new JButton("↻  Actualiser tout");
        bActu.setFont(new Font("Segoe UI", Font.BOLD, 12)); bActu.setForeground(BLANC);
        bActu.setBackground(new Color(25,120,60));
        bActu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,80),1,true),
            BorderFactory.createEmptyBorder(6,14,6,14)));
        bActu.setFocusPainted(false);
        bActu.addActionListener(e -> { chargerStock(); chargerLots(); chargerAlertes(); });
        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); d.setOpaque(false);
        d.add(bActu); p.add(d, BorderLayout.EAST);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       ONGLET 1 — STOCK
    ══════════════════════════════════════════════════════════════════ */
    private JPanel panelStock() {
        JPanel p = new JPanel(new BorderLayout(0,0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14,14,14,14));

        // Recherche
        JPanel barre = new JPanel(new BorderLayout(8,0));
        barre.setOpaque(false); barre.setBorder(new EmptyBorder(0,0,10,0));
        rechercheStock = new JTextField();
        rechercheStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rechercheStock.putClientProperty("JTextField.placeholderText","🔍  Rechercher médicament…");
        rechercheStock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD,1,true),
            BorderFactory.createEmptyBorder(7,11,7,11)));
        rechercheStock.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e){filtrerStock();}});
        barre.add(rechercheStock, BorderLayout.CENTER);
        p.add(barre, BorderLayout.NORTH);

        // Tableau Stock avec jointure Medicament
        // Colonnes : id_stock | Médicament | quantite_disponible | seuil_alerte | État
        String[] cols = {"ID Stock","Médicament","Code CIP","Qté disponible","Seuil alerte","État"};
        modeleStock = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return false;}};
        tableStock = new JTable(modeleStock);
        tableStock.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tableStock.setRowHeight(34);
        tableStock.setShowVerticalLines(false);
        tableStock.setGridColor(GRIS_LIG);
        tableStock.setSelectionBackground(BLEU_CL);
        tableStock.setBackground(BLANC);
        tableStock.setFocusable(false);

        JTableHeader h = tableStock.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,12));
        h.setBackground(new Color(218,228,242));
        h.setForeground(new Color(35,55,100));
        h.setPreferredSize(new Dimension(0,34));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {70,180,110,110,100,100};
        for(int i=0;i<lrg.length;i++) tableStock.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        // Renderer état stock
        tableStock.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,
                    boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setFont(new Font("Segoe UI",Font.PLAIN,13));
                setBorder(new EmptyBorder(0,10,0,10));
                if(!sel){setBackground(row%2==0?BLANC:new Color(248,250,254));setForeground(new Color(30,42,65));}
                if(col==5 && v!=null){
                    String etat=v.toString();
                    switch(etat){
                        case "✓ OK"      ->{setForeground(VERT);setFont(getFont().deriveFont(Font.BOLD));}
                        case "⚠ Faible"  ->{setForeground(ORANGE);setFont(getFont().deriveFont(Font.BOLD));}
                        case "✕ Rupture" ->{setForeground(ROUGE);setFont(getFont().deriveFont(Font.BOLD));}
                    }
                }
                if(col==3){setFont(getFont().deriveFont(Font.BOLD));}
                return this;
            }
        });

        JScrollPane sc = new JScrollPane(tableStock);
        sc.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        // Actions stock
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,9));
        actions.setOpaque(false);
        JButton bModSeuil = btn("✏  Modifier seuil", BLEU);
        bModSeuil.addActionListener(e -> modifierSeuil());
        actions.add(bModSeuil);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       ONGLET 2 — LOTS & PÉREMPTIONS
    ══════════════════════════════════════════════════════════════════ */
    private JPanel panelLots() {
        JPanel p = new JPanel(new BorderLayout(0,0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14,14,14,14));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelTableLots(), panelFormLot());
        split.setDividerLocation(680);
        split.setDividerSize(4);
        split.setBorder(null);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelTableLots() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLANC);
        p.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(BLEU_CL);
        head.setBorder(new EmptyBorder(8,12,8,12));
        JLabel ti = new JLabel("Liste des lots de médicaments");
        ti.setFont(new Font("Segoe UI",Font.BOLD,13)); ti.setForeground(BLEU);
        head.add(ti, BorderLayout.WEST);
        p.add(head, BorderLayout.NORTH);

        // Colonnes = Lot_Medicament : id_lot | numero_lot | date_fabrication | quantite | id_medicament
        String[] cols = {"ID Lot","N° Lot","Médicament","Date fabrication","Quantité","Jours restants","État"};
        modeleLots = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return false;}};
        tableLots = new JTable(modeleLots);
        tableLots.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tableLots.setRowHeight(34);
        tableLots.setShowVerticalLines(false);
        tableLots.setGridColor(GRIS_LIG);
        tableLots.setSelectionBackground(BLEU_CL);
        tableLots.setBackground(BLANC);
        tableLots.setFocusable(false);

        JTableHeader h = tableLots.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,12));
        h.setBackground(new Color(218,228,242));
        h.setForeground(new Color(35,55,100));
        h.setPreferredSize(new Dimension(0,34));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {70,100,160,110,80,100,90};
        for(int i=0;i<lrg.length;i++) tableLots.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        // Renderer état péremption
        tableLots.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,
                    boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setFont(new Font("Segoe UI",Font.PLAIN,13));
                setBorder(new EmptyBorder(0,10,0,10));
                if(!sel){setBackground(row%2==0?BLANC:new Color(248,250,254));setForeground(new Color(30,42,65));}
                if(col==6 && v!=null){
                    String etat=v.toString();
                    if(etat.contains("Expiré")){setForeground(ROUGE);setFont(getFont().deriveFont(Font.BOLD));}
                    else if(etat.contains("Critique")){setForeground(ORANGE);setFont(getFont().deriveFont(Font.BOLD));}
                    else if(etat.contains("Attention")){setForeground(new Color(200,160,0));setFont(getFont().deriveFont(Font.BOLD));}
                    else{setForeground(VERT);setFont(getFont().deriveFont(Font.BOLD));}
                }
                return this;
            }
        });

        tableLots.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){if(e.getClickCount()==2)chargerLot();}});

        JScrollPane sc = new JScrollPane(tableLots);
        sc.setBorder(null);
        p.add(sc, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        actions.setBackground(new Color(248,250,254));
        actions.setBorder(BorderFactory.createMatteBorder(1,0,0,0,GRIS_BRD));
        JButton bMod = btn("✏  Modifier",  BLEU);
        JButton bSup = btn("🗑  Supprimer", ROUGE);
        bMod.addActionListener(e->chargerLot());
        bSup.addActionListener(e->supprimerLot());
        actions.add(bMod); actions.add(bSup);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelFormLot() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(0,8,0,0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(BLEU_CL);
        titre.setBorder(new EmptyBorder(11,18,11,18));
        JLabel tl = new JLabel("Nouveau lot / Modification");
        tl.setFont(new Font("Segoe UI",Font.BOLD,14)); tl.setForeground(BLEU);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(18,22,10,22));

        // Médicament (FK id_medicament)
        champs.add(lbl("Médicament *"));
        champs.add(Box.createVerticalStrut(5));
        cbMedLot = new JComboBox<>();
        cbMedLot.setFont(new Font("Segoe UI",Font.PLAIN,13));
        cbMedLot.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        cbMedLot.setAlignmentX(0f);
        cbMedLot.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList<?> list,Object value,
                    int index,boolean sel,boolean foc){
                super.getListCellRendererComponent(list,value,index,sel,foc);
                if(value instanceof String[]) setText(((String[])value)[1]);
                return this;
            }
        });
        champs.add(cbMedLot);
        champs.add(Box.createVerticalStrut(14));

        // numero_lot
        champs.add(lbl("Numéro de lot *"));
        champs.add(Box.createVerticalStrut(5));
        fNumLot = fieldF("ex: LOT-2025-1");
        champs.add(fNumLot);
        champs.add(Box.createVerticalStrut(14));

        // date_fabrication
        champs.add(lbl("Date de fabrication *"));
        champs.add(Box.createVerticalStrut(5));
         fDateFab = new JDateChooser();
        fDateFab.setDateFormatString("yyyy-MM-dd");
        fDateFab.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        
        champs.add(fDateFab);
        champs.add(Box.createVerticalStrut(10));

        // quantite
        champs.add(lbl("Quantité *"));
        champs.add(Box.createVerticalStrut(5));
        fQteLot = fieldF("ex: 100");
        champs.add(fQteLot);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null); sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,12));
        boutons.setBackground(new Color(247,250,254));
        boutons.setBorder(BorderFactory.createMatteBorder(1,0,0,0,GRIS_BRD));
        JButton bAnn  = btn("Annuler",      GRIS_TXT);
        JButton bSave = btn("Enregistrer",  BLANC); bSave.setBackground(BLEU);
        bAnn.addActionListener(e->viderFormLot());
        bSave.addActionListener(e->sauvegarderLot());
        boutons.add(bAnn); boutons.add(bSave);
        card.add(boutons, BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    /* ══════════════════════════════════════════════════════════════════
       ONGLET 3 — ALERTES
    ══════════════════════════════════════════════════════════════════ */
    private JPanel panelAlertes() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14,14,14,14));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(0,0,10,0));
        lblNbAlertes = new JLabel("Chargement…");
        lblNbAlertes.setFont(new Font("Segoe UI",Font.BOLD,14));
        lblNbAlertes.setForeground(ROUGE);
        JButton bMarquer = btn("✓  Tout marquer comme lu", VERT);
        bMarquer.addActionListener(e->marquerToutLu());
        head.add(lblNbAlertes, BorderLayout.WEST);
        head.add(bMarquer, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);

        // Colonnes = Alerte : id_alerte | type_alerte | message | date_alerte | est_lue | id_medicament
        String[] cols = {"ID","Type","Message","Date","Lu","Médicament"};
        modeleAlertes = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return false;}};
        tableAlertes = new JTable(modeleAlertes);
        tableAlertes.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tableAlertes.setRowHeight(34);
        tableAlertes.setShowVerticalLines(false);
        tableAlertes.setGridColor(GRIS_LIG);
        tableAlertes.setSelectionBackground(BLEU_CL);
        tableAlertes.setBackground(BLANC);
        tableAlertes.setFocusable(false);

        JTableHeader h = tableAlertes.getTableHeader();
        h.setFont(new Font("Segoe UI",Font.BOLD,12));
        h.setBackground(new Color(218,228,242));
        h.setForeground(new Color(35,55,100));
        h.setPreferredSize(new Dimension(0,34));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {70,110,280,100,60,160};
        for(int i=0;i<lrg.length;i++) tableAlertes.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        tableAlertes.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,
                    boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setFont(new Font("Segoe UI",Font.PLAIN,13));
                setBorder(new EmptyBorder(0,10,0,10));
                if(!sel){
                    // Ligne non lue = fond légèrement coloré
                    Object lu = modeleAlertes.getValueAt(row,4);
                    boolean estLue = lu != null && (Boolean)lu;
                    setBackground(estLue ? (row%2==0?BLANC:new Color(248,250,254)) : new Color(255,245,235));
                    setForeground(new Color(30,42,65));
                }
                if(col==1 && v!=null){
                    String type=v.toString();
                    if(type.toLowerCase().contains("pérem") || type.toLowerCase().contains("expir"))
                        setForeground(ROUGE);
                    else if(type.toLowerCase().contains("stock"))
                        setForeground(ORANGE);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                if(col==4 && v instanceof Boolean){
                    setText((Boolean)v ? "✓ Lu" : "● Non lu");
                    setForeground((Boolean)v ? VERT : ROUGE);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return this;
            }
        });

        JScrollPane sc = new JScrollPane(tableAlertes);
        sc.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,9));
        actions.setOpaque(false);
        JButton bMarquerUn = btn("✓  Marquer lu",  VERT);
        JButton bSuppr     = btn("🗑  Supprimer",   ROUGE);
        bMarquerUn.addActionListener(e->marquerLu());
        bSuppr.addActionListener(e->supprimerAlerte());
        actions.add(bMarquerUn); actions.add(bSuppr);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — STOCK
    ══════════════════════════════════════════════════════════════════ */
    private void chargerStock() {
        modeleStock.setRowCount(0);
        String sql = "SELECT s.id_stock, m.nom_commercial, m.code_cip, " +
                     "s.quantite_disponible, s.seuil_alerte " +
                     "FROM \"Stock\" s JOIN \"Medicament\" m ON s.id_medicament = m.id_medicament " +
                     "ORDER BY m.nom_commercial";
        try(Connection c=DBconnexion.getConnection();
            Statement  s=c.createStatement();
            ResultSet  rs=s.executeQuery(sql)){
            while(rs.next()){
                int qte    = rs.getInt("quantite_disponible");
                int seuil  = rs.getInt("seuil_alerte");
                String etat = qte <= 0 ? "✕ Rupture" : qte <= seuil ? "⚠ Faible" : "✓ OK";
                modeleStock.addRow(new Object[]{
                    rs.getString("id_stock"),
                    rs.getString("nom_commercial"),
                    rs.getString("code_cip"),
                    qte, seuil, etat
                });
            }
        }catch(SQLException ex){err("Chargement stock :\n"+ex.getMessage());}
    }

    private void filtrerStock(){
        TableRowSorter<DefaultTableModel> sorter=new TableRowSorter<>(modeleStock);
        tableStock.setRowSorter(sorter);
        String t=rechercheStock.getText().trim();
        sorter.setRowFilter(t.isEmpty()?null:RowFilter.regexFilter("(?i)"+t));
    }

    private void modifierSeuil(){
        int row=tableStock.getSelectedRow();
        if(row<0){warn("Sélectionnez un médicament.");return;}
        int mr=tableStock.convertRowIndexToModel(row);
        String id    = modeleStock.getValueAt(mr,0).toString();
        String nom   = modeleStock.getValueAt(mr,1).toString();
        String seuil = JOptionPane.showInputDialog(this,
            "Nouveau seuil d'alerte pour « "+nom+" » :",
            modeleStock.getValueAt(mr,4).toString());
        if(seuil==null||seuil.trim().isEmpty()) return;
        try{
            int s=Integer.parseInt(seuil.trim());
            try(Connection c=DBconnexion.getConnection();
                PreparedStatement ps=c.prepareStatement(
                    "UPDATE \"Stock\" SET seuil_alerte=? WHERE id_stock=?")){
                ps.setInt(1,s); ps.setString(2,id);
                ps.executeUpdate();
                chargerStock();
            }
        }catch(NumberFormatException ex){warn("Valeur numérique invalide.");}
        catch(SQLException ex){err("Mise à jour impossible :\n"+ex.getMessage());}
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — LOTS
    ══════════════════════════════════════════════════════════════════ */
    private void chargerMedicamentsPourLot(){
        try(Connection c=DBconnexion.getConnection();
            Statement  s=c.createStatement();
            ResultSet  rs=s.executeQuery("SELECT id_medicament,nom_commercial FROM \"Medicament\" ORDER BY nom_commercial")){
            cbMedLot.removeAllItems();
            while(rs.next()) cbMedLot.addItem(new String[]{rs.getString(1),rs.getString(2)});
        }catch(SQLException ex){ System.err.println(ex.getMessage()); }
    }

    private void chargerLots(){
        modeleLots.setRowCount(0);
        String sql ="SELECT l.id_lot,l.numero_lot, m.nom_commercial, " +
             "l.date_fabrication, l.date_expiration, l.quantite, " +
             "(l.date_expiration - CURRENT_DATE) AS jours_restants " +
             "FROM \"Lot_medicament\" l " +
             "JOIN \"Medicament\" m ON l.id_medicament=m.id_medicament " +
             "ORDER BY l.date_expiration";
        try(Connection c=DBconnexion.getConnection();
            Statement  s=c.createStatement();
            ResultSet  rs=s.executeQuery(sql)){
            while(rs.next()){
                int joursRestants = rs.getInt("jours_restants");
String etat;
if (joursRestants < 0)
    etat = "✕ Expiré (" + (-joursRestants) + " jours)";
else if (joursRestants <= 30)
    etat = "⚠ Critique (" + joursRestants + " jours)";
else if (joursRestants <= 90)
    etat = "△ Attention (" + joursRestants + " jours)";
else
    etat = "✓ OK";
modeleLots.addRow(new Object[]{
    rs.getString("id_lot"),
    rs.getString("numero_lot"),
    rs.getString("nom_commercial"),
    rs.getDate("date_fabrication"),
    rs.getInt("quantite"),
    joursRestants + " jours",
    etat
                });
            }
        }catch(SQLException ex){err("Chargement lots :\n"+ex.getMessage());}
    }

    private void chargerLot(){
        int row=tableLots.getSelectedRow();
        if(row<0){warn("Sélectionnez un lot.");return;}
        int mr=tableLots.convertRowIndexToModel(row);
        idLotEnCours=modeleLots.getValueAt(mr,0).toString();
        fNumLot.setText(modeleLots.getValueAt(mr,1).toString());
        fDateFab.setDate((Date) modeleLots.getValueAt(mr,3));
        fQteLot.setText(modeleLots.getValueAt(mr,4).toString());
        modeEditLot=true;
    }

    private void sauvegarderLot(){
        String numLot  = fNumLot.getText().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(fDateFab.getDate()==null){
    warn("Choisissez une date.");
    return;
}
        String date = sdf.format(fDateFab.getDate());
        String sQte    = fQteLot.getText().trim();
        if(numLot.isEmpty()||date.isEmpty()||sQte.isEmpty()){
            warn("Tous les champs sont obligatoires."); return;}
        int qte;
        try{ qte=Integer.parseInt(sQte); }
        catch(NumberFormatException ex){ warn("Quantité invalide."); return; }
        String idMed="";
        if(cbMedLot.getSelectedItem() instanceof String[])
            idMed=((String[])cbMedLot.getSelectedItem())[0];
        try{
            if(modeEditLot){
                try(Connection c = DBconnexion.getConnection();
    PreparedStatement ps = c.prepareStatement(
        "UPDATE \"Lot_medicament\" SET numero_lot=?, date_fabrication=?, quantite=?, id_medicament=? WHERE id_lot=?")) {

    ps.setString(1, numLot);
    ps.setDate(2, new java.sql.Date(fDateFab.getDate().getTime()));
    ps.setInt(3, qte);
    ps.setInt(4, Integer.parseInt(idMed));
    ps.setInt(5, Integer.parseInt(idLotEnCours)); // id_lot

    ps.executeUpdate();
                }
            }else{
                try(Connection c = DBconnexion.getConnection();
    PreparedStatement ps = c.prepareStatement(
        "INSERT INTO \"Lot_medicament\"(numero_lot, date_fabrication, quantite, id_medicament) VALUES(?, ?, ?, ?)")) {
    
    ps.setString(1, numLot);
    ps.setDate(2, new java.sql.Date(fDateFab.getDate().getTime()));
    ps.setInt(3, qte);
    ps.setInt(4, Integer.parseInt(idMed)); // id_medicament

    ps.executeUpdate();
}
            }
            chargerLots(); viderFormLot();
            JOptionPane.showMessageDialog(this,"Lot enregistré.","Succès",JOptionPane.INFORMATION_MESSAGE);
        }catch(SQLException ex){err("Enregistrement impossible :\n"+ex.getMessage());}
    }

    private void supprimerLot(){
        int row=tableLots.getSelectedRow();
        if(row<0){warn("Sélectionnez un lot.");return;}
        int mr=tableLots.convertRowIndexToModel(row);
        String id=modeleLots.getValueAt(mr,0).toString();
        if(JOptionPane.showConfirmDialog(this,"Supprimer ce lot ?","Confirmation",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION) return;
        try(Connection c=DBconnexion.getConnection();
            PreparedStatement ps=c.prepareStatement("DELETE FROM \"Lot_medicament\" WHERE id_lot=?")){
            ps.setString(1,id); ps.executeUpdate();
            chargerLots(); viderFormLot();
        }catch(SQLException ex){err("Suppression impossible :\n"+ex.getMessage());}
    }

    private void viderFormLot(){
        fNumLot.setText(""); fDateFab.setDate(null); fQteLot.setText("");
        if(cbMedLot.getItemCount()>0) cbMedLot.setSelectedIndex(0);
        modeEditLot=false; idLotEnCours=null;
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — ALERTES
    ══════════════════════════════════════════════════════════════════ */
    private void chargerAlertes(){
        modeleAlertes.setRowCount(0);
        String sql = "SELECT a.id_alerte, a.type_alerte, a.message, a.date_alerte, " +
                     "a.est_lue, m.nom_commercial " +
                     "FROM \"Alerte\" a LEFT JOIN \"Medicament\" m ON a.id_medicament=m.id_medicament " +
                     "ORDER BY a.est_lue ASC, a.date_alerte DESC";
        try(Connection c=DBconnexion.getConnection();
            Statement  s=c.createStatement();
            ResultSet  rs=s.executeQuery(sql)){
            int nonLues=0;
            while(rs.next()){
                boolean lu=rs.getBoolean("est_lue");
                if(!lu) nonLues++;
                modeleAlertes.addRow(new Object[]{
                    rs.getString("id_alerte"),
                    rs.getString("type_alerte"),
                    rs.getString("message"),
                    rs.getDate("date_alerte"),
                    lu,
                    rs.getString("nom_commercial")
                });
            }
            lblNbAlertes.setText(nonLues > 0 ?
                "⚠  " + nonLues + " alerte(s) non lue(s)" :
                "✓  Toutes les alertes sont lues");
            lblNbAlertes.setForeground(nonLues > 0 ? ROUGE : VERT);
        }catch(SQLException ex){err("Chargement alertes :\n"+ex.getMessage());}
    }

    private void marquerLu(){
        int row=tableAlertes.getSelectedRow();
        if(row<0){warn("Sélectionnez une alerte.");return;}
        int mr=tableAlertes.convertRowIndexToModel(row);
        String id=modeleAlertes.getValueAt(mr,0).toString();
        try(Connection c=DBconnexion.getConnection();
            PreparedStatement ps=c.prepareStatement(
                "UPDATE \"Alerte\" SET est_lue=true WHERE id_alerte=?")){
            ps.setString(1,id); ps.executeUpdate(); chargerAlertes();
        }catch(SQLException ex){err(ex.getMessage());}
    }

    private void marquerToutLu(){
        try(Connection c=DBconnexion.getConnection();
            Statement  s=c.createStatement()){
            s.executeUpdate("UPDATE \"Alerte\" SET est_lue=true WHERE est_lue=false");
            chargerAlertes();
        }catch(SQLException ex){err(ex.getMessage());}
    }

    private void supprimerAlerte(){
        int row=tableAlertes.getSelectedRow();
        if(row<0){warn("Sélectionnez une alerte.");return;}
        int mr=tableAlertes.convertRowIndexToModel(row);
        String id=modeleAlertes.getValueAt(mr,0).toString();
        if(JOptionPane.showConfirmDialog(this,"Supprimer cette alerte ?","Confirmation",
            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)!=JOptionPane.YES_OPTION) return;
        try(Connection c=DBconnexion.getConnection();
            PreparedStatement ps=c.prepareStatement("DELETE FROM \"Alerte\" WHERE id_alerte=?")){
            ps.setString(1,id); ps.executeUpdate(); chargerAlertes();
        }catch(SQLException ex){err(ex.getMessage());}
    }

    /* ── HELPERS UI ──────────────────────────────────────────────────── */
    private JLabel lbl(String t){
        JLabel l=new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,12));
        l.setForeground(new Color(45,62,88)); l.setAlignmentX(0f); return l;
    }
    private JTextField fieldF(String ph){
        JTextField f=new JTextField(){
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                if(getText().isEmpty()&&!isFocusOwner()){
                    g.setColor(new Color(175,188,205));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(ph,10,getHeight()/2+5);
                }
            }
        };
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        f.setAlignmentX(0f);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD,1),
            BorderFactory.createEmptyBorder(5,9,5,9)));
        f.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BLEU,2),
                    BorderFactory.createEmptyBorder(4,8,4,8)));}
            public void focusLost(FocusEvent e){
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GRIS_BRD,1),
                    BorderFactory.createEmptyBorder(5,9,5,9)));}
        });
        return f;
    }
    private JButton btn(String txt,Color fg){
        JButton b=new JButton(txt);
        b.setFont(new Font("Segoe UI Emoji",Font.BOLD,12));
        b.setForeground(fg); b.setBackground(BLANC);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD,1,true),
            BorderFactory.createEmptyBorder(6,14,6,14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private void warn(String m){JOptionPane.showMessageDialog(this,m,"Attention",JOptionPane.WARNING_MESSAGE);}
    private void err(String m) {JOptionPane.showMessageDialog(this,m,"Erreur",JOptionPane.ERROR_MESSAGE);}
    
    
    private void genererAlertesStock() {
    try(Connection c = DBconnexion.getConnection();
        PreparedStatement psCheck = c.prepareStatement(
            "SELECT id_alerte FROM \"Alerte\" WHERE type_alerte=? AND id_medicament=?");
        PreparedStatement psInsert = c.prepareStatement(
            "INSERT INTO \"Alerte\"(type_alerte,message,date_alerte,est_lue,id_medicament) VALUES(?,?,?,?,?)")) {
        
        String sqlStock = "SELECT s.id_stock, s.quantite_disponible, s.seuil_alerte, m.id_medicament, m.nom_commercial " +
                          "FROM \"Stock\" s JOIN \"Medicament\" m ON s.id_medicament = m.id_medicament";
        try(Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sqlStock)) {
            while(rs.next()) {
                int qte = rs.getInt("quantite_disponible");
                int seuil = rs.getInt("seuil_alerte");
                if(qte <= seuil) {
                    String type = "Stock faible";
                    String msg = "Le médicament " + rs.getString("nom_commercial") + " est en dessous du seuil.";
                    
                    psCheck.setString(1, type);
                    psCheck.setInt(2, rs.getInt("id_medicament"));
                    ResultSet rsCheck = psCheck.executeQuery();
                    if(!rsCheck.next()) { // éviter doublon
                        psInsert.setString(1, type);
                        psInsert.setString(2, msg);
                        psInsert.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                        psInsert.setBoolean(4, false);
                        psInsert.setInt(5, rs.getInt("id_medicament"));
                        psInsert.executeUpdate();
                    }
                }
            }
        }
    } catch(SQLException ex){ err("Erreur génération alertes stock :\n"+ex.getMessage()); }
}

private void genererAlertesPeremption() {
    try(Connection c = DBconnexion.getConnection();
        PreparedStatement psCheck = c.prepareStatement(
            "SELECT id_alerte FROM \"Alerte\" WHERE type_alerte=? AND id_medicament=?");
        PreparedStatement psInsert = c.prepareStatement(
            "INSERT INTO \"Alerte\"(type_alerte,message,date_alerte,est_lue,id_medicament) VALUES(?,?,?,?,?)")) {
        
        String sqlLots = "SELECT l.id_lot, l.date_fabrication, l.quantite, m.id_medicament, m.nom_commercial " +
                         "FROM \"Lot_Medicament\" l JOIN \"Medicament\" m ON l.id_medicament=m.id_medicament";
        try(Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sqlLots)) {
            while(rs.next()) {
                java.sql.Date dateFab = rs.getDate("date_fabrication");
                long jours = (System.currentTimeMillis() - dateFab.getTime()) / (1000*60*60*24);
                String etat = "";
                if(jours > 365*2) etat = "Expiré";
                else if(jours > 300) etat = "Critique";
                else if(jours > 200) etat = "Attention";
                if(!etat.isEmpty()) {
                    String type = "Péremption " + etat;
                    String msg = "Le lot du médicament " + rs.getString("nom_commercial") + " est " + etat + ".";
                    
                    psCheck.setString(1, type);
                    psCheck.setInt(2, rs.getInt("id_medicament"));
                    ResultSet rsCheck = psCheck.executeQuery();
                    if(!rsCheck.next()) { // éviter doublon
                        psInsert.setString(1, type);
                        psInsert.setString(2, msg);
                        psInsert.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                        psInsert.setBoolean(4, false);
                        psInsert.setInt(5, rs.getInt("id_medicament"));
                        psInsert.executeUpdate();
                    }
                }
            }
        }
    } catch(SQLException ex){ err("Erreur génération alertes péremption :\n"+ex.getMessage()); }
}
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionStock();
    });
}

}