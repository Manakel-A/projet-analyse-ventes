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
import java.text.SimpleDateFormat;
import java.util.Date;
import database.DBconnexion;

/**
 * TableauDeBord.java  — Hub central de PharmacieL2M
 *
 * Flux réel d'une pharmacie couvert :
 *  1. Matin → vérifier les alertes stock + péremptions
 *  2. Journée → saisir les ventes (point de vente)
 *  3. Réception livraison → approvisionnement fournisseur
 *  4. Gestion référentiel → médicaments, catégories, pharmaciens
 *  5. Fin de journée → bilan caisse (recettes - dépenses)
 */
public class Tableaudebord extends JFrame {

    private static final Color BLEU_NUIT  = new Color(15, 30, 60);
    private static final Color BLEU_FOND  = new Color(22, 45, 90);
    private static final Color BLEU_ACT   = new Color(35, 75, 150);
    private static final Color BLANC      = Color.WHITE;
    private static final Color GRIS_FOND  = new Color(245, 247, 252);
    private static final Color GRIS_BRD   = new Color(200, 210, 228);
    private static final Color VERT       = new Color(35, 155, 75);
    private static final Color ROUGE      = new Color(200, 50, 50);
    private static final Color ORANGE     = new Color(210, 120, 0);
    private static final Color OR         = new Color(190, 130, 0);
    private static final Color CYAN       = new Color(0, 130, 150);
    private static final Color VIOLET     = new Color(100, 50, 160);

    private final String idPharmacien;
    private final String nomComplet;
    private final String role;

    private JLabel lblMedicaments, lblAlertes, lblVentesJour, lblStockFaible, lblRecettesJour;
    private JPanel panelContenu;
    private JButton btnActif = null;

    public Tableaudebord(String idPharmacien, String nom, String prenom, String role) {
        this.idPharmacien = idPharmacien;
        this.nomComplet   = prenom + " " + nom;
        this.role         = role;
        buildUI();
        ImageIcon logo = new ImageIcon(getClass().getResource("logo.png"));
        setIconImage(logo.getImage());
        chargerKPIs();
        new Timer(60000, e -> chargerKPIs()).start();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Tableau de bord");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(headerBar(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            sidebar(), panelPrincipal());
        split.setDividerLocation(230);
        split.setDividerSize(0);
        split.setBorder(null);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
        setVisible(true);
    }

    /* ── HEADER ──────────────────────────────────────────────────────── */
    private JPanel headerBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU_NUIT);
        p.setPreferredSize(new Dimension(0, 56));

        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        g.setOpaque(false);
        JPanel logoBox = new JPanel(new BorderLayout());
        logoBox.setBackground(new Color(0, 100, 200));
        logoBox.setPreferredSize(new Dimension(56, 56));
        JLabel logo = new JLabel("💊", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        logoBox.add(logo, BorderLayout.CENTER);
        JLabel titre = new JLabel("  PharmacieL2M");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titre.setForeground(BLANC);
        g.add(logoBox); g.add(titre);
        p.add(g, BorderLayout.WEST);

        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        d.setOpaque(false);
        String today = new SimpleDateFormat("EEE d MMM yyyy", java.util.Locale.FRENCH).format(new Date());
        JLabel lblDate = new JLabel("📅 " + today);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(180, 200, 235));
        JLabel lblUser = new JLabel("👤  " + nomComplet + "  |  " + role.toUpperCase());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(200, 220, 255));
        JButton btnDeco = new JButton("⏻  Déconnexion");
        btnDeco.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnDeco.setForeground(new Color(255, 120, 100));
        btnDeco.setBackground(new Color(40, 55, 90));
        btnDeco.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 80, 100), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        btnDeco.setFocusPainted(false);
        btnDeco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeco.addActionListener(e -> deconnecter());
        d.add(lblDate); d.add(lblUser); d.add(btnDeco);
        p.add(d, BorderLayout.EAST);
        return p;
    }

    /* ── SIDEBAR ─────────────────────────────────────────────────────── */
    private JPanel sidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLEU_FOND);
        p.setPreferredSize(new Dimension(230, 0));
        p.add(Box.createVerticalStrut(12));

        p.add(sectionLabel("ACCUEIL"));
        p.add(btnNav("🏠", "Tableau de bord",  "dashboard", BLANC,  e -> afficherDashboard()));
        p.add(Box.createVerticalStrut(8));

        p.add(sectionLabel("JOURNÉE"));
        p.add(btnNav("🛒", "Point de vente",   "vente",     VERT,   e -> ouvrirVente()));
        p.add(btnNav("⚠",  "Alertes & Stock",  "stock",     ORANGE, e -> ouvrirStock()));
        p.add(btnNav("💰", "Caisse / Finance", "caisse",    OR,     e -> ouvrirCaisse()));
        p.add(Box.createVerticalStrut(8));

        p.add(sectionLabel("RÉFÉRENTIEL"));
        p.add(btnNav("💊", "Médicaments",      "meds",      BLANC,  e -> ouvrirMedicaments()));
        p.add(btnNav("🏷",  "Catégories",       "cats",      CYAN,   e -> ouvrirCategories()));
        p.add(btnNav("🚛", "Fournisseurs",     "fourn",     VIOLET, e -> ouvrirFournisseurs()));
        p.add(Box.createVerticalStrut(8));

        if (role != null && (role.equalsIgnoreCase("Admin") ||
            role.equalsIgnoreCase("Responsable") ||
            role.equalsIgnoreCase("Administrateur"))) {
            p.add(sectionLabel("ADMINISTRATION"));
            p.add(btnNav("👥", "Pharmaciens",  "pharma",    new Color(200,200,255), e -> ouvrirPharmaciens()));
            p.add(Box.createVerticalStrut(8));
        }

        p.add(Box.createVerticalGlue());
        JLabel version = new JLabel("PharmacieL2M v1.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(100, 120, 160));
        version.setAlignmentX(CENTER_ALIGNMENT);
        version.setBorder(new EmptyBorder(0, 0, 10, 0));
        p.add(version);
        return p;
    }

    private JLabel sectionLabel(String texte) {
        JLabel l = new JLabel("  " + texte);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(100, 130, 180));
        l.setBorder(new EmptyBorder(6, 8, 4, 8));
        l.setAlignmentX(0f);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return l;
    }

    private JButton btnNav(String icone, String label, String navId,
                           Color couleur, ActionListener action) {
        JButton b = new JButton(icone + "  " + label);
        b.putClientProperty("navId", navId);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        b.setForeground(couleur);
        b.setBackground(BLEU_FOND);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(0f);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btnActif != b) b.setBackground(new Color(30, 60, 110));
            }
            public void mouseExited(MouseEvent e) {
                if (btnActif != b) b.setBackground(BLEU_FOND);
            }
        });

        b.addActionListener(e -> {
            if (btnActif != null) { btnActif.setBackground(BLEU_FOND); }
            btnActif = b;
            b.setBackground(BLEU_ACT);
            action.actionPerformed(e);
        });
        return b;
    }

    /* ── PANEL PRINCIPAL ─────────────────────────────────────────────── */
    private JPanel panelPrincipal() {
        panelContenu = new JPanel(new BorderLayout());
        panelContenu.setBackground(GRIS_FOND);
        afficherDashboard();
        return panelContenu;
    }

    /* ══════════════════════════════════════════════════════════════════
       DASHBOARD
    ══════════════════════════════════════════════════════════════════ */
    private void afficherDashboard() {
        panelContenu.removeAll();

        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(22, 22, 22, 22));

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setOpaque(false);
        entete.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel titPage = new JLabel("Bonjour, " + nomComplet + " 👋");
        titPage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titPage.setForeground(new Color(20, 40, 80));
        JLabel sousTit = new JLabel("Vue d'ensemble — " +
            new SimpleDateFormat("EEEE d MMMM yyyy", java.util.Locale.FRENCH).format(new Date()));
        sousTit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sousTit.setForeground(new Color(90, 110, 150));
        JPanel titBox = new JPanel(); titBox.setOpaque(false);
        titBox.setLayout(new BoxLayout(titBox, BoxLayout.Y_AXIS));
        titPage.setAlignmentX(0f); sousTit.setAlignmentX(0f);
        titBox.add(titPage); titBox.add(Box.createVerticalStrut(3)); titBox.add(sousTit);
        entete.add(titBox, BorderLayout.WEST);
        JButton bActu = new JButton("↻  Actualiser");
        bActu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bActu.setForeground(BLEU_ACT); bActu.setBackground(BLANC);
        bActu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        bActu.setFocusPainted(false);
        bActu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bActu.addActionListener(e -> chargerKPIs());
        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        droite.setOpaque(false); droite.add(bActu);
        entete.add(droite, BorderLayout.EAST);
        p.add(entete, BorderLayout.NORTH);

        // Corps scrollable
        JPanel corps = new JPanel();
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setOpaque(false);

        // ── KPIs ──────────────────────────────────────────────────────
        JPanel ligneKPI = new JPanel(new GridLayout(1, 5, 14, 0));
        ligneKPI.setOpaque(false);
        ligneKPI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        ligneKPI.setAlignmentX(0f);

        lblMedicaments  = kpiLabel(); lblAlertes      = kpiLabel();
        lblVentesJour   = kpiLabel(); lblStockFaible  = kpiLabel();
        lblRecettesJour = new JLabel("…");
        lblRecettesJour.setFont(new Font("Segoe UI", Font.BOLD, 20));

        ligneKPI.add(kpiCard("💊  Médicaments",    lblMedicaments,  BLEU_ACT, e -> ouvrirMedicaments()));
        ligneKPI.add(kpiCard("⚠  Alertes actives", lblAlertes,      ROUGE,    e -> ouvrirStock()));
        ligneKPI.add(kpiCard("🛒  Ventes du jour",  lblVentesJour,   VERT,     e -> ouvrirVente()));
        ligneKPI.add(kpiCard("📦  Stocks faibles",  lblStockFaible,  ORANGE,   e -> ouvrirStock()));
        ligneKPI.add(kpiCard("💰  Recettes du jour",lblRecettesJour, OR,       e -> ouvrirCaisse()));
        corps.add(ligneKPI);
        corps.add(Box.createVerticalStrut(24));

        // ── Modules principaux ─────────────────────────────────────────
        JLabel titActions = new JLabel("Actions rapides");
        titActions.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titActions.setForeground(new Color(40, 55, 90));
        titActions.setAlignmentX(0f);
        corps.add(titActions);
        corps.add(Box.createVerticalStrut(12));

        JPanel grille = new JPanel(new GridLayout(2, 3, 14, 14));
        grille.setOpaque(false);
        grille.setAlignmentX(0f);

        grille.add(moduleCard("🛒", "Point de Vente",
            "Enregistrer une vente,\ncalculer le total, encaisser",
            VERT,    e -> ouvrirVente()));
        grille.add(moduleCard("⚠", "Stock & Alertes",
            "Niveaux de stock,\nlots et alertes péremption",
            ORANGE,  e -> ouvrirStock()));
        grille.add(moduleCard("💰", "Caisse / Finance",
            "Bilan du jour, dépenses,\nrecettes et solde net",
            OR,      e -> ouvrirCaisse()));
        grille.add(moduleCard("💊", "Médicaments",
            "Catalogue, prix,\ndosages et formes",
            BLEU_ACT,e -> ouvrirMedicaments()));
        grille.add(moduleCard("🚛", "Fournisseurs",
            "Commandes, réceptions,\nhistorique approvisionnements",
            VIOLET,  e -> ouvrirFournisseurs()));
        grille.add(moduleCard("🏷", "Catégories",
            "Antibiotiques, Analgésiques,\nCardiologie, Diabétologie…",
            CYAN,    e -> ouvrirCategories()));

        corps.add(grille);

        // ── Admin ──────────────────────────────────────────────────────
        if (role != null && (role.equalsIgnoreCase("Admin") ||
            role.equalsIgnoreCase("Responsable") ||
            role.equalsIgnoreCase("Administrateur"))) {
            corps.add(Box.createVerticalStrut(22));
            JLabel titAdmin = new JLabel("Administration");
            titAdmin.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titAdmin.setForeground(new Color(40, 55, 90));
            titAdmin.setAlignmentX(0f);
            corps.add(titAdmin);
            corps.add(Box.createVerticalStrut(12));
            JPanel ligAdmin = new JPanel(new GridLayout(1, 2, 14, 0));
            ligAdmin.setOpaque(false);
            ligAdmin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            ligAdmin.setAlignmentX(0f);
            ligAdmin.add(moduleCard("👥", "Pharmaciens",
                "Gérer les comptes,\nrôles et accès du personnel",
                new Color(80, 60, 160), e -> ouvrirPharmaciens()));
            ligAdmin.add(moduleCard("📊", "Bilan mensuel",
                "Recettes et dépenses\npar mois depuis la Caisse",
                new Color(40, 100, 120), e -> ouvrirCaisse()));
            corps.add(ligAdmin);
        }

        corps.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(corps);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(GRIS_FOND);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        p.add(scroll, BorderLayout.CENTER);

        panelContenu.add(p, BorderLayout.CENTER);
        panelContenu.revalidate();
        panelContenu.repaint();
    }

    private JLabel kpiLabel() {
        JLabel l = new JLabel("…");
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return l;
    }

    private JPanel kpiCard(String titre, JLabel valeur, Color couleur, ActionListener action) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(BLANC);
        c.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel bande = new JPanel(); bande.setBackground(couleur);
        bande.setPreferredSize(new Dimension(0, 4));
        c.add(bande, BorderLayout.NORTH);
        JPanel corps = new JPanel(); corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setOpaque(false); corps.setBorder(new EmptyBorder(12, 14, 12, 14));
        valeur.setForeground(couleur); valeur.setAlignmentX(0f);
        JLabel tl = new JLabel(titre);
        tl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        tl.setForeground(new Color(80, 100, 135));
        tl.setAlignmentX(0f); tl.setBorder(new EmptyBorder(5, 0, 0, 0));
        corps.add(valeur); corps.add(tl);
        c.add(corps, BorderLayout.CENTER);
        c.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { c.setBackground(new Color(245, 248, 255)); }
            public void mouseExited(MouseEvent e)  { c.setBackground(BLANC); }
            public void mouseClicked(MouseEvent e) { action.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, null)
    ); ; }
        });
        return c;
    }

    private JPanel moduleCard(String icone, String titre, String desc,
                               Color couleur, ActionListener action) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(BLANC);
        c.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel bande = new JPanel(); bande.setBackground(couleur);
        bande.setPreferredSize(new Dimension(5, 0));
        c.add(bande, BorderLayout.WEST);
        JPanel corps = new JPanel(new BorderLayout());
        corps.setOpaque(false); corps.setBorder(new EmptyBorder(14, 16, 14, 16));
        JPanel haut = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); haut.setOpaque(false);
        JLabel ic = new JLabel(icone); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel tl = new JLabel(titre); tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(new Color(20, 40, 80));
        haut.add(ic); haut.add(tl);
        JLabel dl = new JLabel("<html><font color='#6b7a99'>" +
            desc.replace("\n","<br>") + "</font></html>");
        dl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dl.setBorder(new EmptyBorder(4, 4, 0, 0));
        JLabel fleche = new JLabel("→");
        fleche.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fleche.setForeground(couleur);
        corps.add(haut, BorderLayout.NORTH);
        corps.add(dl,   BorderLayout.CENTER);
        corps.add(fleche, BorderLayout.EAST);
        c.add(corps, BorderLayout.CENTER);
        c.addMouseListener(new MouseAdapter() {
    public void mouseEntered(MouseEvent e) {
        c.setBackground(new Color(248, 250, 255));
        bande.setPreferredSize(new Dimension(7, 0));
        c.revalidate();
    }

    public void mouseExited(MouseEvent e) {
        c.setBackground(BLANC);
        bande.setPreferredSize(new Dimension(5, 0));
        c.revalidate();
    }

    public void mouseClicked(MouseEvent e) {
        action.actionPerformed(
            new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, null)
        );
    }
});

return c;
        
    }

    /* ══════════════════════════════════════════════════════════════════
       KPIs BDD
    ══════════════════════════════════════════════════════════════════ */
    private void chargerKPIs() {
        new SwingWorker<int[], Void>() {
            double recettes = 0;
            protected int[] doInBackground() {
                int[] v = new int[4];
                try (Connection c = DBconnexion.getConnection()) {
                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM \"Medicament\" "))
                    { if (rs.next()) v[0] = rs.getInt(1); }

                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery(
                             "SELECT COUNT(*) FROM \"Alerte\" WHERE est_lue = false"))
                    { if (rs.next()) v[1] = rs.getInt(1); }

                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery(
                             "SELECT COUNT(*) FROM \"Vente\" WHERE date_vente = CURRENT_DATE"))
                    { if (rs.next()) v[2] = rs.getInt(1); }

                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery(
                             "SELECT COUNT(*) FROM \"Stock\" WHERE quantite_disponible <= seuil_alerte"))
                    { if (rs.next()) v[3] = rs.getInt(1); }

                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery(
                             "SELECT COALESCE(SUM(montant),0) FROM \"Caisse\" " +
                             "WHERE date_operation = CURRENT_DATE " +
                             "AND LOWER(type_operation) = 'recette'"))
                    { if (rs.next()) recettes = rs.getDouble(1); }
                } catch (SQLException ex) {
                    System.err.println("KPIs : " + ex.getMessage());
                }
                return v;
            }
            protected void done() {
                try {
                    int[] v = get();
                    lblMedicaments.setText(String.valueOf(v[0]));
                    lblAlertes.setText(String.valueOf(v[1]));
                    lblAlertes.setForeground(v[1] > 0 ? ROUGE : VERT);
                    lblVentesJour.setText(String.valueOf(v[2]));
                    lblStockFaible.setText(String.valueOf(v[3]));
                    lblStockFaible.setForeground(v[3] > 5 ? ROUGE : v[3] > 0 ? ORANGE : VERT);
                    lblRecettesJour.setText(String.format("%.0f FCFA", recettes));
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    /* ══════════════════════════════════════════════════════════════════
       NAVIGATION
    ══════════════════════════════════════════════════════════════════ */
    private void ouvrirVente()        { SwingUtilities.invokeLater(() -> new GestionVente(idPharmacien)); }
    private void ouvrirStock()        { SwingUtilities.invokeLater(() -> new GestionStock()); }
    private void ouvrirCaisse()       { SwingUtilities.invokeLater(() -> new GestionCaisse()); }
    private void ouvrirMedicaments()  { SwingUtilities.invokeLater(() -> new GestionMedicaments()); }
    private void ouvrirCategories()   { SwingUtilities.invokeLater(() -> new GestionCategorie()); }
    private void ouvrirFournisseurs() { SwingUtilities.invokeLater(() -> new GestionFournisseur()); }
    private void ouvrirPharmaciens()  { SwingUtilities.invokeLater(() -> new GestionPharmacien()); }

    /* ══════════════════════════════════════════════════════════════════
       DÉCONNEXION
    ══════════════════════════════════════════════════════════════════ */
    private void deconnecter() {
        int r = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vous déconnecter ?", "Déconnexion",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            DBconnexion.fermer();
            dispose();
            SwingUtilities.invokeLater(() -> new Connexion());
        }
    }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new Tableaudebord("1", "AÏNADOU ", "Mondoukpè", "Admin");
    });
}
}