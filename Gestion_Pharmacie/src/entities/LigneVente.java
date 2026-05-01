/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "Ligne_Vente")
@NamedQueries({
    @NamedQuery(name = "LigneVente.findAll", query = "SELECT l FROM LigneVente l"),
    @NamedQuery(name = "LigneVente.findByIdLigne", query = "SELECT l FROM LigneVente l WHERE l.idLigne = :idLigne"),
    @NamedQuery(name = "LigneVente.findByQuantite", query = "SELECT l FROM LigneVente l WHERE l.quantite = :quantite"),
    @NamedQuery(name = "LigneVente.findByPrixUnitaire", query = "SELECT l FROM LigneVente l WHERE l.prixUnitaire = :prixUnitaire"),
    @NamedQuery(name = "LigneVente.findBySousTotal", query = "SELECT l FROM LigneVente l WHERE l.sousTotal = :sousTotal")})
public class LigneVente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_ligne")
    private Integer idLigne;
    @Basic(optional = false)
    @Column(name = "quantite")
    private int quantite;
    @Basic(optional = false)
    @Column(name = "prix_unitaire")
    private BigInteger prixUnitaire;
    @Basic(optional = false)
    @Column(name = "sous_total")
    private BigInteger sousTotal;
    @JoinColumn(name = "id_medicament", referencedColumnName = "id_medicament")
    @ManyToOne(optional = false)
    private Medicament idMedicament;
    @JoinColumn(name = "id_vente", referencedColumnName = "id_vente")
    @ManyToOne(optional = false)
    private Vente idVente;

    public LigneVente() {
    }

    public LigneVente(Integer idLigne) {
        this.idLigne = idLigne;
    }

    public LigneVente(Integer idLigne, int quantite, BigInteger prixUnitaire, BigInteger sousTotal) {
        this.idLigne = idLigne;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.sousTotal = sousTotal;
    }

    public Integer getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(Integer idLigne) {
        this.idLigne = idLigne;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigInteger getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigInteger prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigInteger getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(BigInteger sousTotal) {
        this.sousTotal = sousTotal;
    }

    public Medicament getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(Medicament idMedicament) {
        this.idMedicament = idMedicament;
    }

    public Vente getIdVente() {
        return idVente;
    }

    public void setIdVente(Vente idVente) {
        this.idVente = idVente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLigne != null ? idLigne.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LigneVente)) {
            return false;
        }
        LigneVente other = (LigneVente) object;
        if ((this.idLigne == null && other.idLigne != null) || (this.idLigne != null && !this.idLigne.equals(other.idLigne))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.LigneVente[ idLigne=" + idLigne + " ]";
    }
    
}
