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
 * GestionCategorie.java
 * Table Categorie : id_categorie | nom_categorie | description
 *
 * Utilisée comme référentiel par GestionMedicament (FK id_categorie)
 * Ex: Antibiotiques, Analgésiques, Cardiologie, Diabétologie...
 */
public class GestionCategorie extends JFrame {

    private static final Color BLEU      = new Color(25, 90, 160);
    private static final Color BLEU_CL   = new Color(235, 243, 255);
    private static final Color BLANC     = Color.WHITE;
    private static final Color GRIS_FOND = new Color(245, 247, 250);
    private static final Color GRIS_LIG  = new Color(230, 235, 242);
    private static final Color GRIS_BRD  = new Color(200, 210, 225);
    private static final Color GRIS_TXT  = new Color(90, 100, 120);
    private static final Color VERT      = new Color(35, 155, 75);
    private static final Color ROUGE     = new Color(200, 50, 50);
    private static final Color CYAN      = new Color(0, 130, 150);

    private JTable            table;
    private DefaultTableModel modele;
    private JTextField        rechercheChamp;
    private JLabel            lblNb;

    // Formulaire
    private JTextField fNomCat, fDescription;
    private JButton    btnSave;
    private boolean    modeEdit   = false;
    private String     idEnCours  = null;

    public GestionCategorie() {
        buildUI();
        charger();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Catégories");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 580);
        setMinimumSize(new Dimension(780, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);
        root.add(topBar(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelGauche(), panelDroit());
        split.setDividerLocation(520);
        split.setDividerSize(4);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CYAN);
        p.setBorder(new EmptyBorder(13, 22, 13, 22));
        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("🏷"); ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Gestion des Catégories de Médicaments");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17)); ti.setForeground(BLANC);
        g.add(ic); g.add(ti);
        p.add(g, BorderLayout.WEST);
        JButton btnNew = btn("+ Nouvelle catégorie", BLANC);
        btnNew.addActionListener(e -> vider());
        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        d.setOpaque(false); d.add(btnNew);
        p.add(d, BorderLayout.EAST);
        return p;
    }

    private JPanel panelGauche() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 7));

        JPanel barre = new JPanel(new BorderLayout(8, 0));
        barre.setOpaque(false); barre.setBorder(new EmptyBorder(0, 0, 10, 0));
        rechercheChamp = new JTextField();
        rechercheChamp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rechercheChamp.putClientProperty("JTextField.placeholderText", "🔍  Rechercher catégorie…");
        rechercheChamp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        rechercheChamp.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrer(); }
        });
        lblNb = new JLabel("0 catégorie(s)");
        lblNb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNb.setForeground(GRIS_TXT);
        barre.add(rechercheChamp, BorderLayout.CENTER);
        barre.add(lblNb, BorderLayout.EAST);
        p.add(barre, BorderLayout.NORTH);

        // Colonnes = attributs réels : id_categorie | nom_categorie | description
        String[] cols = {"ID", "Nom de la catégorie", "Description", "Nb médicaments"};
        modele = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(modele);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setGridColor(GRIS_LIG);
        table.setSelectionBackground(new Color(220, 245, 250));
        table.setSelectionForeground(CYAN);
        table.setBackground(BLANC);
        table.setFocusable(false);

        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 36));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CYAN));
        h.setReorderingAllowed(false);

        int[] lrg = {70, 180, 200, 80};
        for (int i = 0; i < lrg.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(245, 252, 253));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 1) setFont(getFont().deriveFont(Font.BOLD));
                if (col == 3) {
                    setForeground(CYAN); setFont(getFont().deriveFont(Font.BOLD));
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) chargerLigne();
            }
        });

        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createLineBorder(GRIS_BRD));
        sc.getViewport().setBackground(BLANC);
        p.add(sc, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        actions.setOpaque(false);
        JButton bMod = btn("✏  Modifier",   CYAN);
        JButton bSup = btn("🗑  Supprimer", ROUGE);
        bMod.addActionListener(e -> chargerLigne());
        bSup.addActionListener(e -> supprimer());
        actions.add(bMod); actions.add(bSup);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelDroit() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(14, 7, 14, 14));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(new Color(230, 248, 250));
        titre.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel tl = new JLabel("Informations de la catégorie");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14)); tl.setForeground(CYAN);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(20, 22, 10, 22));

        // Exemples de catégories pour guider l'utilisateur
        JPanel exemples = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        exemples.setOpaque(false);
        exemples.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        exemples.setAlignmentX(0f);
        JLabel exLbl = new JLabel("Suggestions :");
        exLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exLbl.setForeground(GRIS_TXT);
        exemples.add(exLbl);
        String[] cats = {"Antibiotiques","Analgésiques","Cardiologie",
                         "Diabétologie","Pneumologie","Dermatologie",
                         "Gastro","Vitamines"};
        for (String cat : cats) {
            JButton chip = new JButton(cat);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setForeground(CYAN);
            chip.setBackground(new Color(230, 248, 252));
            chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 210, 220), 1, true),
                BorderFactory.createEmptyBorder(3, 9, 3, 9)));
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.addActionListener(e -> fNomCat.setText(cat));
            exemples.add(chip);
        }
        champs.add(exemples);
        champs.add(Box.createVerticalStrut(16));

        // nom_categorie
        champs.add(lbl("Nom de la catégorie *"));
        champs.add(Box.createVerticalStrut(5));
        fNomCat = fieldF("ex: Antibiotiques");
        champs.add(fNomCat);
        champs.add(Box.createVerticalStrut(14));

        // description
        champs.add(lbl("Description"));
        champs.add(Box.createVerticalStrut(5));
        fDescription = fieldF("ex: Médicaments contre les infections bactériennes");
        champs.add(fDescription);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null); sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        boutons.setBackground(new Color(245, 252, 253));
        boutons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        JButton bAnn = btn("Annuler", GRIS_TXT);
        btnSave = btn("Enregistrer", BLANC); btnSave.setBackground(CYAN);
        bAnn.addActionListener(e -> vider());
        btnSave.addActionListener(e -> sauvegarder());
        boutons.add(bAnn); boutons.add(btnSave);
        card.add(boutons, BorderLayout.SOUTH);

        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    /* ══════════════════════════════════════════════════════════════════
       SQL — CATEGORIE
    ══════════════════════════════════════════════════════════════════ */

    /** SELECT Categorie avec comptage médicaments associés */
    private void charger() {
        modele.setRowCount(0);
        String sql = "SELECT c.id_categorie, c.nom_categorie, c.description, " +
                     "COUNT(m.id_medicament) AS nb_meds " +
                     "FROM \"Categorie\" c " +
                     "LEFT JOIN \"Medicament\" m ON c.id_categorie = m.id_categorie " +
                     "GROUP BY c.id_categorie, c.nom_categorie, c.description " +
                     "ORDER BY c.nom_categorie";
        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            int nb = 0;
            while (rs.next()) {
                modele.addRow(new Object[]{
                    rs.getString("id_categorie"),
                    rs.getString("nom_categorie"),
                    rs.getString("description"),
                    rs.getInt("nb_meds")
                });
                nb++;
            }
            lblNb.setText(nb + " catégorie(s)");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Chargement impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void sauvegarder() {
    String nom  = fNomCat.getText().trim();
    String desc = fDescription.getText().trim();
    if (nom.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Le nom de la catégorie est obligatoire.",
            "Champ manquant", JOptionPane.WARNING_MESSAGE); 
        return;
    }
    try {
        if (modeEdit) {
            // Modification
            try (Connection c = DBconnexion.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "UPDATE \"Categorie\" SET nom_categorie=?, description=? WHERE id_categorie=?")) {
                ps.setString(1, nom);
                ps.setString(2, desc);
                ps.setInt(3, Integer.parseInt(idEnCours)); // idEnCours est un entier ici
                ps.executeUpdate();
            }
        } else {
            // Ajout
            try (Connection c = DBconnexion.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO \"Categorie\"(nom_categorie, description) VALUES(?, ?)")) {
                ps.setString(1, nom);
                ps.setString(2, desc);
                ps.executeUpdate();
            }
        }
        charger(); 
        vider();
        JOptionPane.showMessageDialog(this, "Catégorie enregistrée.", "Succès",
            JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erreur :\n" + ex.getMessage(),
            "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
       private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Sélectionnez une catégorie."); return; }
        int mr = table.convertRowIndexToModel(row);
        String id  = s(modele.getValueAt(mr, 0));
        String nom = s(modele.getValueAt(mr, 1));
        int    nb  = Integer.parseInt(s(modele.getValueAt(mr, 3)));
        if (nb > 0) {
            JOptionPane.showMessageDialog(this,
                "Impossible de supprimer « " + nom + " » :\n" +
                nb + " médicament(s) appartiennent à cette catégorie.",
                "Suppression impossible", JOptionPane.WARNING_MESSAGE); return;
        }
        if (JOptionPane.showConfirmDialog(this, "Supprimer « " + nom + " » ?",
            "Confirmation", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM \"Categorie\" WHERE id_categorie=?")) {
            ps.setString(1, id); ps.executeUpdate();
            charger(); vider();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Suppression impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Sélectionnez une catégorie."); return; }
        int mr = table.convertRowIndexToModel(row);
        idEnCours = s(modele.getValueAt(mr, 0));
        fNomCat.setText(s(modele.getValueAt(mr, 1)));
        fDescription.setText(s(modele.getValueAt(mr, 2)));
        modeEdit = true;
        btnSave.setText("Mettre à jour");
    }

    private void filtrer() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modele);
        table.setRowSorter(sorter);
        String t = rechercheChamp.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
    }

    private void vider() {
        fNomCat.setText(""); fDescription.setText("");
        modeEdit = false; idEnCours = null;
        btnSave.setText("Enregistrer");
        table.clearSelection();
    }

    /* ── HELPERS ─────────────────────────────────────────────────────── */
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
                    BorderFactory.createLineBorder(CYAN, 2),
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
    private JButton btn(String txt, Color fg) {
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
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Attention", JOptionPane.WARNING_MESSAGE); }
    private String s(Object o)  { return o == null ? "" : o.toString(); }
    
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionCategorie();
    });
}
}