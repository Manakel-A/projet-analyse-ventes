/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion_pharmacie;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @Column(name = "idVente")
    private String idVente;
    @Basic(optional = false)
    @Column(name = "numeroFacture")
    private String numeroFacture;
    @Basic(optional = false)
    @Column(name = "dateVente")
    @Temporal(TemporalType.DATE)
    private Date dateVente;
    @Basic(optional = false)
    @Column(name = "total")
    private String total;
    @Basic(optional = false)
    @Column(name = "modePaiement")
    private String modePaiement;
    @JoinColumn(name = "idPharmacien", referencedColumnName = "idPharmacien")
    @ManyToOne(optional = false)
    private Pharmacien idPharmacien;

    public Vente() {
    }

    public Vente(String idVente) {
        this.idVente = idVente;
    }

    public Vente(String idVente, String numeroFacture, Date dateVente, String total, String modePaiement) {
        this.idVente = idVente;
        this.numeroFacture = numeroFacture;
        this.dateVente = dateVente;
        this.total = total;
        this.modePaiement = modePaiement;
    }

    public String getIdVente() {
        return idVente;
    }

    public void setIdVente(String idVente) {
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
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
        return "gestion_pharmacie.Vente[ idVente=" + idVente + " ]";
    }
    
}
