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
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import database.DBconnexion;

/**
 * GestionFournisseur.java
 * Table Fournisseur : id_fournisseur | nom | telephone | email | adresse
 * Table Approvisionnement : id_approvisionnement | date_approvisionnement |
 *                           quantite | prix_achat | id_medicament | id_fournisseur
 */
public class GestionFournisseur extends JFrame {

    private static final Color BLEU      = new Color(25, 90, 160);
    private static final Color BLEU_CL   = new Color(235, 243, 255);
    private static final Color BLANC     = Color.WHITE;
    private static final Color GRIS_FOND = new Color(245, 247, 250);
    private static final Color GRIS_LIG  = new Color(230, 235, 242);
    private static final Color GRIS_BRD  = new Color(200, 210, 225);
    private static final Color GRIS_TXT  = new Color(90, 100, 120);
    private static final Color VERT      = new Color(35, 155, 75);
    private static final Color ROUGE     = new Color(200, 50, 50);
    private static final Color VIOLET    = new Color(100, 50, 160);

    // Onglets
    private JTabbedPane onglets;

    // ── Fournisseurs ──
    private JTable            tableFourn;
    private DefaultTableModel modeleFourn;
    private JTextField        rechFourn;
    private JTextField        fNomF, fTelF, fEmailF, fAdresseF;
    private JButton           btnSaveFourn;
    private boolean           modeEditFourn = false;
    private String            idFournEnCours;

    // ── Approvisionnement ──
    private JTable            tableAppro;
    private DefaultTableModel modeleAppro;
    private JTextField        fQteAppro, fPrixAchatAppro;
    private JDateChooser      fDateAppro;
    private JComboBox<String[]> cbMedAppro, cbFournAppro;
    private JButton           btnSaveAppro;
    private boolean           modeEditAppro = false;
    private String            idApproEnCours;

    public GestionFournisseur() {
        buildUI();
        chargerFournisseurs();
        chargerApprovisionnements();
        chargerCombos();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Fournisseurs & Approvisionnements");
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
        onglets.addTab("🏢  Fournisseurs",       panelFournisseurs());
        onglets.addTab("🚛  Approvisionnements", panelApprovisionnement());
        root.add(onglets, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(VIOLET);
        p.setBorder(new EmptyBorder(13, 22, 13, 22));
        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("🏢"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Fournisseurs & Approvisionnements");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17)); ti.setForeground(BLANC);
        g.add(ic); g.add(ti);
        p.add(g, BorderLayout.WEST);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       ONGLET 1 — FOURNISSEURS
    ══════════════════════════════════════════════════════════════════ */
    private JPanel panelFournisseurs() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelTableFourn(), panelFormFourn());
        split.setDividerLocation(640);
        split.setDividerSize(4);
        split.setBorder(null);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelTableFourn() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(0, 0, 0, 7));

        // Recherche
        JPanel barre = new JPanel(new BorderLayout(8, 0));
        barre.setOpaque(false); barre.setBorder(new EmptyBorder(0, 0, 10, 0));
        rechFourn = new JTextField();
        rechFourn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rechFourn.putClientProperty("JTextField.placeholderText", "🔍  Rechercher fournisseur…");
        rechFourn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        rechFourn.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrerFournisseurs(); }
        });
        barre.add(rechFourn, BorderLayout.CENTER);
        p.add(barre, BorderLayout.NORTH);

        // Colonnes = attributs réels Fournisseur
        String[] cols = {"ID", "Nom", "Téléphone", "Email", "Adresse"};
        modeleFourn = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableFourn = new JTable(modeleFourn);
        tableFourn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableFourn.setRowHeight(34);
        tableFourn.setShowVerticalLines(false);
        tableFourn.setGridColor(GRIS_LIG);
        tableFourn.setSelectionBackground(BLEU_CL);
        tableFourn.setBackground(BLANC);
        tableFourn.setFocusable(false);

        JTableHeader h = tableFourn.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 34));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {70, 160, 110, 160, 200};
        for (int i = 0; i < lrg.length; i++)
            tableFourn.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        tableFourn.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(248, 250, 254));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 1) setFont(getFont().deriveFont(Font.BOLD));
                return this;
            }
        });

        tableFourn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) chargerFourn();
            }
        });

        JScrollPane sc = new JScrollPane(tableFourn);
        sc.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        actions.setOpaque(false);
        JButton bNew = btn("+ Nouveau",      BLEU);
        JButton bMod = btn("✏  Modifier",    BLEU);
        JButton bSup = btn("🗑  Supprimer",  ROUGE);
        bNew.addActionListener(e -> viderFormFourn());
        bMod.addActionListener(e -> chargerFourn());
        bSup.addActionListener(e -> supprimerFourn());
        actions.add(bNew); actions.add(bMod); actions.add(bSup);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelFormFourn() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(0, 7, 0, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(BLEU_CL);
        titre.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel tl = new JLabel("Informations du fournisseur");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14)); tl.setForeground(BLEU);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(18, 22, 10, 22));

        // Attributs réels : nom | telephone | email | adresse
        fNomF     = rowF(champs, "Nom du fournisseur *", "ex:L2M Mondoukpè ");
        fTelF     = rowF(champs, "Téléphone *",          "ex: 01 23 45 67 89");
        fEmailF   = rowF(champs, "Email",                "ex: contact@gmail.com");

        champs.add(lbl("Adresse *"));
        champs.add(Box.createVerticalStrut(5));
        fAdresseF = new JTextField();
        styleField(fAdresseF);
        fAdresseF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        champs.add(fAdresseF);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null); sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        boutons.setBackground(new Color(247, 250, 254));
        boutons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton bAnn = btn("Annuler", GRIS_TXT);
        btnSaveFourn = btn("Enregistrer", BLANC); btnSaveFourn.setBackground(BLEU);
        bAnn.addActionListener(e -> viderFormFourn());
        btnSaveFourn.addActionListener(e -> sauvegarderFourn());
        boutons.add(bAnn); boutons.add(btnSaveFourn);
        card.add(boutons, BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    /* ══════════════════════════════════════════════════════════════════
       ONGLET 2 — APPROVISIONNEMENTS
    ══════════════════════════════════════════════════════════════════ */
    private JPanel panelApprovisionnement() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 14));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelTableAppro(), panelFormAppro());
        split.setDividerLocation(660);
        split.setDividerSize(4);
        split.setBorder(null);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelTableAppro() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(0, 0, 0, 7));

        // Colonnes = Approvisionnement : id_approvisionnement | date_approvisionnement |
        //            quantite | prix_achat | id_medicament | id_fournisseur
        String[] cols = {"ID", "Date", "Médicament", "Fournisseur", "Quantité", "Prix achat"};
        modeleAppro = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableAppro = new JTable(modeleAppro);
        tableAppro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableAppro.setRowHeight(34);
        tableAppro.setShowVerticalLines(false);
        tableAppro.setGridColor(GRIS_LIG);
        tableAppro.setSelectionBackground(BLEU_CL);
        tableAppro.setBackground(BLANC);
        tableAppro.setFocusable(false);

        JTableHeader h = tableAppro.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 34));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {70, 100, 180, 160, 80, 90};
        for (int i = 0; i < lrg.length; i++)
            tableAppro.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        tableAppro.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(248, 250, 254));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 5) { setForeground(VERT); setFont(getFont().deriveFont(Font.BOLD)); }
                return this;
            }
        });

        tableAppro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) chargerAppro();
            }
        });

        JScrollPane sc = new JScrollPane(tableAppro);
        sc.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        actions.setOpaque(false);
        JButton bMod = btn("✏  Modifier",   BLEU);
        JButton bSup = btn("🗑  Supprimer", ROUGE);
        JButton bAct = btn("↻  Actualiser", GRIS_TXT);
        bMod.addActionListener(e -> chargerAppro());
        bSup.addActionListener(e -> supprimerAppro());
        bAct.addActionListener(e -> chargerApprovisionnements());
        actions.add(bMod); actions.add(bSup);
        actions.add(Box.createHorizontalStrut(8)); actions.add(bAct);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelFormAppro() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(0, 7, 0, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(BLEU_CL);
        titre.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel tl = new JLabel("Nouvel approvisionnement");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14)); tl.setForeground(BLEU);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(18, 22, 10, 22));

        // Médicament (FK id_medicament)
        champs.add(lbl("Médicament *"));
        champs.add(Box.createVerticalStrut(5));
        cbMedAppro = new JComboBox<>();
        cbMedAppro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbMedAppro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbMedAppro.setAlignmentX(0f);
        cbMedAppro.setRenderer(comboRenderer());
        champs.add(cbMedAppro);
        champs.add(Box.createVerticalStrut(13));

        // Fournisseur (FK id_fournisseur)
        champs.add(lbl("Fournisseur *"));
        champs.add(Box.createVerticalStrut(5));
        cbFournAppro = new JComboBox<>();
        cbFournAppro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFournAppro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbFournAppro.setAlignmentX(0f);
        cbFournAppro.setRenderer(comboRenderer());
        champs.add(cbFournAppro);
        champs.add(Box.createVerticalStrut(13));

        // date_approvisionnement
            champs.add(lbl("Date approvisionnement *"));
champs.add(Box.createVerticalStrut(5));

fDateAppro = new JDateChooser();
fDateAppro.setDateFormatString("yyyy-MM-dd");
fDateAppro.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
champs.add(fDateAppro);

        // quantite
        champs.add(lbl("Quantité *"));
        champs.add(Box.createVerticalStrut(5));
        fQteAppro = fieldF("ex: 200");
        champs.add(fQteAppro);
        champs.add(Box.createVerticalStrut(13));

        // prix_achat
        champs.add(lbl("Prix d'achat unitaire (FCFA) *"));
        champs.add(Box.createVerticalStrut(5));
        fPrixAchatAppro = fieldF("ex: 3.50");
        champs.add(fPrixAchatAppro);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null); sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        boutons.setBackground(new Color(247, 250, 254));
        boutons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton bAnn = btn("Annuler", GRIS_TXT);
        btnSaveAppro = btn("Enregistrer", BLANC); btnSaveAppro.setBackground(BLEU);
        bAnn.addActionListener(e -> viderFormAppro());
        btnSaveAppro.addActionListener(e -> sauvegarderAppro());
        boutons.add(bAnn); boutons.add(btnSaveAppro);
        card.add(boutons, BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — FOURNISSEURS
    ══════════════════════════════════════════════════════════════════ */
    private void chargerFournisseurs() {
        modeleFourn.setRowCount(0);
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                "SELECT id_fournisseur, nom, telephone, email, adresse FROM \"Fournisseur\" ORDER BY nom")) {
            while (rs.next())
                modeleFourn.addRow(new Object[]{
                    rs.getString("id_fournisseur"), rs.getString("nom"),
                    rs.getString("telephone"), rs.getString("email"), rs.getString("adresse")
                });
        } catch (SQLException ex) { err("Chargement fournisseurs :\n" + ex.getMessage()); }
    }

    private void filtrerFournisseurs() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeleFourn);
        tableFourn.setRowSorter(sorter);
        String t = rechFourn.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
    }

    private void chargerFourn() {
        int row = tableFourn.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un fournisseur."); return; }
        int mr = tableFourn.convertRowIndexToModel(row);
        idFournEnCours = s(modeleFourn.getValueAt(mr, 0));
        fNomF.setText(s(modeleFourn.getValueAt(mr, 1)));
        fTelF.setText(s(modeleFourn.getValueAt(mr, 2)));
        fEmailF.setText(s(modeleFourn.getValueAt(mr, 3)));
        fAdresseF.setText(s(modeleFourn.getValueAt(mr, 4)));
        modeEditFourn = true;
        btnSaveFourn.setText("Mettre à jour");
    }

    private void sauvegarderFourn() {
    String nom  = fNomF.getText().trim();
    String tel  = fTelF.getText().trim();
    String mail = fEmailF.getText().trim();
    String adr  = fAdresseF.getText().trim();

    if (nom.isEmpty() || tel.isEmpty()) {
        warn("Nom et téléphone sont obligatoires.");
        return;
    }

    try {
        if (modeEditFourn) {

            try (Connection c = DBconnexion.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                "UPDATE \"Fournisseur\" SET nom=?,telephone=?,email=?,adresse=? WHERE id_fournisseur=?")) {

                ps.setString(1, nom);
                ps.setString(2, tel);
                ps.setString(3, mail);
                ps.setString(4, adr);
                ps.setInt(5, Integer.parseInt(idFournEnCours));

                ps.executeUpdate();
            }

        } else {

            try (Connection c = DBconnexion.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                "INSERT INTO \"Fournisseur\" (nom,telephone,email,adresse) VALUES(?,?,?,?)")) {

                ps.setString(1, nom);
                ps.setString(2, tel);
                ps.setString(3, mail);
                ps.setString(4, adr);

                ps.executeUpdate();
            }
        }

        chargerFournisseurs();
        chargerCombos();
        viderFormFourn();

        JOptionPane.showMessageDialog(this,
                "Fournisseur enregistré.",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
        err("Enregistrement impossible :\n" + ex.getMessage());
    }
}

    private void supprimerFourn() {
        int row = tableFourn.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un fournisseur."); return; }
        int mr = tableFourn.convertRowIndexToModel(row);
        String id = s(modeleFourn.getValueAt(mr, 0));
        if (JOptionPane.showConfirmDialog(this, "Supprimer ce fournisseur ?",
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM \"Fournisseur\" WHERE id_fournisseur=?")) {
            ps.setInt(1, Integer.parseInt(id)); ps.executeUpdate();
            chargerFournisseurs(); chargerCombos(); viderFormFourn();
        } catch (SQLException ex) { err("Suppression impossible :\n" + ex.getMessage()); }
    }

    private void viderFormFourn() {
        fNomF.setText(""); fTelF.setText(""); fEmailF.setText(""); fAdresseF.setText("");
        modeEditFourn = false; idFournEnCours = null;
        btnSaveFourn.setText("Enregistrer");
        tableFourn.clearSelection();
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — APPROVISIONNEMENTS
    ══════════════════════════════════════════════════════════════════ */
    private void chargerCombos() {
        // Médicaments
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT id_medicament,nom_commercial FROM \"Medicament\" ORDER BY nom_commercial")) {
            cbMedAppro.removeAllItems();
            while (rs.next())
                cbMedAppro.addItem(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException ex) { System.err.println(ex.getMessage()); }

        // Fournisseurs
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id_fournisseur,nom FROM \"Fournisseur\" ORDER BY nom")) {
            cbFournAppro.removeAllItems();
            while (rs.next())
                cbFournAppro.addItem(new String[]{rs.getString(1), rs.getString(2)});
        } catch (SQLException ex) { System.err.println(ex.getMessage()); }
    }

    private void chargerApprovisionnements() {
        modeleAppro.setRowCount(0);
        String sql ="SELECT a.id_approvisionnement, m.nom_commercial, f.nom, a.date_approvisionnement, a.quantite, a.prix_achat " +
                                 "FROM \"Approvisionnement\" a " +
                    "JOIN \"Medicament\" m ON a.id_medicament = m.id_medicament " +
"JOIN \"Fournisseur\" f ON a.id_fournisseur = f.id_fournisseur " +
"ORDER BY a.date_approvisionnement DESC";
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next())
                modeleAppro.addRow(new Object[]{
                    rs.getString("id_approvisionnement"),
                    rs.getDate("date_approvisionnement"),
                    rs.getString("nom_commercial"),
                    rs.getString("nom"),
                    rs.getInt("quantite"),
                    String.format("%.2f FCFA", rs.getDouble("prix_achat"))
                });
        } catch (SQLException ex) { err("Chargement approvisionnements :\n" + ex.getMessage()); }
    }

    private void chargerAppro() {
        int row = tableAppro.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un approvisionnement."); return; }
        int mr = tableAppro.convertRowIndexToModel(row);
        idApproEnCours = s(modeleAppro.getValueAt(mr, 0));
        fDateAppro.setDate((java.util.Date) modeleAppro.getValueAt(mr, 1));
        fQteAppro.setText(s(modeleAppro.getValueAt(mr, 4)));
        fPrixAchatAppro.setText(s(modeleAppro.getValueAt(mr, 5)).replace(" FCFA",""));
        modeEditAppro = true;
        btnSaveAppro.setText("Mettre à jour");
    }

    private void sauvegarderAppro() {
        if (cbFournAppro.getSelectedItem() == null) {
    warn("Veuillez sélectionner un fournisseur.");
    return;
}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fDateAppro.getDate());
        String sQte  = fQteAppro.getText().trim();
        String sPrix = fPrixAchatAppro.getText().trim().replace(",",".");
        if (date.isEmpty() || sQte.isEmpty() || sPrix.isEmpty()) {
            warn("Tous les champs sont obligatoires."); return;
        }
        int    qte;
        double prix;
        try { qte  = Integer.parseInt(sQte); }
        catch (NumberFormatException ex) { warn("Quantité invalide."); return; }
        try { prix = Double.parseDouble(sPrix); }
        catch (NumberFormatException ex) { warn("Prix invalide."); return; }

        String[] med = (String[]) cbMedAppro.getSelectedItem();
String[] fourn = (String[]) cbFournAppro.getSelectedItem();

int idMed = Integer.parseInt(med[0]);
int idFourn = Integer.parseInt(fourn[0]);

        try {
            if (modeEditAppro) {
                try (Connection c = DBconnexion.getConnection();
                     PreparedStatement ps = c.prepareStatement(
                         "UPDATE \"Approvisionnement\" SET date_approvisionnement=?::date," +
                         "quantite=?,prix_achat=?,id_medicament=?,id_fournisseur=? " +
                         "WHERE id_approvisionnement=?")) {
                    ps.setDate(1, new java.sql.Date(fDateAppro.getDate().getTime())); ps.setInt(2,qte); ps.setDouble(3,prix);
                   ps.setInt(6, Integer.parseInt(idApproEnCours));
                    ps.executeUpdate();
                }
            } else {
                // INSERT + UPDATE Stock (ajouter qte au stock)
                Connection c = DBconnexion.getConnection();
                c.setAutoCommit(false);
                try {
                    try (PreparedStatement ps = c.prepareStatement(
                           "INSERT INTO \"Approvisionnement\"(date_approvisionnement,quantite,prix_achat,id_medicament,id_fournisseur) VALUES(?::date,?,?,?,?)")) {

                                ps.setDate(1, new java.sql.Date(fDateAppro.getDate().getTime())); 
                                ps.setInt(2, qte);
                                ps.setDouble(3, prix);
                                ps.setInt(4, idMed);
                                ps.setInt(5, idFourn);

                                ps.executeUpdate();
                    }
                    // Mettre à jour le stock
try (PreparedStatement ps = c.prepareStatement(
    "UPDATE \"Stock\" SET quantite_disponible = quantite_disponible + ? " +
    "WHERE id_medicament = ?")) {

    ps.setInt(1, qte);
    ps.setInt(2, idMed);

    ps.executeUpdate();
}                    c.commit();
                } catch (SQLException ex) {
                    c.rollback(); throw ex;
                } finally { c.setAutoCommit(true); }
            }
            chargerApprovisionnements(); viderFormAppro();
            JOptionPane.showMessageDialog(this,
                "Approvisionnement enregistré.\nStock mis à jour automatiquement.",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { err("Enregistrement impossible :\n" + ex.getMessage()); }
    }

    private void supprimerAppro() {
        int row = tableAppro.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un approvisionnement."); return; }
        int mr = tableAppro.convertRowIndexToModel(row);
        String id = s(modeleAppro.getValueAt(mr, 0));
        if (JOptionPane.showConfirmDialog(this,"Supprimer cet approvisionnement ?",
            "Confirmation",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM \"Approvisionnement\" WHERE id_approvisionnement=?")) {
            ps.setInt(1, Integer.parseInt(id)); ps.executeUpdate();
            chargerApprovisionnements(); viderFormAppro();
        } catch (SQLException ex) { err("Suppression impossible :\n" + ex.getMessage()); }
    }

    private void viderFormAppro() {
        fDateAppro.setDate(null); fQteAppro.setText(""); fPrixAchatAppro.setText("");
        if (cbMedAppro.getItemCount()  > 0) cbMedAppro.setSelectedIndex(0);
        if (cbFournAppro.getItemCount() > 0) cbFournAppro.setSelectedIndex(0);
        modeEditAppro = false; idApproEnCours = null;
        btnSaveAppro.setText("Enregistrer");
        tableAppro.clearSelection();
    }

    /* ── HELPERS UI ──────────────────────────────────────────────────── */
    private JTextField rowF(JPanel p, String label, String ph) {
        p.add(lbl(label)); p.add(Box.createVerticalStrut(5));
        JTextField f = fieldF(ph); p.add(f); p.add(Box.createVerticalStrut(13));
        return f;
    }
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,12));
        l.setForeground(new Color(45,62,88)); l.setAlignmentX(0f); return l;
    }
    private JTextField fieldF(String ph) {
        JTextField f = new JTextField() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
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
    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setAlignmentX(0f);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD,1),
            BorderFactory.createEmptyBorder(5,9,5,9)));
    }
    private JButton btn(String txt, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI Emoji",Font.BOLD,12));
        b.setForeground(fg); b.setBackground(BLANC);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD,1,true),
            BorderFactory.createEmptyBorder(6,14,6,14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private DefaultListCellRenderer comboRenderer() {
        return new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list,value,index,sel,foc);
                if (value instanceof String[]) setText(((String[])value)[1]);
                return this;
            }
        };
    }
    private String s(Object o) { return o == null ? "" : o.toString(); }
    private void warn(String m){JOptionPane.showMessageDialog(this,m,"Attention",JOptionPane.WARNING_MESSAGE);}
    private void err(String m) {JOptionPane.showMessageDialog(this,m,"Erreur",JOptionPane.ERROR_MESSAGE);}
   
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionFournisseur();
    });
}

}

