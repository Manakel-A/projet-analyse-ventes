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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import database.DBconnexion;

/**
 * GestionVente.java  — Point de vente / Caisse
 *
 * Tables utilisées :
 *   Vente      : id_vente | numero_facture | date_vente | total | mode_paiement | id_pharmacien
 *   Ligne_Vente: id_ligne | quantite | prix_unitaire | sous_total | id_vente | id_medicament
 *   Medicament : id_medicament | nom_commercial | prix_vente | (stock via table Stock)
 *   Caisse     : id_operation | type_operation | montant | date_operation | description
 *   Stock      : id_stock | quantite_disponible | seuil_alerte | id_medicament
 */
public class GestionVente extends JFrame {

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

    // ID pharmacien connecté (passé depuis TableauDeBord)
    private final String idPharmacien;

    // ── Composants recherche médicament ──
    private JTextField     champRechercheMed;
    private JTable         tableMeds;
    private DefaultTableModel modeleMeds;

    // ── Composants panier (lignes de vente) ──
    private JTable         tablePanier;
    private DefaultTableModel modelePanier;

    // ── Résumé paiement ──
    private JLabel         lblTotal, lblNbArticles;
    private JComboBox<String> cbModePaiement;
    private JTextField     champMontantRecu;
    private JLabel         lblMonnaie;
    private JButton        btnValider, btnAnnulerVente;

    // ── Historique ventes ──
    private JTable         tableHistorique;
    private DefaultTableModel modeleHistorique;

    public GestionVente(String idPharmacien) {
        this.idPharmacien = idPharmacien;
        buildUI();
        chargerMedicaments();
        chargerHistorique();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Ventes / Caisse");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);

        root.add(topBar(), BorderLayout.NORTH);

        // Zone principale : gauche (catalogue + panier) | droite (paiement + historique)
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, panelGauche(), panelDroit());
        split.setDividerLocation(820);
        split.setDividerSize(4);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke("F2"), "valider");

root.getActionMap().put("valider", new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
        validerVente();
    }
});
        setVisible(true);
    }

    /* ── BARRE TOP ───────────────────────────────────────────────────── */
    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 155, 75)); // vert caisse
        p.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("🛒"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Point de Vente / Caisse");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17)); ti.setForeground(BLANC);

        // Numéro de facture auto
        String numFact = "FAC-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());
        JLabel lblFact = new JLabel("  |  Facture : " + numFact);
        lblFact.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFact.setForeground(new Color(200, 240, 215));

        g.add(ic); g.add(ti); g.add(lblFact);
        p.add(g, BorderLayout.WEST);

        JLabel date = new JLabel(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "  ");
        date.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        date.setForeground(new Color(200, 240, 215));
        p.add(date, BorderLayout.EAST);
        return p;
    }

    /* ── PANEL GAUCHE : catalogue + panier ──────────────────────────── */
    private JPanel panelGauche() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 7));

        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            panelCatalogue(), panelPanier());
        split2.setDividerLocation(260);
        split2.setDividerSize(4);
        split2.setBorder(null);
        p.add(split2, BorderLayout.CENTER);
        return p;
    }

    /** Catalogue de médicaments avec recherche */
    private JPanel panelCatalogue() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(BLANC);
        p.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        // En-tête catalogue
        JPanel head = new JPanel(new BorderLayout(8, 0));
        head.setBackground(new Color(235, 245, 255));
        head.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel titre = new JLabel("💊  Catalogue médicaments");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titre.setForeground(BLEU);

        champRechercheMed = new JTextField();
        champRechercheMed.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        champRechercheMed.putClientProperty("JTextField.placeholderText",
            "🔍  Rechercher par nom ou code CIP…");
        champRechercheMed.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        champRechercheMed.setPreferredSize(new Dimension(250, 30));
        champRechercheMed.addKeyListener(new KeyAdapter() {

    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            scannerCIP(champRechercheMed.getText().trim());
        } else {
            filtrerMedicaments();
        }
    }
});

        head.add(titre, BorderLayout.WEST);
        head.add(champRechercheMed, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);

        // Tableau catalogue
        String[] cols = {"ID", "Code CIP", "Nom commercial", "Forme", "Prix vente", "Stock dispo"};
        modeleMeds = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMeds = new JTable(modeleMeds);
        tableMeds.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableMeds.setRowHeight(28);
        tableMeds.setShowVerticalLines(false);
        tableMeds.setGridColor(GRIS_LIG);
        tableMeds.setSelectionBackground(BLEU_CL);
        tableMeds.setSelectionForeground(BLEU);
        tableMeds.setBackground(BLANC);
        tableMeds.setFocusable(false);

        JTableHeader h = tableMeds.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 30));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLEU));

        int[] lrg = {60, 100, 200, 90, 80, 80};
        for (int i = 0; i < lrg.length; i++)
            tableMeds.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        // Colorer stock faible
        tableMeds.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(248, 250, 254));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 4) { setForeground(VERT); setFont(getFont().deriveFont(Font.BOLD)); }
                if (col == 5 && v != null) {
                    try {
                        int qty = Integer.parseInt(v.toString());
                        setForeground(qty <= 0 ? ROUGE : qty < 10 ? ORANGE : new Color(30,42,65));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } catch (NumberFormatException ignored) {}
                }
                return this;
            }
        });

        // Double-clic → ajouter au panier
        tableMeds.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) ajouterAuPanier();
            }
        });

        JScrollPane sc = new JScrollPane(tableMeds);
        sc.setBorder(null);
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        foot.setBackground(new Color(248, 250, 254));
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton btnAjouter = btnVert("➕  Ajouter au panier");
        btnAjouter.addActionListener(e -> ajouterAuPanier());
        foot.add(btnAjouter);
        foot.add(new JLabel("  ou double-cliquez sur un médicament"));
        p.add(foot, BorderLayout.SOUTH);
        return p;
    }

    /** Panier = lignes de vente en cours */
    private JPanel panelPanier() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLANC);
        p.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(240, 255, 245));
        head.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel titre = new JLabel("🛒  Panier — Lignes de vente");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titre.setForeground(VERT);
        lblNbArticles = new JLabel("0 article(s)");
        lblNbArticles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNbArticles.setForeground(GRIS_TXT);
        head.add(titre, BorderLayout.WEST);
        head.add(lblNbArticles, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);

        // Colonnes = Ligne_Vente : quantite | prix_unitaire | sous_total | id_medicament
        String[] cols = {"id_medicament", "Médicament", "Forme", "Prix unit.", "Qté", "Sous-total"};
        modelePanier = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; } // qté éditable
        };
        tablePanier = new JTable(modelePanier);
        tablePanier.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePanier.setRowHeight(32);
        tablePanier.setShowVerticalLines(false);
        tablePanier.setGridColor(GRIS_LIG);
        tablePanier.setBackground(BLANC);
        tablePanier.setFocusable(true);
        tablePanier.getColumnModel().getColumn(0).setMaxWidth(0); // Cacher id
        tablePanier.getColumnModel().getColumn(0).setMinWidth(0);
        tablePanier.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Recalculer sous_total quand quantité change
        modelePanier.addTableModelListener(e -> {
            if (e.getColumn() == 4) recalculerPanier();
        });

        JTableHeader h = tablePanier.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(225, 245, 230));
        h.setForeground(new Color(20, 80, 40));
        h.setPreferredSize(new Dimension(0, 30));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, VERT));

        // Colonne sous-total en vert
        tablePanier.getColumn("Sous-total").setCellRenderer(new DefaultTableCellRenderer() {
            { setForeground(VERT); setFont(new Font("Segoe UI", Font.BOLD, 13)); }
        });

        JScrollPane sc = new JScrollPane(tablePanier);
        sc.setBorder(null);
        p.add(sc, BorderLayout.CENTER);

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        foot.setBackground(new Color(248, 254, 250));
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton btnSuppr = new JButton("🗑  Retirer ligne");
        btnSuppr.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnSuppr.setForeground(ROUGE);
        btnSuppr.setBackground(BLANC);
        btnSuppr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        btnSuppr.setFocusPainted(false);
        btnSuppr.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSuppr.addActionListener(e -> retirerLigne());
        foot.add(btnSuppr);
        p.add(foot, BorderLayout.SOUTH);
        return p;
    }

    /* ── PANEL DROIT : paiement + historique ─────────────────────────── */
    private JPanel panelDroit() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 7, 14, 14));

        p.add(panelPaiement(),   BorderLayout.NORTH);
        p.add(panelHistorique(), BorderLayout.CENTER);
        return p;
    }

    /** Résumé + mode paiement + validation */
    private JPanel panelPaiement() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel titre = new JLabel("💰  Résumé & Paiement");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titre.setForeground(BLEU);
        titre.setAlignmentX(0f);
        card.add(titre);
        card.add(Box.createVerticalStrut(14));

        // Total
        JPanel rowTotal = new JPanel(new BorderLayout());
        rowTotal.setOpaque(false);
        rowTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lblTitTotal = new JLabel("TOTAL À PAYER");
        lblTitTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitTotal.setForeground(GRIS_TXT);
        lblTotal = new JLabel("0,00 FCFA");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(VERT);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        rowTotal.add(lblTitTotal, BorderLayout.WEST);
        rowTotal.add(lblTotal,    BorderLayout.EAST);
        card.add(rowTotal);
        card.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setForeground(GRIS_BRD);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));

        // Mode de paiement (colonne mode_paiement de Vente)
        card.add(labelF("Mode de paiement"));
        card.add(Box.createVerticalStrut(5));
        cbModePaiement = new JComboBox<>(new String[]{
            "Espèces", "Carte bancaire", "Mobile Money", "Chèque", "Virement"});
        cbModePaiement.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbModePaiement.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbModePaiement.setAlignmentX(0f);
        cbModePaiement.addActionListener(e -> {
            boolean especes = "Espèces".equals(cbModePaiement.getSelectedItem());
            champMontantRecu.setEnabled(especes);
            lblMonnaie.setVisible(especes);
        });
        card.add(cbModePaiement);
        card.add(Box.createVerticalStrut(12));

        // Montant reçu (espèces uniquement)
        card.add(labelF("Montant reçu (FCFA)"));
        card.add(Box.createVerticalStrut(5));
        champMontantRecu = new JTextField("0.00");
        champMontantRecu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        champMontantRecu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        champMontantRecu.setAlignmentX(0f);
        champMontantRecu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        champMontantRecu.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { calculerMonnaie(); }
        });
        card.add(champMontantRecu);
        card.add(Box.createVerticalStrut(8));

        // Monnaie à rendre
        lblMonnaie = new JLabel("Monnaie à rendre : 0,00 FCFA");
        lblMonnaie.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMonnaie.setForeground(ORANGE);
        lblMonnaie.setAlignmentX(0f);
        card.add(lblMonnaie);
        card.add(Box.createVerticalStrut(16));

        // Boutons
        btnValider = new JButton("✔  Valider la vente");
        btnValider.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnValider.setForeground(BLANC);
        btnValider.setBackground(VERT);
        btnValider.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 120, 55), 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        btnValider.setFocusPainted(false);
        btnValider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnValider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnValider.setAlignmentX(0f);
        btnValider.addActionListener(e -> validerVente());
        card.add(btnValider);
        card.add(Box.createVerticalStrut(8));

        btnAnnulerVente = new JButton("✕  Annuler / Nouveau");
        btnAnnulerVente.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        btnAnnulerVente.setForeground(ROUGE);
        btnAnnulerVente.setBackground(BLANC);
        btnAnnulerVente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btnAnnulerVente.setFocusPainted(false);
        btnAnnulerVente.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnnulerVente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnAnnulerVente.setAlignmentX(0f);
        btnAnnulerVente.addActionListener(e -> viderPanier());
        card.add(btnAnnulerVente);

        return card;
    }

    /** Historique des ventes du jour */
    private JPanel panelHistorique() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLANC);
        p.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(BLEU_CL);
        head.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel titre = new JLabel("📋  Historique du jour");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titre.setForeground(BLEU);
        JButton btnActu = new JButton("↻");
        btnActu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnActu.setForeground(BLEU);
        btnActu.setBackground(BLEU_CL);
        btnActu.setBorderPainted(false);
        btnActu.setFocusPainted(false);
        btnActu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActu.addActionListener(e -> chargerHistorique());
        head.add(titre, BorderLayout.WEST);
        head.add(btnActu, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);

        // Colonnes = Vente : numero_facture | date_vente | total | mode_paiement
        String[] cols = {"N° Facture", "Date", "Total (FCFA)", "Paiement"};
        modeleHistorique = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableHistorique = new JTable(modeleHistorique);
        tableHistorique.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHistorique.setRowHeight(28);
        tableHistorique.setShowVerticalLines(false);
        tableHistorique.setGridColor(GRIS_LIG);
        tableHistorique.setSelectionBackground(BLEU_CL);
        tableHistorique.setBackground(BLANC);
        tableHistorique.setFocusable(false);

        JTableHeader h = tableHistorique.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 28));
        h.setReorderingAllowed(false);

        JScrollPane sc = new JScrollPane(tableHistorique);
        sc.setBorder(null);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       LOGIQUE MÉTIER
    ══════════════════════════════════════════════════════════════════ */

    /** Charger catalogue médicaments + stock disponible */
    private void chargerMedicaments() {
        modeleMeds.setRowCount(0);
        String sql = "SELECT m.id_medicament, m.code_cip, m.nom_commercial, m.forme, " +
                     "m.prix_vente, COALESCE(s.quantite_disponible, 0) AS qte_dispo " +
                     "FROM \"Medicament\" m " +
                     "LEFT JOIN \"Stock\" s ON m.id_medicament = s.id_medicament " +
                     "ORDER BY m.nom_commercial";
        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            while (rs.next()) {
                modeleMeds.addRow(new Object[]{
                    rs.getString("id_medicament"),
                    rs.getString("code_cip"),
                    rs.getString("nom_commercial"),
                    rs.getString("forme"),
                    String.format("%.2f FCFA", rs.getDouble("prix_vente")),
                    rs.getInt("qte_dispo")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Chargement médicaments impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrerMedicaments() {

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeleMeds);
    tableMeds.setRowSorter(sorter);

    String t = champRechercheMed.getText().trim();

    sorter.setRowFilter(
        t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t)
    );
}
    private void scannerCIP(String cip) {

    for (int i = 0; i < modeleMeds.getRowCount(); i++) {

        if (modeleMeds.getValueAt(i, 1).toString().equals(cip)) {

            tableMeds.setRowSelectionInterval(i, i);
            ajouterAuPanier();
            champRechercheMed.setText("");
            return;
        }
    }

    JOptionPane.showMessageDialog(this,
            "Médicament non trouvé pour le CIP : " + cip);
}

    /** Ajouter médicament sélectionné au panier */
    private void ajouterAuPanier() {
        int row = tableMeds.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un médicament.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE); return;
        }
        int mr = tableMeds.convertRowIndexToModel(row);
        String idMed    = modeleMeds.getValueAt(mr, 0).toString();
        String nomMed   = modeleMeds.getValueAt(mr, 2).toString();
        String forme    = modeleMeds.getValueAt(mr, 3).toString();
        int    qteDispo = Integer.parseInt(modeleMeds.getValueAt(mr, 5).toString());
        String prixStr  = modeleMeds.getValueAt(mr, 4).toString().replace(" FCFA","").replace(",",".");
        double prix     = Double.parseDouble(prixStr);

        if (qteDispo <= 0) {
            JOptionPane.showMessageDialog(this,
                "Stock insuffisant pour « " + nomMed + " ».",
                "Rupture de stock", JOptionPane.WARNING_MESSAGE); return;
        }

        // Vérifier si déjà dans le panier → incrémenter quantité
        for (int i = 0; i < modelePanier.getRowCount(); i++) {
            if (modelePanier.getValueAt(i, 0).equals(idMed)) {
                int qteActuelle = Integer.parseInt(modelePanier.getValueAt(i, 4).toString());
                if (qteActuelle >= qteDispo) {
                    JOptionPane.showMessageDialog(this,
                        "Quantité maximum atteinte pour ce médicament.", "Attention",
                        JOptionPane.WARNING_MESSAGE); return;
                }
                modelePanier.setValueAt(qteActuelle + 1, i, 4);
                return;
            }
        }

        // Nouvelle ligne panier
        double sousTotal = prix * 1;
        modelePanier.addRow(new Object[]{idMed, nomMed, forme,
            String.format("%.2f", prix), 1, String.format("%.2f", sousTotal)});
        recalculerPanier();
    }

    private void retirerLigne() {
        int row = tablePanier.getSelectedRow();
        if (row < 0) return;
        modelePanier.removeRow(row);
        recalculerPanier();
    }

    private void recalculerPanier() {
        double total = 0;
        for (int i = 0; i < modelePanier.getRowCount(); i++) {
            try {
                double prix = Double.parseDouble(modelePanier.getValueAt(i, 3).toString().replace(",","."));
                int    qte  = Integer.parseInt(modelePanier.getValueAt(i, 4).toString());
                double st   = prix * qte;
                modelePanier.setValueAt(String.format("%.2f", st), i, 5);
                total += st;
            } catch (NumberFormatException ignored) {}
        }
        lblTotal.setText(String.format("%.2f FCFA", total));
        lblNbArticles.setText(modelePanier.getRowCount() + " article(s)");
        calculerMonnaie();
    }

    private void calculerMonnaie() {
        try {
            double totalD = Double.parseDouble(
                lblTotal.getText().replace(" FCFA","").replace(",","."));
            double recu = Double.parseDouble(
                champMontantRecu.getText().replace(",","."));
            double monnaie = recu - totalD;
            lblMonnaie.setText("Monnaie à rendre : " + String.format("%.2f FCFA", Math.max(0, monnaie)));
            lblMonnaie.setForeground(monnaie < 0 ? ROUGE : ORANGE);
        } catch (NumberFormatException ignored) {}
    }

    private void viderPanier() {
        modelePanier.setRowCount(0);
        lblTotal.setText("0,00 FCFA");
        lblNbArticles.setText("0 article(s)");
        champMontantRecu.setText("0.00");
        lblMonnaie.setText("Monnaie à rendre : 0,00 FCFA");
    }

    /** Valider vente → INSERT Vente + INSERT Ligne_Vente (×n) + UPDATE Stock + INSERT Caisse */
    private void validerVente() {

    if (modelePanier.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Le panier est vide.",
                "Aucun article", JOptionPane.WARNING_MESSAGE);
        return;
    }

    double total = Double.parseDouble(
            lblTotal.getText().replace(" FCFA", "").replace(",", "."));

    String modePaiement = (String) cbModePaiement.getSelectedItem();

    if ("Espèces".equals(modePaiement)) {
        try {
            double recu = Double.parseDouble(champMontantRecu.getText().replace(",", "."));
            if (recu < total) {
                JOptionPane.showMessageDialog(this,
                        "Montant insuffisant. Il manque "
                        + String.format("%.2f FCFA", total - recu));
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Montant reçu invalide.");
            return;
        }
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer la vente ?\nTotal : " + total + " FCFA\nMode : " + modePaiement,
            "Confirmation", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    Connection conn = null;

    try {

        conn = DBconnexion.getConnection();
        conn.setAutoCommit(false);

        String numFacture = "FAC-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // 1️⃣ INSERT VENTE
        String sqlVente =
                "INSERT INTO \"Vente\"(numero_facture,date_vente,total,mode_paiement,id_pharmacien) " +
                "VALUES(?,CURRENT_DATE,?,?,?)";

        int idVente;

        try (PreparedStatement ps = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, numFacture);
            ps.setDouble(2, total);
            ps.setString(3, modePaiement);
            ps.setString(4, idPharmacien);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            idVente = rs.getInt(1);
        }

        // 2️⃣ PREPARED STATEMENTS (réutilisés → plus rapide)

        String sqlLigne =
                "INSERT INTO \"Ligne_Vente\"(id_ligne,quantite,prix_unitaire,sous_total,id_vente,id_medicament) " +
                "VALUES(?,?,?,?,?,?)";

        String sqlStock =
                "UPDATE \"Stock\" SET quantite_disponible = quantite_disponible - ? " +
                "WHERE id_medicament=?";

        PreparedStatement psLigne = conn.prepareStatement(sqlLigne);
        PreparedStatement psStock = conn.prepareStatement(sqlStock);

        for (int i = 0; i < modelePanier.getRowCount(); i++) {

            String idMed = modelePanier.getValueAt(i, 0).toString();
            int qte = Integer.parseInt(modelePanier.getValueAt(i, 4).toString());
            double prix = Double.parseDouble(modelePanier.getValueAt(i, 3).toString().replace(",", "."));
            double st = Double.parseDouble(modelePanier.getValueAt(i, 5).toString().replace(",", "."));

            // Ligne vente
            psLigne.setString(1, "LV-" + System.currentTimeMillis() + "-" + i);
            psLigne.setInt(2, qte);
            psLigne.setDouble(3, prix);
            psLigne.setDouble(4, st);
            psLigne.setInt(5, idVente);
            psLigne.setString(6, idMed);
            psLigne.executeUpdate();

            // Stock
            psStock.setInt(1, qte);
            psStock.setString(2, idMed);
            psStock.executeUpdate();
            
            String sqlCheck =
"SELECT quantite_disponible,seuil_alerte FROM \"Stock\" WHERE id_medicament=?";

try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

    psCheck.setString(1, idMed);

    ResultSet rs = psCheck.executeQuery();

    if (rs.next()) {

        int qteRestante = rs.getInt("quantite_disponible");
        int seuil = rs.getInt("seuil_alerte");

        if (qteRestante <= seuil) {

            JOptionPane.showMessageDialog(this,
                    "⚠ Stock faible pour ce médicament\nQuantité restante : " + qte);
        }
    }
}
        }

        // 3️⃣ CAISSE
        String sqlCaisse =
                "INSERT INTO \"Caisse\"(id_operation,type_operation,montant,date_operation,description) " +
                "VALUES(?,?,?,CURRENT_DATE,?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlCaisse)) {

            ps.setString(1, "OP-" + System.currentTimeMillis());
            ps.setString(2, "Recette");
            ps.setDouble(3, total);
            ps.setString(4, "Vente " + numFacture + " — " + modePaiement);

            ps.executeUpdate();
        }

        conn.commit();

        JOptionPane.showMessageDialog(this,
                "✓ Vente enregistrée\nFacture : " + numFacture);

        viderPanier();
        chargerMedicaments();
        chargerHistorique();

    } catch (Exception ex) {

        try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}

        JOptionPane.showMessageDialog(this,
                "Erreur vente : " + ex.getMessage());

    } finally {

        try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ignored) {}

    }
}
    /** Charger l'historique des ventes du jour */
    private void chargerHistorique() {
        modeleHistorique.setRowCount(0);
        String sql = "SELECT numero_facture, date_vente, total, mode_paiement " +
                     "FROM \"Vente\" WHERE date_vente = CURRENT_DATE " +
                     "ORDER BY numero_facture DESC";
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                modeleHistorique.addRow(new Object[]{
                    rs.getString("numero_facture"),
                    rs.getDate("date_vente"),
                    String.format("%.2f FCFA", rs.getDouble("total")),
                    rs.getString("mode_paiement")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Historique : " + ex.getMessage());
        }
    }

    /* ── HELPERS UI ──────────────────────────────────────────────────── */
    private JLabel labelF(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(45, 62, 88));
        l.setAlignmentX(0f);
        return l;
    }
    private JButton btnVert(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        b.setForeground(VERT);
        b.setBackground(new Color(235, 255, 240));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 210, 170), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new GestionVente("PH001");
    });
}
}
