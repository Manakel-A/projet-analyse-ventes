/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "Medicament")
@NamedQueries({
    @NamedQuery(name = "Medicament.findAll", query = "SELECT m FROM Medicament m"),
    @NamedQuery(name = "Medicament.findByIdMedicament", query = "SELECT m FROM Medicament m WHERE m.idMedicament = :idMedicament"),
    @NamedQuery(name = "Medicament.findByCodeCip", query = "SELECT m FROM Medicament m WHERE m.codeCip = :codeCip"),
    @NamedQuery(name = "Medicament.findByNomCommercial", query = "SELECT m FROM Medicament m WHERE m.nomCommercial = :nomCommercial"),
    @NamedQuery(name = "Medicament.findByNomGenerique", query = "SELECT m FROM Medicament m WHERE m.nomGenerique = :nomGenerique"),
    @NamedQuery(name = "Medicament.findByDosage", query = "SELECT m FROM Medicament m WHERE m.dosage = :dosage"),
    @NamedQuery(name = "Medicament.findByForme", query = "SELECT m FROM Medicament m WHERE m.forme = :forme"),
    @NamedQuery(name = "Medicament.findByPrixAchat", query = "SELECT m FROM Medicament m WHERE m.prixAchat = :prixAchat"),
    @NamedQuery(name = "Medicament.findByPrixVente", query = "SELECT m FROM Medicament m WHERE m.prixVente = :prixVente"),
    @NamedQuery(name = "Medicament.findByDateExpiration", query = "SELECT m FROM Medicament m WHERE m.dateExpiration = :dateExpiration")})
public class Medicament implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_medicament")
    private Integer idMedicament;
    @Basic(optional = false)
    @Column(name = "code_cip")
    private String codeCip;
    @Basic(optional = false)
    @Column(name = "nom_commercial")
    private String nomCommercial;
    @Basic(optional = false)
    @Column(name = "nom_generique")
    private String nomGenerique;
    @Basic(optional = false)
    @Column(name = "dosage")
    private String dosage;
    @Basic(optional = false)
    @Column(name = "forme")
    private String forme;
    @Basic(optional = false)
    @Column(name = "prix_achat")
    private BigInteger prixAchat;
    @Basic(optional = false)
    @Column(name = "prix_vente")
    private BigInteger prixVente;
    @Basic(optional = false)
    @Column(name = "date_expiration")
    @Temporal(TemporalType.DATE)
    private Date dateExpiration;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMedicament")
    private List<Lotmedicament> lotmedicamentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMedicament")
    private List<Approvisionnement> approvisionnementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMedicament")
    private List<Alerte> alerteList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMedicament")
    private List<LigneVente> ligneVenteList;
    @JoinColumn(name = "id_categorie", referencedColumnName = "id_categorie")
    @ManyToOne(optional = false)
    private Categorie idCategorie;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMedicament")
    private List<Stock> stockList;

    public Medicament() {
    }

    public Medicament(Integer idMedicament) {
        this.idMedicament = idMedicament;
    }

    public Medicament(Integer idMedicament, String codeCip, String nomCommercial, String nomGenerique, String dosage, String forme, BigInteger prixAchat, BigInteger prixVente, Date dateExpiration) {
        this.idMedicament = idMedicament;
        this.codeCip = codeCip;
        this.nomCommercial = nomCommercial;
        this.nomGenerique = nomGenerique;
        this.dosage = dosage;
        this.forme = forme;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.dateExpiration = dateExpiration;
    }

    public Integer getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(Integer idMedicament) {
        this.idMedicament = idMedicament;
    }

    public String getCodeCip() {
        return codeCip;
    }

    public void setCodeCip(String codeCip) {
        this.codeCip = codeCip;
    }

    public String getNomCommercial() {
        return nomCommercial;
    }

    public void setNomCommercial(String nomCommercial) {
        this.nomCommercial = nomCommercial;
    }

    public String getNomGenerique() {
        return nomGenerique;
    }

    public void setNomGenerique(String nomGenerique) {
        this.nomGenerique = nomGenerique;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getForme() {
        return forme;
    }

    public void setForme(String forme) {
        this.forme = forme;
    }

    public BigInteger getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(BigInteger prixAchat) {
        this.prixAchat = prixAchat;
    }

    public BigInteger getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigInteger prixVente) {
        this.prixVente = prixVente;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public List<Lotmedicament> getLotmedicamentList() {
        return lotmedicamentList;
    }

    public void setLotmedicamentList(List<Lotmedicament> lotmedicamentList) {
        this.lotmedicamentList = lotmedicamentList;
    }

    public List<Approvisionnement> getApprovisionnementList() {
        return approvisionnementList;
    }

    public void setApprovisionnementList(List<Approvisionnement> approvisionnementList) {
        this.approvisionnementList = approvisionnementList;
    }

    public List<Alerte> getAlerteList() {
        return alerteList;
    }

    public void setAlerteList(List<Alerte> alerteList) {
        this.alerteList = alerteList;
    }

    public List<LigneVente> getLigneVenteList() {
        return ligneVenteList;
    }

    public void setLigneVenteList(List<LigneVente> ligneVenteList) {
        this.ligneVenteList = ligneVenteList;
    }

    public Categorie getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(Categorie idCategorie) {
        this.idCategorie = idCategorie;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMedicament != null ? idMedicament.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medicament)) {
            return false;
        }
        Medicament other = (Medicament) object;
        if ((this.idMedicament == null && other.idMedicament != null) || (this.idMedicament != null && !this.idMedicament.equals(other.idMedicament))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Medicament[ idMedicament=" + idMedicament + " ]";
    }
    
}
