/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

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
@Table(name = "Lot_medicament")
@NamedQueries({
    @NamedQuery(name = "Lotmedicament.findAll", query = "SELECT l FROM Lotmedicament l"),
    @NamedQuery(name = "Lotmedicament.findByIdLot", query = "SELECT l FROM Lotmedicament l WHERE l.idLot = :idLot"),
    @NamedQuery(name = "Lotmedicament.findByNumeroLot", query = "SELECT l FROM Lotmedicament l WHERE l.numeroLot = :numeroLot"),
    @NamedQuery(name = "Lotmedicament.findByDateExpiration", query = "SELECT l FROM Lotmedicament l WHERE l.dateExpiration = :dateExpiration"),
    @NamedQuery(name = "Lotmedicament.findByQuantite", query = "SELECT l FROM Lotmedicament l WHERE l.quantite = :quantite")})
public class Lotmedicament implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_lot")
    private Integer idLot;
    @Basic(optional = false)
    @Column(name = "numero_lot")
    private String numeroLot;
    @Basic(optional = false)
    @Column(name = "date_expiration")
    @Temporal(TemporalType.DATE)
    private Date dateExpiration;
    @Basic(optional = false)
    @Column(name = "quantite")
    private int quantite;
    @JoinColumn(name = "id_medicament", referencedColumnName = "id_medicament")
    @ManyToOne(optional = false)
    private Medicament idMedicament;

    public Lotmedicament() {
    }

    public Lotmedicament(Integer idLot) {
        this.idLot = idLot;
    }

    public Lotmedicament(Integer idLot, String numeroLot, Date dateExpiration, int quantite) {
        this.idLot = idLot;
        this.numeroLot = numeroLot;
        this.dateExpiration = dateExpiration;
        this.quantite = quantite;
    }

    public Integer getIdLot() {
        return idLot;
    }

    public void setIdLot(Integer idLot) {
        this.idLot = idLot;
    }

    public String getNumeroLot() {
        return numeroLot;
    }

    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Medicament getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(Medicament idMedicament) {
        this.idMedicament = idMedicament;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLot != null ? idLot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lotmedicament)) {
            return false;
        }
        Lotmedicament other = (Lotmedicament) object;
        if ((this.idLot == null && other.idLot != null) || (this.idLot != null && !this.idLot.equals(other.idLot))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Lotmedicament[ idLot=" + idLot + " ]";
    }
    
}
