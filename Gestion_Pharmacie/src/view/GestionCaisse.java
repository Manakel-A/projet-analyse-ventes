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
import java.util.Date;
import database.DBconnexion;

/**
 * GestionCaisse.java
 * Table Caisse : id_operation | type_operation | montant | date_operation | description
 *
 * Flux réel pharmacie :
 *  - Chaque vente → INSERT automatique type "Recette"
 *  - Dépenses manuelles (achats, charges) → type "Dépense"
 *  - Bilan journalier : total recettes - total dépenses = solde net
 *  - Historique complet avec filtres par date et type
 */
public class GestionCaisse extends JFrame {

    private static final Color BLEU       = new Color(25, 90, 160);
    private static final Color BLEU_CL    = new Color(235, 243, 255);
    private static final Color BLANC      = Color.WHITE;
    private static final Color GRIS_FOND  = new Color(245, 247, 250);
    private static final Color GRIS_LIG   = new Color(230, 235, 242);
    private static final Color GRIS_BRD   = new Color(200, 210, 225);
    private static final Color GRIS_TXT   = new Color(90, 100, 120);
    private static final Color VERT       = new Color(35, 155, 75);
    private static final Color ROUGE      = new Color(200, 50, 50);
    private static final Color OR         = new Color(190, 130, 0);

    // Tableau historique
    private JTable            tableOps;
    private DefaultTableModel modeleOps;

    // KPIs du jour
    private JLabel lblRecettes, lblDepenses, lblSolde, lblNbOps;

    // Filtre
    private JTextField    champDate;
    private JComboBox<String> cbTypeFiltre;
    private JDateChooser champDateChooser;
    
    // Formulaire nouvelle opération
    private JComboBox<String> cbTypeOp;
    private JTextField        fMontant, fDescription;

    public GestionCaisse() {
        buildUI();
        chargerBilanJour();
        chargerOperations();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Caisse & Finance");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(GRIS_FOND);
        setContentPane(root);

        root.add(topBar(),    BorderLayout.NORTH);
        root.add(panelKPIs(), BorderLayout.CENTER);

        setVisible(true);
    }

    /* ── BARRE TOP ───────────────────────────────────────────────────── */
    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(OR);
        p.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("💰"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Caisse & Finance");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17)); ti.setForeground(BLANC);
        String today = new SimpleDateFormat("EEEE d MMMM yyyy", java.util.Locale.FRENCH)
            .format(new Date());
        JLabel dt = new JLabel("  —  " + today);
        dt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dt.setForeground(new Color(255, 235, 175));
        g.add(ic); g.add(ti); g.add(dt);
        p.add(g, BorderLayout.WEST);

        JButton bActu = btnStyle("↻  Actualiser", BLANC);
        bActu.addActionListener(e -> { chargerBilanJour(); chargerOperations(); });
        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        d.setOpaque(false); d.add(bActu);
        p.add(d, BorderLayout.EAST);
        return p;
    }

    /* ── KPIs + contenu principal ─────────────────────────────────────── */
    private JPanel panelKPIs() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(GRIS_FOND);
        outer.setBorder(new EmptyBorder(18, 18, 18, 18));

        // ── Ligne KPIs ─────────────────────────────────────────────────
        JPanel kpis = new JPanel(new GridLayout(1, 4, 14, 0));
        kpis.setOpaque(false);
        kpis.setPreferredSize(new Dimension(0, 110));

        lblRecettes = new JLabel("—");
        lblDepenses = new JLabel("—");
        lblSolde    = new JLabel("—");
        lblNbOps    = new JLabel("—");

        kpis.add(kpiCard("💵  Recettes du jour",  lblRecettes, VERT));
        kpis.add(kpiCard("📤  Dépenses du jour",  lblDepenses, ROUGE));
        kpis.add(kpiCard("🏦  Solde net du jour", lblSolde,    OR));
        kpis.add(kpiCard("📋  Opérations",        lblNbOps,    BLEU));
        outer.add(kpis, BorderLayout.NORTH);

        // ── Zone principale : tableau + formulaire ─────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelHistorique(), panelSaisie());
        split.setDividerLocation(680);
        split.setDividerSize(4);
        split.setBorder(null);
        outer.add(split, BorderLayout.CENTER);

        return outer;
    }

    private JPanel kpiCard(String titre, JLabel valeur, Color couleur) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(BLANC);
        c.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel bande = new JPanel(); bande.setBackground(couleur);
        bande.setPreferredSize(new Dimension(0, 5));
        c.add(bande, BorderLayout.NORTH);

        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setOpaque(false);
        corps.setBorder(new EmptyBorder(12, 16, 12, 16));

        valeur.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valeur.setForeground(couleur);
        valeur.setAlignmentX(0f);

        JLabel tl = new JLabel(titre);
        tl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        tl.setForeground(new Color(30, 45, 80));
        tl.setAlignmentX(0f);
        tl.setBorder(new EmptyBorder(6, 0, 0, 0));

        corps.add(valeur); corps.add(tl);
        c.add(corps, BorderLayout.CENTER);
        return c;
    }

    /* ── Historique des opérations ────────────────────────────────────── */
    private JPanel panelHistorique() {
    JPanel p = new JPanel(new BorderLayout(0, 0));
    p.setBackground(BLANC);
    p.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

    // ── En-tête avec titre et filtres
    JPanel head = new JPanel(new BorderLayout(8, 0));
    head.setBackground(new Color(255, 248, 230));
    head.setBorder(new EmptyBorder(8, 12, 8, 12));

    JLabel titre = new JLabel("📋  Historique des opérations");
    titre.setFont(new Font("Segoe UI", Font.BOLD, 13));
    titre.setForeground(OR);

    JPanel filtres = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    filtres.setOpaque(false);

    // ── JDateChooser
    champDateChooser = new JDateChooser();
    champDateChooser.setDate(new Date()); // date actuelle
    champDateChooser.setDateFormatString("yyyy-MM-dd"); // format affiché
    champDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    champDateChooser.setPreferredSize(new Dimension(100, 25));

    // ── ComboBox type
    cbTypeFiltre = new JComboBox<>(new String[]{"Toutes", "Recette", "Dépense"});
    cbTypeFiltre.setFont(new Font("Segoe UI", Font.PLAIN, 12));

    // ── Bouton Filtrer
    JButton bFiltrer = btnStyle("Filtrer", OR);
    bFiltrer.addActionListener(e -> chargerOperations());

    JLabel lblDateLbl = new JLabel("Date :");
    lblDateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblDateLbl.setForeground(GRIS_TXT);

    filtres.add(lblDateLbl);
    filtres.add(champDateChooser); // JDateChooser
    filtres.add(cbTypeFiltre);
    filtres.add(bFiltrer);

    head.add(titre, BorderLayout.WEST);
    head.add(filtres, BorderLayout.EAST);
    p.add(head, BorderLayout.NORTH);

    // ── Tableau des opérations
    String[] cols = {"ID", "Type", "Montant", "Date", "Description"};
    modeleOps = new DefaultTableModel(cols, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    tableOps = new JTable(modeleOps);
    tableOps.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tableOps.setRowHeight(34);
    tableOps.setShowVerticalLines(false);
    tableOps.setGridColor(GRIS_LIG);
    tableOps.setSelectionBackground(BLEU_CL);
    tableOps.setBackground(BLANC);
    tableOps.setFocusable(false);

    JTableHeader h = tableOps.getTableHeader();
    h.setFont(new Font("Segoe UI", Font.BOLD, 12));
    h.setBackground(new Color(218, 228, 242));
    h.setForeground(new Color(35, 55, 100));
    h.setPreferredSize(new Dimension(0, 34));
    h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, OR));
    h.setReorderingAllowed(false);

    int[] lrg = {80, 90, 100, 100, 260};
    for (int i = 0; i < lrg.length; i++)
        tableOps.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

    // Renderer : Recette=vert, Dépense=rouge
    tableOps.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(0, 10, 0, 10));
            if (!sel) {
                setBackground(row % 2 == 0 ? BLANC : new Color(255, 252, 242));
                setForeground(new Color(30, 42, 65));
            }
            Object type = modeleOps.getValueAt(row, 1);
            if (type != null) {
                boolean isRecette = type.toString().equalsIgnoreCase("Recette");
                if (col == 1) {
                    setForeground(isRecette ? VERT : ROUGE);
                    setFont(getFont().deriveFont(Font.BOLD));
                    setText(isRecette ? "▲ Recette" : "▼ Dépense");
                }
                if (col == 2) {
                    setForeground(isRecette ? VERT : ROUGE);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            return this;
        }
    });

    JScrollPane sc = new JScrollPane(tableOps);
    sc.setBorder(null);
    sc.getViewport().setBackground(BLANC);
    p.add(sc, BorderLayout.CENTER);

    // ── Barre actions
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    actions.setBackground(new Color(255, 252, 242));
    actions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
    JButton bSup = btnStyle("🗑  Supprimer", ROUGE);
    JButton bExp = btnStyle("📊  Bilan mensuel", BLEU);
    bSup.addActionListener(e -> supprimerOperation());
    bExp.addActionListener(e -> afficherBilanMensuel());
    actions.add(bSup);
    actions.add(bExp);
    p.add(actions, BorderLayout.SOUTH);

    // ── Charger les opérations au démarrage
    chargerOperations();

    return p;
}

    /* ── Formulaire saisie dépense manuelle ──────────────────────────── */
    private JPanel panelSaisie() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(0, 12, 0, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        // Titre
        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(new Color(255, 248, 230));
        titre.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel tl = new JLabel("Enregistrer une opération");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(OR);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        // Champs
        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(20, 22, 10, 22));

        // Info : les recettes sont auto depuis les ventes
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 255, 245));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 210, 170), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        infoPanel.setAlignmentX(0f);
        JLabel infoTxt = new JLabel(
            "<html><b style='color:#228B22'>ℹ</b>  Les recettes de ventes sont enregistrées " +
            "automatiquement.<br>Utilisez ce formulaire pour les dépenses manuelles.</html>");
        infoTxt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoTxt.setForeground(new Color(30, 90, 50));
        infoPanel.add(infoTxt, BorderLayout.CENTER);
        champs.add(infoPanel);
        champs.add(Box.createVerticalStrut(18));

        // Type opération (type_operation)
        champs.add(lbl("Type d'opération *"));
        champs.add(Box.createVerticalStrut(5));
        cbTypeOp = new JComboBox<>(new String[]{
            "Dépense", "Recette", "Remboursement", "Avance", "Autre"});
        cbTypeOp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTypeOp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbTypeOp.setAlignmentX(0f);
        champs.add(cbTypeOp);
        champs.add(Box.createVerticalStrut(14));

        // Montant (montant numeric)
        champs.add(lbl("Montant (FCFA) *"));
        champs.add(Box.createVerticalStrut(5));
        fMontant = fieldF("ex: 150.00");
        champs.add(fMontant);
        champs.add(Box.createVerticalStrut(14));

        // Description (description text)
        champs.add(lbl("Description *"));
        champs.add(Box.createVerticalStrut(5));
        fDescription = fieldF("ex: Achat fournitures, Électricité, Salaire…");
        champs.add(fDescription);
        champs.add(Box.createVerticalStrut(20));

        // Résumé des types de dépenses courantes pharmacie
        champs.add(lbl("Dépenses courantes (clic rapide) :"));
        champs.add(Box.createVerticalStrut(8));
        String[] depensesCourantes = {
            "Électricité/Eau", "Salaires", "Fournitures",
            "Maintenance", "Assurance", "Transport", "Medicaments/Produits"
        };
        JPanel raccourcis = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        raccourcis.setOpaque(false);
        raccourcis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        raccourcis.setAlignmentX(0f);
        for (String dep : depensesCourantes) {
            JButton chip = new JButton(dep);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setForeground(ROUGE);
            chip.setBackground(new Color(255, 240, 240));
            chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addActionListener(e -> {
                fDescription.setText(dep);
                cbTypeOp.setSelectedItem("Dépense");
                fMontant.requestFocus();
            });
            raccourcis.add(chip);
        }
        champs.add(raccourcis);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null); sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);

        // Boutons
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        boutons.setBackground(new Color(255, 252, 242));
        boutons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton bVider = btnStyle("Vider",       GRIS_TXT);
        JButton bSave  = btnStyle("Enregistrer", BLANC);
        bSave.setBackground(OR);
        bVider.addActionListener(e -> viderForm());
        bSave.addActionListener(e -> enregistrerOperation());
        boutons.add(bVider); boutons.add(bSave);
        card.add(boutons, BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — CAISSE
    ══════════════════════════════════════════════════════════════════ */

    /** Bilan du jour depuis table Caisse */
    private void chargerBilanJour() {
        String sql = "SELECT type_operation, SUM(montant) AS total, COUNT(*) AS nb " +
                     "FROM \"Caisse\" WHERE date_operation = CURRENT_DATE GROUP BY type_operation";
        double recettes = 0, depenses = 0;
        int nbOps = 0;
        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("type_operation");
                double total = rs.getDouble("total");
                nbOps += rs.getInt("nb");
                if (type != null && type.equalsIgnoreCase("Recette")) recettes += total;
                else depenses += total;
            }
        } catch (SQLException ex) {
            System.err.println("Bilan jour : " + ex.getMessage());
        }
        double solde = recettes - depenses;

        lblRecettes.setText(String.format("%.2f FCFA", recettes));
        lblDepenses.setText(String.format("%.2f FCFA", depenses));
        lblSolde.setText(String.format("%.2f FCFA", solde));
        lblSolde.setForeground(solde >= 0 ? VERT : ROUGE);
        lblNbOps.setText(String.valueOf(nbOps));
    }

    /** Charger opérations filtrées par date et type */
    private void chargerOperations() {
    modeleOps.setRowCount(0); // vide le tableau

    // ── Récupération de la date depuis JDateChooser
    Date selectedDate = champDateChooser.getDate();
    String date = "";
    if (selectedDate != null) {
        date = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
    }

    String type = (String) cbTypeFiltre.getSelectedItem();

    // ── Construction de la requête SQL
    StringBuilder sql = new StringBuilder(
        "SELECT id_operation, type_operation, montant, date_operation, description " +
        "FROM \"Caisse\" WHERE 1=1");
    if (!date.isEmpty()) sql.append(" AND date_operation = '").append(date).append("'");
    if (type != null && !"Toutes".equals(type))
        sql.append(" AND LOWER(type_operation) = LOWER('").append(type).append("')");
    sql.append(" ORDER BY date_operation DESC, id_operation DESC");

    // ── Exécution de la requête et remplissage du tableau
    try (Connection c = DBconnexion.getConnection();
         Statement s = c.createStatement();
         ResultSet rs = s.executeQuery(sql.toString())) {
        while (rs.next()) {
            modeleOps.addRow(new Object[]{
                rs.getString("id_operation"),
                rs.getString("type_operation"),
                String.format("%.2f FCFA", rs.getDouble("montant")),
                rs.getDate("date_operation"),
                rs.getString("description")
            });
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Chargement impossible :\n" + ex.getMessage(),
            "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
    /** INSERT INTO Caisse */
    private void enregistrerOperation() {
        String type = (String) cbTypeOp.getSelectedItem();
        String sMontant = fMontant.getText().trim().replace(",", ".");
        String desc = fDescription.getText().trim();

        if (sMontant.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Montant et description sont obligatoires.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE); return;
        }
        double montant;
        try { montant = Double.parseDouble(sMontant); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide (ex: 150.00).",
                "Format incorrect", JOptionPane.WARNING_MESSAGE); return;
        }
        if (montant <= 0) {
            JOptionPane.showMessageDialog(this, "Le montant doit être positif.",
                "Erreur", JOptionPane.WARNING_MESSAGE); return;
        }

        String sql = "INSERT INTO \"Caisse\"(type_operation, montant, " +
                     "date_operation, description) VALUES(?, ?, CURRENT_DATE, ?)";
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ps.setDouble(2, montant);
            ps.setString(3, desc);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Opération enregistrée.", "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            viderForm();
            chargerBilanJour();
            chargerOperations();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Enregistrement impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** DELETE FROM Caisse */
    private void supprimerOperation() {
        int row = tableOps.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une opération.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE); return;
        }
        int mr  = tableOps.convertRowIndexToModel(row);
        String id = modeleOps.getValueAt(mr, 0).toString();
        String type = modeleOps.getValueAt(mr, 1).toString();

        if (type.toLowerCase().contains("recette")) {
            int conf = JOptionPane.showConfirmDialog(this,
                "⚠ Cette recette est liée à une vente.\nSuppression uniquement comptable.\nContinuer ?",
                "Attention", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf != JOptionPane.YES_OPTION) return;
        }

        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM \"Caisse\" WHERE id_operation=?")) {
            ps.setString(1, id); ps.executeUpdate();
            chargerBilanJour(); chargerOperations();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Suppression impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Bilan mensuel : résumé par mois */
    private void afficherBilanMensuel() {
        String sql = "SELECT TO_CHAR(date_operation,'YYYY-MM') AS mois, " +
                     "type_operation, SUM(montant) AS total " +
                     "FROM \"Caisse\" GROUP BY mois, type_operation ORDER BY mois DESC";
        StringBuilder sb = new StringBuilder();
        String moisCourant = "";
        double rec = 0, dep = 0;

        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            while (rs.next()) {
                String mois = rs.getString("mois");
                String type = rs.getString("type_operation");
                double total = rs.getDouble("total");
                if (!mois.equals(moisCourant)) {
                    if (!moisCourant.isEmpty()) {
                        sb.append("  → Solde : ").append(String.format("%.2f FCFA", rec - dep))
                          .append("\n─────────────────────────────────\n");
                    }
                    moisCourant = mois;
                    rec = dep = 0;
                    sb.append("\n📅  ").append(mois).append("\n");
                }
                if (type.equalsIgnoreCase("Recette")) { rec += total; sb.append("  ▲ Recettes : ").append(String.format("%.2f FCFA%n", total)); }
                else { dep += total; sb.append("  ▼ Dépenses : ").append(String.format("%.2f FCFA%n", total)); }
            }
            if (!moisCourant.isEmpty())
                sb.append("  → Solde : ").append(String.format("%.2f FCFA", rec - dep));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE); return;
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(255, 252, 240));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(380, 400));
        JOptionPane.showMessageDialog(this, sp, "📊  Bilan Mensuel", JOptionPane.PLAIN_MESSAGE);
    }

    private void viderForm() {
        fMontant.setText(""); fDescription.setText("");
        cbTypeOp.setSelectedIndex(0);
    }

    /* ── HELPERS UI ──────────────────────────────────────────────────── */
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(45, 62, 88)); l.setAlignmentX(0f); return l;
    }
    private JTextField fieldF(String ph) {
        JTextField f = new JTextField() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(175, 188, 205));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(ph, 10, getHeight() / 2 + 5);
                }
            }
        };
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(0f);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(OR, 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GRIS_BRD, 1),
                    BorderFactory.createEmptyBorder(5, 9, 5, 9)));
            }
        });
        return f;
    }
    private JButton btnStyle(String txt, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        b.setForeground(fg); b.setBackground(BLANC);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionCaisse();
    });
}
}
