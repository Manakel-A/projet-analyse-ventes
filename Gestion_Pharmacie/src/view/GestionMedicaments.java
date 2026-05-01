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
 * GestionMedicament.java
 * Table Medicament : id_medicament | code_cip | nom_commercial | nom_generique
 *                    dosage | forme | prix_achat | prix_vente | id_categorie
 */
public class GestionMedicaments extends JFrame {

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

    // Tableau
    private JTable            table;
    private DefaultTableModel modele;
    private JTextField        champRecherche;
    private JLabel            lblNb;
    private JComboBox<String> cbFiltreCategorie;

    // Formulaire — attributs réels de la table
    private JTextField fIdMedicament, fCodeCip, fNomCommercial, fNomGenerique;
    private JTextField fDosage, fForme, fPrixAchat, fPrixVente;
    private JComboBox<String[]> cbCategorie; // affiche nom_categorie, stocke id_categorie
    private JButton   btnSave, btnAnnuler;

    private boolean modeEdit  = false;
    private String  idEnCours = null;

    public GestionMedicaments() {
        buildUI();
        chargerCategories();
        charger();
    }

    private void buildUI() {
        setTitle("PharmacieL2M — Médicaments");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(GRIS_FOND);
        setContentPane(root);

        root.add(topBar(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, panelGauche(), panelDroit());
        split.setDividerLocation(660);
        split.setDividerSize(4);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setVisible(true);
    }

    /* ── BARRE SUPÉRIEURE ───────────────────────────────────────────── */
    private JPanel topBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLEU);
        p.setBorder(new EmptyBorder(13, 22, 13, 22));

        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        g.setOpaque(false);
        JLabel ic = new JLabel("💊");
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel ti = new JLabel("Gestion des Médicaments");
        ti.setFont(new Font("Segoe UI", Font.BOLD, 17));
        ti.setForeground(BLANC);
        g.add(ic); g.add(ti);
        p.add(g, BorderLayout.WEST);

        JButton btnNew = btnStyle("+ Nouveau médicament", BLANC);
        btnNew.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,100), 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btnNew.addActionListener(e -> vider());
        JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        d.setOpaque(false); d.add(btnNew);
        p.add(d, BorderLayout.EAST);
        return p;
    }

    /* ── PANEL GAUCHE : filtre + tableau ────────────────────────────── */
    private JPanel panelGauche() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(GRIS_FOND);
        p.setBorder(new EmptyBorder(14, 14, 14, 7));

        // Barre recherche + filtre catégorie
        JPanel barreTop = new JPanel(new BorderLayout(8, 0));
        barreTop.setOpaque(false);
        barreTop.setBorder(new EmptyBorder(0, 0, 10, 0));

        champRecherche = new JTextField();
        champRecherche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        champRecherche.putClientProperty("JTextField.placeholderText",
            "🔍  Rechercher nom commercial, CIP, forme…");
        champRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BRD, 1, true),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        champRecherche.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrer(); }
        });

        cbFiltreCategorie = new JComboBox<>();
        cbFiltreCategorie.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltreCategorie.setPreferredSize(new Dimension(150, 36));
        cbFiltreCategorie.addActionListener(e -> filtrer());

        lblNb = new JLabel("0 médicament(s)");
        lblNb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNb.setForeground(GRIS_TXT);

        JPanel droiteBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        droiteBar.setOpaque(false);
        droiteBar.add(cbFiltreCategorie);
        droiteBar.add(lblNb);

        barreTop.add(champRecherche, BorderLayout.CENTER);
        barreTop.add(droiteBar, BorderLayout.EAST);
        p.add(barreTop, BorderLayout.NORTH);

        // Colonnes = attributs réels
        String[] cols = {"ID", "Code CIP", "Nom commercial", "Nom générique",
                         "Dosage", "Forme", "Prix achat", "Prix vente", "Catégorie"};
        modele = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(modele);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setGridColor(GRIS_LIG);
        table.setSelectionBackground(BLEU_CL);
        table.setSelectionForeground(BLEU);
        table.setBackground(BLANC);
        table.setFocusable(false);

        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(new Color(218, 228, 242));
        h.setForeground(new Color(35, 55, 100));
        h.setPreferredSize(new Dimension(0, 34));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BLEU));
        h.setReorderingAllowed(false);

        int[] lrg = {60, 90, 140, 130, 70, 80, 80, 80, 100};
        for (int i = 0; i < lrg.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(lrg[i]);

        // Renderer : colorer prix vente
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    setBackground(row % 2 == 0 ? BLANC : new Color(248, 250, 254));
                    setForeground(new Color(30, 42, 65));
                }
                if (col == 0) { setForeground(GRIS_TXT); setFont(getFont().deriveFont(Font.BOLD, 10f)); }
                if (col == 7 && v != null) { // prix_vente
                    setForeground(VERT); setFont(getFont().deriveFont(Font.BOLD));
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
        JButton bMod = btnStyle("✏  Modifier",   BLEU);
        JButton bSup = btnStyle("🗑  Supprimer",  ROUGE);
        JButton bAct = btnStyle("↻  Actualiser", GRIS_TXT);
        bMod.addActionListener(e -> chargerLigne());
        bSup.addActionListener(e -> supprimer());
        bAct.addActionListener(e -> charger());
        p.add(bMod); p.add(bSup);
        p.add(Box.createHorizontalStrut(8)); p.add(bAct);
        return p;
    }

    /* ── PANEL DROIT : formulaire ───────────────────────────────────── */
    private JPanel panelDroit() {
        JPanel ext = new JPanel(new BorderLayout());
        ext.setBackground(GRIS_FOND);
        ext.setBorder(new EmptyBorder(14, 7, 14, 14));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANC);
        card.setBorder(BorderFactory.createLineBorder(GRIS_BRD));

        // Titre
        JPanel titre = new JPanel(new BorderLayout());
        titre.setBackground(BLEU_CL);
        titre.setBorder(new EmptyBorder(11, 18, 11, 18));
        JLabel tl = new JLabel("Informations du médicament");
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(BLEU);
        titre.add(tl, BorderLayout.WEST);
        card.add(titre, BorderLayout.NORTH);

        // Champs formulaire
        JPanel champs = new JPanel();
        champs.setLayout(new BoxLayout(champs, BoxLayout.Y_AXIS));
        champs.setBackground(BLANC);
        champs.setBorder(new EmptyBorder(16, 22, 10, 22));

        // Ligne 1 : Code CIP + ID
        champs.add(labelF("Code CIP *"));
        champs.add(Box.createVerticalStrut(4));
        fCodeCip = field("ex: 0001");
        champs.add(fCodeCip);
        champs.add(Box.createVerticalStrut(12));

        // Nom commercial
        champs.add(labelF("Nom commercial *"));
        champs.add(Box.createVerticalStrut(4));
        fNomCommercial = field("ex: Doliprane");
        champs.add(fNomCommercial);
        champs.add(Box.createVerticalStrut(12));

        // Nom générique
        champs.add(labelF("Nom générique *"));
        champs.add(Box.createVerticalStrut(4));
        fNomGenerique = field("ex: Paracétamol");
        champs.add(fNomGenerique);
        champs.add(Box.createVerticalStrut(12));

        // Dosage + Forme (côte à côte)
        JPanel row1 = new JPanel(new GridLayout(1, 2, 10, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.setAlignmentX(0f);
        JPanel pDosage = colField("Dosage *", "ex: 500mg");
        JPanel pForme  = colField("Forme *",  "ex: Comprimé");
        fDosage = extractField(pDosage);
        fForme  = extractField(pForme);
        row1.add(pDosage); row1.add(pForme);
        champs.add(row1);
        champs.add(Box.createVerticalStrut(12));

        // Prix achat + Prix vente (côte à côte)
        JPanel row2 = new JPanel(new GridLayout(1, 2, 10, 0));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.setAlignmentX(0f);
        JPanel pAchat = colField("Prix achat (FCFA) *", "ex: 350");
        JPanel pVente = colField("Prix vente (FCFA) *", "ex: 520");
        fPrixAchat = extractField(pAchat);
        fPrixVente = extractField(pVente);
        row2.add(pAchat); row2.add(pVente);
        champs.add(row2);
        champs.add(Box.createVerticalStrut(12));

        // Catégorie (FK → table Categorie)
        champs.add(labelF("Catégorie *"));
        champs.add(Box.createVerticalStrut(4));
        cbCategorie = new JComboBox<>();
        cbCategorie.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCategorie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cbCategorie.setAlignmentX(0f);
        cbCategorie.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String[]) setText(((String[]) value)[1]);
                return this;
            }
        });
        champs.add(cbCategorie);

        JScrollPane sc = new JScrollPane(champs);
        sc.setBorder(null);
        sc.getViewport().setBackground(BLANC);
        card.add(sc, BorderLayout.CENTER);
        card.add(panelBoutons(), BorderLayout.SOUTH);
        ext.add(card, BorderLayout.CENTER);
        return ext;
    }

    private JPanel panelBoutons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(new Color(247, 250, 254));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BRD));
        btnAnnuler = btnStyle("Annuler",      GRIS_TXT);
        btnSave    = btnStyle("Enregistrer",  BLANC);
        btnSave.setBackground(BLEU);
        btnAnnuler.addActionListener(e -> vider());
        btnSave.addActionListener(e -> sauvegarder());
        p.add(btnAnnuler); p.add(btnSave);
        return p;
    }

    /* ══════════════════════════════════════════════════════════════════
       OPÉRATIONS SQL
    ══════════════════════════════════════════════════════════════════ */

    /** Charger les catégories dans le combobox (id_categorie, nom_categorie) */
    private void chargerCategories() {
        try (Connection c = DBconnexion.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                 "SELECT id_categorie, nom_categorie FROM \"Categorie\" ORDER BY nom_categorie")) {
            cbCategorie.removeAllItems();
            cbFiltreCategorie.removeAllItems();
            cbFiltreCategorie.addItem("Toutes catégories");
            while (rs.next()) {
                String[] item = {rs.getString("id_categorie"), rs.getString("nom_categorie")};
                cbCategorie.addItem(item);
                cbFiltreCategorie.addItem(rs.getString("nom_categorie"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Impossible de charger les catégories :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** SELECT tous les médicaments avec jointure Categorie */
    private void charger() {
        modele.setRowCount(0);
        String sql = "SELECT m.id_medicament, m.code_cip, m.nom_commercial, m.nom_generique, " +
                     "m.dosage, m.forme, m.prix_achat, m.prix_vente, c.nom_categorie " +
                     "FROM \"Medicament\" m " +
                     "LEFT JOIN \"Categorie\" c ON m.id_categorie = c.id_categorie " +
                     "ORDER BY m.nom_commercial";
        try (Connection c = DBconnexion.getConnection();
             Statement  s = c.createStatement();
             ResultSet  rs = s.executeQuery(sql)) {
            int nb = 0;
            while (rs.next()) {
                modele.addRow(new Object[]{
                    rs.getString("id_medicament"),
                    rs.getString("code_cip"),
                    rs.getString("nom_commercial"),
                    rs.getString("nom_generique"),
                    rs.getString("dosage"),
                    rs.getString("forme"),
                    rs.getBigDecimal("prix_achat"),
                    rs.getBigDecimal("prix_vente"),
                    rs.getString("nom_categorie")
                });
                nb++;
            }
            lblNb.setText(nb + " médicament(s)");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Chargement impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** INSERT INTO Medicament */
    private void inserer(String id, String cip, String nomCom, String nomGen,
                         String dosage, String forme, double pAchat, double pVente,
                         String idCat) throws SQLException {
        String sql = "INSERT INTO \"Medicament\"(id_medicament,code_cip,nom_commercial," +
                     "nom_generique,dosage,forme,prix_achat,prix_vente,id_categorie) " +
                     "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));  ps.setString(2, cip);
            ps.setString(3, nomCom); ps.setString(4, nomGen);
            ps.setString(5, dosage); ps.setString(6, forme);
            ps.setDouble(7, pAchat); ps.setDouble(8, pVente);
            ps.setString(9, idCat);
            ps.executeUpdate();
        }
    }

    /** UPDATE Medicament */
    private void modifier(String id, String cip, String nomCom, String nomGen,
                          String dosage, String forme, double pAchat, double pVente,
                          String idCat) throws SQLException {
        String sql = "UPDATE \"Medicament\" SET code_cip=?,nom_commercial=?,nom_generique=?," +
                     "dosage=?,forme=?,prix_achat=?,prix_vente=?,id_categorie=? " +
                     "WHERE id_medicament=?";
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cip);    ps.setString(2, nomCom);
            ps.setString(3, nomGen); ps.setString(4, dosage);
            ps.setString(5, forme);  ps.setDouble(6, pAchat);
            ps.setDouble(7, pVente); ps.setString(8, idCat);
            ps.setString(9, id);
            ps.executeUpdate();
        }
    }

    /** DELETE FROM Medicament */
    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un médicament."); return; }
        int mr = table.convertRowIndexToModel(row);
        int id  = Integer.parseInt(s(modele.getValueAt(mr, 0)));
        String nom = s(modele.getValueAt(mr, 2));
        if (JOptionPane.showConfirmDialog(this,
            "Supprimer « " + nom + " » ?", "Confirmation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection c = DBconnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM \"Medicament\" WHERE id_medicament=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            charger(); vider();
            JOptionPane.showMessageDialog(this, "Médicament supprimé.", "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { err("Suppression impossible :\n" + ex.getMessage()); }
    }

    private void sauvegarder() {
    String cip    = fCodeCip.getText().trim();
    String nomCom = fNomCommercial.getText().trim();
    String nomGen = fNomGenerique.getText().trim();
    String dosage = fDosage.getText().trim();
    String forme  = fForme.getText().trim();
    String sAchat = fPrixAchat.getText().trim();
    String sVente = fPrixVente.getText().trim();

    if (cip.isEmpty() || nomCom.isEmpty() || nomGen.isEmpty() || 
        dosage.isEmpty() || forme.isEmpty()) {
        warn("Tous les champs marqués * sont obligatoires."); 
        return;
    }

    double pAchat, pVente;
    try {
        pAchat = Double.parseDouble(sAchat.replace(",", "."));
        pVente = Double.parseDouble(sVente.replace(",", "."));
    } catch (NumberFormatException ex) {
        warn("Les prix doivent être des nombres valides (ex: 520)"); 
        return;
    }

    if (pVente < pAchat) {
        warn("Le prix de vente ne peut pas être inférieur au prix d'achat."); 
        return;
    }

    // id_categorie en int
    int idCat = 0;
    if (cbCategorie.getSelectedItem() instanceof String[]) {
        idCat = Integer.parseInt(((String[]) cbCategorie.getSelectedItem())[0]);
    }

    // date_expiration automatique = aujourd'hui + 1 an
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.YEAR, 1);
    java.sql.Date dateExp = new java.sql.Date(cal.getTimeInMillis());

    try (Connection c = DBconnexion.getConnection()) {
        if (modeEdit) {
            // UPDATE
            String sql = "UPDATE \"Medicament\" SET code_cip=?, nom_commercial=?, nom_generique=?, " +
                         "dosage=?, forme=?, prix_achat=?, prix_vente=?, id_categorie=?, date_expiration=? " +
                         "WHERE id_medicament=?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cip);
                ps.setString(2, nomCom);
                ps.setString(3, nomGen);
                ps.setString(4, dosage);
                ps.setString(5, forme);
                ps.setDouble(6, pAchat);
                ps.setDouble(7, pVente);
                ps.setInt(8, idCat);
                ps.setDate(9, dateExp);
                ps.setInt(10, Integer.parseInt(idEnCours)); // idEnCours converti en int
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Médicament mis à jour.", "Succès",
                                          JOptionPane.INFORMATION_MESSAGE);
        } else {
            // INSERT
            String sql = "INSERT INTO \"Medicament\"(code_cip, nom_commercial, nom_generique, " +
                         "dosage, forme, prix_achat, prix_vente, id_categorie, date_expiration) " +
                         "VALUES(?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cip);
                ps.setString(2, nomCom);
                ps.setString(3, nomGen);
                ps.setString(4, dosage);
                ps.setString(5, forme);
                ps.setDouble(6, pAchat);
                ps.setDouble(7, pVente);
                ps.setInt(8, idCat);
                ps.setDate(9, dateExp);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Médicament enregistré.", "Succès",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

        charger(); 
        vider();
    } catch (SQLException ex) {
        err("Enregistrement impossible :\n" + ex.getMessage());
    }
}

    private void chargerLigne() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Sélectionnez un médicament."); return; }
        int mr = table.convertRowIndexToModel(row);
        idEnCours = s(modele.getValueAt(mr, 0));
        fCodeCip.setText(s(modele.getValueAt(mr, 1)));
        fNomCommercial.setText(s(modele.getValueAt(mr, 2)));
        fNomGenerique.setText(s(modele.getValueAt(mr, 3)));
        fDosage.setText(s(modele.getValueAt(mr, 4)));
        fForme.setText(s(modele.getValueAt(mr, 5)));
        fPrixAchat.setText(s(modele.getValueAt(mr, 6)));
        fPrixVente.setText(s(modele.getValueAt(mr, 7)));
        // Sélectionner catégorie dans combo
        String nomCat = s(modele.getValueAt(mr, 8));
        for (int i = 0; i < cbCategorie.getItemCount(); i++) {
            if (((String[]) cbCategorie.getItemAt(i))[1].equals(nomCat)) {
                cbCategorie.setSelectedIndex(i); break;
            }
        }
        modeEdit = true;
        btnSave.setText("Mettre à jour");
    }

    private void filtrer() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modele);
        table.setRowSorter(sorter);
        String txt = champRecherche.getText().trim();
        String cat = cbFiltreCategorie.getSelectedIndex() <= 0 ? "" :
                     (String) cbFiltreCategorie.getSelectedItem();
        RowFilter<DefaultTableModel, Object> fTxt = txt.isEmpty() ? null :
            RowFilter.regexFilter("(?i)" + txt);
        RowFilter<DefaultTableModel, Object> fCat = cat.isEmpty() ? null :
            RowFilter.regexFilter("(?i)" + cat, 8);
        if (fTxt != null && fCat != null)
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(fTxt, fCat)));
        else if (fTxt != null) sorter.setRowFilter(fTxt);
        else if (fCat != null) sorter.setRowFilter(fCat);
        else sorter.setRowFilter(null);
    }

    private void vider() {
        fCodeCip.setText(""); fNomCommercial.setText(""); fNomGenerique.setText("");
        fDosage.setText(""); fForme.setText(""); fPrixAchat.setText(""); fPrixVente.setText("");
        if (cbCategorie.getItemCount() > 0) cbCategorie.setSelectedIndex(0);
        modeEdit = false; idEnCours = null;
        btnSave.setText("Enregistrer");
        table.clearSelection();
    }

    /* ── HELPERS UI ──────────────────────────────────────────────────── */
    private JLabel labelF(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(45, 62, 88));
        l.setAlignmentX(0f);
        return l;
    }
    private JTextField field(String ph) {
        JTextField f = new JTextField() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(new Color(175, 188, 205));
                    g.setFont(getFont().deriveFont(Font.ITALIC));
                    g.drawString(ph, 10, getHeight()/2+5);
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
                    BorderFactory.createLineBorder(BLEU, 2),
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
    private JPanel colField(String label, String ph) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = labelF(label); p.add(l);
        p.add(Box.createVerticalStrut(4));
        JTextField f = field(ph); p.add(f);
        return p;
    }
    private JTextField extractField(JPanel p) {
        for (Component c : p.getComponents())
            if (c instanceof JTextField) return (JTextField) c;
        return new JTextField();
    }
    private JButton btnStyle(String txt, Color fg) {
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
    private String s(Object o) { return o == null ? "" : o.toString(); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Attention", JOptionPane.WARNING_MESSAGE); }
    private void err(String m)  { JOptionPane.showMessageDialog(this, m, "Erreur",    JOptionPane.ERROR_MESSAGE); }
    
    public static void main(String[] args) {
    // Pour lancer la création et l'affichage de la fenêtre dans le thread de l'interface graphique Swing
    SwingUtilities.invokeLater(() -> {
        new GestionMedicaments();
    });
}
}