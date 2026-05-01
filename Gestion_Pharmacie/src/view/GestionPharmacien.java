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
import database.DBconnexion;

/**
 * GestionPharmacien.java
 * Table : Pharmacien (id_pharmacien | nom | prenom | email | motdepasse | role)
 */
public class GestionPharmacien extends JFrame {

    /* ── Palette ─────────────────────────────────────────────────────── */
    private static final Color BLEU      = new Color(25, 90, 160);
    private static final Color BLEU_CL   = new Color(235, 243, 255);
    private static final Color BLEU_DARK = new Color(18, 65, 125);
    private static final Color BLANC     = Color.WHITE;
    private static final Color GRIS_FOND = new Color(245, 247, 250);
    private static final Color GRIS_LIG  = new Color(230, 235, 242);
    private static final Color GRIS_BRD  = new Color(200, 210, 225);
    private static final Color GRIS_TXT  = new Color(90, 100, 120);
    private static final Color VERT      = new Color(35, 155, 75);
    private static final Color ROUGE     = new Color(200, 50, 50);
    private static final Color ORANGE    = new Color(210, 120, 0);

    /* ── Tableau ─────────────────────────────────────────────────────── */
    private JTable             table;
    private DefaultTableModel  modele;
    private JTextField         champRecherche;
    private JLabel             lblNb;

    /* ── Formulaire ──────────────────────────────────────────────────── */
    private JTextField     fNom, fPrenom, fEmail;
    private JPasswordField fMdp, fConfirm;
    private JComboBox<String> cbRole;
    private JButton        btnSave, btnAnnuler;

    private boolean modeEdit = false;
    private String  idEnCours = null;   // id_pharmacien (varchar)

    public GestionPharmacien() {
        buildUI();
        charger();
    }

    /* ═══════════════════════════════════════════════════════════════════
       CONSTRUCTION UI
    ═══════════════════════════════════════════════════════════════════ */
    private void buildUI() {
        setTitle("PharmacieL2M — Pharmaciens");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(960, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);

        root.add(topBar(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, panelGauche(), panelDroit());
        split.setDividerLocation(630);
        split.setDividerSize(4);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setVisible(true);
    }

    /* ── Barre supérieure ───────────────────────────────────────────── */
    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU);
        p.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel gauche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        gauche.setOpaque(false);
        JLabel ic = new JLabel("👤"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Gestion des Pharmaciens");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17));
        ti.setForeground(BLANC);
        gauche.add(ic); gauche.add(ti);
        p.add(gauche, BorderLayout.WEST);

        JButton btnNew = btnAction("+ Nouveau", BLANC);
        btnNew.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,100), 1, true),
            BorderFactory.createEmptyBorder(6,14,6,14)));
        btnNew.addActionListener(e -> vider());
        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        droite.setOpaque(false); droite.add(btnNew);
        p.add(droite, BorderLayout.EAST);
        return p;
    }

    /* ── Panel gauche : recherche + tableau + actions ───────────────── */
    private JPanel panelGauche() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 7));

        // Barre recherche
        JPanel barreTop = new JPanel(new BorderLayout(10, 0));
        barreTop.setOpaque(false);
        barreTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        champRecherche = new JTextField();
        champRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        champRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        champRecherche.putClientProperty("JTextField.placeholderText", "🔍  Rechercher nom, prénom, email, rôle…");
        champRecherche.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrer(); }
        });

        lblNb = new JLabel("0 pharmacien(s)");
        lblNb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNb.setForeground(GRIS_TXT);

        barreTop.add(champRecherche, BorderLayout.CENTER);
        barreTop.add(lblNb, BorderLayout.EAST);
        p.add(barreTop, BorderLayout.NORTH);

        // Tableau — colonnes = attributs réels
        String[] cols = {"ID", "Nom", "Prénom", "Email", "Rôle"};
        modele = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modele);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(GRIS_LIG);
        table.setSelectionBackground(BLEU_CL);
        table.setSelectionForeground(BLEU);
        table.setBackground(BLANC);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 36));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLEU));
        h.setReorderingAllowed(false);

        // Largeurs
        int[] lrg = {70, 110, 110, 170, 90};
        for (int i = 0; i < lrg.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        // Renderer rôles colorés
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(248, 250, 254));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 0) { setForeground(GRIS_TXT); setFont(getFont().deriveFont(Font.BOLD, 11f)); }
                if (col == 4 && v != null) {
                    String r = v.toString();
                    setFont(getFont().deriveFont(Font.BOLD));
                    switch (r) {
                        case "Admin"       -> setForeground(BLEU);
                        case "Titulaire"   -> setForeground(VERT);
                        case "Adjoint"     -> setForeground(ORANGE);
                        default            -> setForeground(GRIS_TXT);
                    }
                }
                return this;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) chargerLigne();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        scroll.getViewport().setBackground(BLANC);
        p.add(scroll, BorderLayout.CENTER);
        p.add(barreActions(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel barreActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        p.setOpaque(false);

        JButton bMod = btnAction("✏  Modifier",    BLEU);
        JButton bSup = btnAction("🗑  Supprimer",   ROUGE);
        JButton bAct = btnAction("↻  Actualiser",  GRIS_TXT);

        bMod.addActionListener(e -> chargerLigne());
        bSup.addActionListener(e -> supprimer());
        bAct.addActionListener(e -> charger());

        p.add(bMod); p.add(bSup);
        p.add(Box.createHorizontalStrut(8)); p.add(bAct);
        return p;
    }

    /* ── Panel droit : formulaire ───────────────────────────────────── */
    private JPanel panelDroit() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(14, 7, 14, 14));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        // Titre carte
        JPanel titrePane = new JPanel(new BorderLayout());
        titrePane.setBackground(BLEU_CL);
        titrePane.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel titreL = new JLabel("Informations du pharmacien");
        titreL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titreL.setForeground(BLEU);
        titrePane.add(titreL, BorderLayout.WEST);
        card.add(titrePane, BorderLayout.NORTH);

        // Champs (attributs réels de la table Pharmacien)
        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(18, 22, 10, 22));

        fNom    = row(champs, "Nom *",          "ex : Dupont");
        fPrenom = row(champs, "Prénom *",       "ex : Mana");
        fEmail  = row(champs, "Email *",        "ex : mano@gmail.com");

        // Rôle (valeurs cohérentes avec la colonne role VARCHAR(50))
        champs.add(labelF("Rôle *"));
        champs.add(Box.createVerticalStrut(5));
        cbRole = new JComboBox<>(new String[]{"Caissier","Pharmacien","Responsable_Stock"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbRole.setAlignmentX(0f);
        champs.add(cbRole);
        champs.add(Box.createVerticalStrut(14));

        // Mot de passe (motdepasse VARCHAR(200))
        champs.add(labelF("Mot de passe *  (min. 6 caractères)"));
        champs.add(Box.createVerticalStrut(5));
        fMdp = new JPasswordField(); styleField(fMdp);
        champs.add(fMdp);
        champs.add(Box.createVerticalStrut(13));

        champs.add(labelF("Confirmer le mot de passe *"));
        champs.add(Box.createVerticalStrut(5));
        fConfirm = new JPasswordField(); styleField(fConfirm);
        champs.add(fConfirm);

        JScrollPane scrollChamps = new JScrollPane(champs);
        scrollChamps.setBorder(null);
        scrollChamps.getViewport().setBackground(BLANC);
        card.add(scrollChamps, BorderLayout.CENTER);
        card.add(panelBoutons(), BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    private JPanel panelBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(new Color(247, 250, 254));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));

        btnAnnuler = btnAction("Annuler", GRIS_TXT);
        btnSave    = btnAction("Enregistrer", BLEU);
        btnSave.setForeground(BLANC);
        btnSave.setBackground(BLEU);

        btnAnnuler.addActionListener(e -> vider());
        btnSave.addActionListener(e -> sauvegarder());

        p.add(btnAnnuler); p.add(btnSave);
        return p;
    }

    /* ═══════════════════════════════════════════════════════════════════
       OPÉRATIONS SQL  (table Pharmacien)
    ═══════════════════════════════════════════════════════════════════ */

    /** SELECT * FROM Pharmacien */
    private void charger() {
        modele.setRowCount(0);
        String sql = "SELECT id_pharmacien, nom, prenom, email, role FROM \"Pharmacien\" ORDER BY nom, prenom";
        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            int nb = 0;
            while (rs.next()) {
                modele.addRow(new Object[]{
                    rs.getString("id_pharmacien"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("role")
                });
                nb++;
            }
            lblNb.setText(nb + " pharmacien(s)");
        } catch (SQLException ex) {
            erreur("Chargement impossible :\n" + ex.getMessage());
        }
    }

    /** INSERT INTO Pharmacien */
    private void inserer(String id, String nom, String prenom, String email,
                         String mdp, String role) throws SQLException {
        String sql = "INSERT INTO \"Pharmacien\"(nom,prenom,email,motdepasse,role) " +
                     "VALUES(?,?,?,?,?)";
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setString(4, mdp);   // en prod : hacher avec BCrypt
            ps.setString(5, role);
            ps.executeUpdate();
        }
    }

    /** UPDATE Pharmacien SET … WHERE id_pharmacien = ? */
    private void modifier(String id, String nom, String prenom, String email,
                          String mdp, String role) throws SQLException {
        boolean changerMdp = !mdp.isEmpty();
        String sql = changerMdp
            ? "UPDATE \"Pharmacien\" SET nom=?,prenom=?,email=?,motdepasse=?,role=? WHERE id_pharmacien=?"
            : "UPDATE \"Pharmacien\" SET nom=?,prenom=?,email=?,role=? WHERE id_pharmacien=?";
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            if (changerMdp) {
                ps.setString(4, mdp);
                ps.setString(5, role);
                ps.setString(6, id);
            } else {
                ps.setString(4, role);
                ps.setString(5, id);
            }
            ps.executeUpdate();
        }
    }

    /** DELETE FROM Pharmacien WHERE id_pharmacien = ? */
    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,
            "Sélectionnez un pharmacien.", "Aucune sélection", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(modele.getValueAt(mRow, 0).toString());
        String name = modele.getValueAt(mRow, 1) + " " + modele.getValueAt(mRow, 2);
        if (JOptionPane.showConfirmDialog(this,
            "Supprimer définitivement « " + name + " » ?",
            "Confirmation", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM \"Pharmacien\" WHERE id_pharmacien=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            charger(); vider();
            JOptionPane.showMessageDialog(this, "Pharmacien supprimé.", "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { erreur("Suppression impossible :\n" + ex.getMessage()); }
    }

    /* ── Sauvegarder (insert ou update) ──────────────────────────────── */
    private void sauvegarder() {
        String nom    = fNom.getText().trim();
        String prenom = fPrenom.getText().trim();
        String email  = fEmail.getText().trim();
        String mdp    = new String(fMdp.getPassword());
        String conf   = new String(fConfirm.getPassword());
        String role   = (String) cbRole.getSelectedItem();

        // Validations
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom, prénom et email sont obligatoires.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            JOptionPane.showMessageDialog(this, "Adresse e-mail invalide.",
                "Format incorrect", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!modeEdit && mdp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire.",
                "Champ manquant", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!mdp.isEmpty() && mdp.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mot de passe trop court (min. 6 caractères).",
                "Erreur", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!mdp.isEmpty() && !mdp.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.",
                "Erreur", JOptionPane.WARNING_MESSAGE); return;
        }

        try {
            if (modeEdit) {
                modifier(idEnCours, nom, prenom, email, mdp, role);
                JOptionPane.showMessageDialog(this, "Pharmacien mis à jour.", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Génération id simple : "PH-" + timestamp
                String newId = "PH-" + System.currentTimeMillis();
                inserer(newId, nom, prenom, email, mdp, role);
                JOptionPane.showMessageDialog(this, "Pharmacien enregistré.", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            charger(); vider();
        } catch (SQLException ex) {
            erreur("Enregistrement impossible :\n" + ex.getMessage());
        }
    }

    /* ── Charger la ligne sélectionnée dans le formulaire ─────────────── */
    private void chargerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,
            "Sélectionnez un pharmacien.", "Aucune sélection", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = table.convertRowIndexToModel(row);
        idEnCours = modele.getValueAt(mRow, 0).toString();
        fNom.setText(obj(modele.getValueAt(mRow, 1)));
        fPrenom.setText(obj(modele.getValueAt(mRow, 2)));
        fEmail.setText(obj(modele.getValueAt(mRow, 3)));
        cbRole.setSelectedItem(obj(modele.getValueAt(mRow, 4)));
        fMdp.setText(""); fConfirm.setText("");
        modeEdit = true;
        btnSave.setText("Mettre à jour");
    }

    private void filtrer() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modele);
        table.setRowSorter(sorter);
        String t = champRecherche.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
    }

    private void vider() {
        fNom.setText(""); fPrenom.setText(""); fEmail.setText("");
        fMdp.setText(""); fConfirm.setText("");
        cbRole.setSelectedIndex(0);
        modeEdit = false; idEnCours = null;
        btnSave.setText("Enregistrer");
        table.clearSelection();
    }

    /* ═══════════════════════════════════════════════════════════════════
       HELPERS UI
    ═══════════════════════════════════════════════════════════════════ */
    private JTextField row(JPanel p, String libelle, String ph) {
        p.add(labelF(libelle));
        p.add(Box.createVerticalStrut(5));
        JTextField f = champTexte(ph);
        p.add(f);
        p.add(Box.createVerticalStrut(13));
        return f;
    }
    private JLabel labelF(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(45, 62, 88));
        l.setAlignmentX(0f);
        return l;
    }
    private JTextField champTexte(String ph) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(175, 188, 205));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(ph, 10, getHeight()/2+5);
                }
            }
        };
        styleField(f); return f;
    }
    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(0f);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BLEU, 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(GRIS_BRD, 1),
                    BorderFactory.createEmptyBorder(5, 9, 5, 9)));
            }
        });
    }
    private JButton btnAction(String txt, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(fg);
        b.setBackground(BLANC);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private void erreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    private String obj(Object o) { return o == null ? "" : o.toString(); }
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionPharmacien();
    });
}
}
