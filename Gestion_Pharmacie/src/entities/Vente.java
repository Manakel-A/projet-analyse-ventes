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
@Table(name = "Vente")
@NamedQueries({
    @NamedQuery(name = "Vente.findAll", query = "SELECT v FROM Vente v"),
    @NamedQuery(name = "Vente.findByIdVente", query = "SELECT v FROM Vente v WHERE v.idVente = :idVente"),
    @NamedQuery(name = "Vente.findByNumeroFacture", query = "SELECT v FROM Vente v WHERE v.numeroFacture = :numeroFacture"),
    @NamedQuery(name = "Vente.findByDateVente", query = "SELECT v FROM Vente v WHERE v.dateVente = :dateVente"),
    @NamedQuery(name = "Vente.findByTotal", query = "SELECT v FROM Vente v WHERE v.total = :total"),
    @NamedQuery(name = "Vente.findByModePaiement", query = "SELECT v FROM Vente v WHERE v.modePaiement = :modePaiement")})
public class Vente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_vente")
    private Integer idVente;
    @Basic(optional = false)
    @Column(name = "numero_facture")
    private String numeroFacture;
    @Basic(optional = false)
    @Column(name = "date_vente")
    @Temporal(TemporalType.DATE)
    private Date dateVente;
    @Basic(optional = false)
    @Column(name = "total")
    private BigInteger total;
    @Basic(optional = false)
    @Column(name = "mode_paiement")
    private String modePaiement;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idVente")
    private List<LigneVente> ligneVenteList;
    @JoinColumn(name = "id_pharmacien", referencedColumnName = "id_pharmacien")
    @ManyToOne(optional = false)
    private Pharmacien idPharmacien;

    public Vente() {
    }

    public Vente(Integer idVente) {
        this.idVente = idVente;
    }

    public Vente(Integer idVente, String numeroFacture, Date dateVente, BigInteger total, String modePaiement) {
        this.idVente = idVente;
        this.numeroFacture = numeroFacture;
        this.dateVente = dateVente;
        this.total = total;
        this.modePaiement = modePaiement;
    }

    public Integer getIdVente() {
        return idVente;
    }

    public void setIdVente(Integer idVente) {
        this.idVente = idVente;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public Date getDateVente() {
        return dateVente;
    }

    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }

    public BigInteger getTotal() {
        return total;
    }

    public void setTotal(BigInteger total) {
        this.total = total;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public List<LigneVente> getLigneVenteList() {
        return ligneVenteList;
    }

    public void setLigneVenteList(List<LigneVente> ligneVenteList) {
        this.ligneVenteList = ligneVenteList;
    }

    public Pharmacien getIdPharmacien() {
        return idPharmacien;
    }

    public void setIdPharmacien(Pharmacien idPharmacien) {
        this.idPharmacien = idPharmacien;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idVente != null ? idVente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vente)) {
            return false;
        }
        Vente other = (Vente) object;
        if ((this.idVente == null && other.idVente != null) || (this.idVente != null && !this.idVente.equals(other.idVente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Vente[ idVente=" + idVente + " ]";
    }
    
}
