/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
import java.math.BigInteger;
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
@Table(name = "Approvisionnement")
@NamedQueries({
    @NamedQuery(name = "Approvisionnement.findAll", query = "SELECT a FROM Approvisionnement a"),
    @NamedQuery(name = "Approvisionnement.findByIdApprovisionnement", query = "SELECT a FROM Approvisionnement a WHERE a.idApprovisionnement = :idApprovisionnement"),
    @NamedQuery(name = "Approvisionnement.findByDateApprovisionnement", query = "SELECT a FROM Approvisionnement a WHERE a.dateApprovisionnement = :dateApprovisionnement"),
    @NamedQuery(name = "Approvisionnement.findByQuantite", query = "SELECT a FROM Approvisionnement a WHERE a.quantite = :quantite"),
    @NamedQuery(name = "Approvisionnement.findByPrixAchat", query = "SELECT a FROM Approvisionnement a WHERE a.prixAchat = :prixAchat")})
public class Approvisionnement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_approvisionnement")
    private Integer idApprovisionnement;
    @Basic(optional = false)
    @Column(name = "date_approvisionnement")
    @Temporal(TemporalType.DATE)
    private Date dateApprovisionnement;
    @Basic(optional = false)
    @Column(name = "quantite")
    private int quantite;
    @Basic(optional = false)
    @Column(name = "prix_achat")
    private BigInteger prixAchat;
    @JoinColumn(name = "id_fournisseur", referencedColumnName = "id_fournisseur")
    @ManyToOne(optional = false)
    private Fournisseur idFournisseur;
    @JoinColumn(name = "id_medicament", referencedColumnName = "id_medicament")
    @ManyToOne(optional = false)
    private Medicament idMedicament;

    public Approvisionnement() {
    }

    public Approvisionnement(Integer idApprovisionnement) {
        this.idApprovisionnement = idApprovisionnement;
    }

    public Approvisionnement(Integer idApprovisionnement, Date dateApprovisionnement, int quantite, BigInteger prixAchat) {
        this.idApprovisionnement = idApprovisionnement;
        this.dateApprovisionnement = dateApprovisionnement;
        this.quantite = quantite;
        this.prixAchat = prixAchat;
    }

    public Integer getIdApprovisionnement() {
        return idApprovisionnement;
    }

    public void setIdApprovisionnement(Integer idApprovisionnement) {
        this.idApprovisionnement = idApprovisionnement;
    }

    public Date getDateApprovisionnement() {
        return dateApprovisionnement;
    }

    public void setDateApprovisionnement(Date dateApprovisionnement) {
        this.dateApprovisionnement = dateApprovisionnement;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigInteger getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(BigInteger prixAchat) {
        this.prixAchat = prixAchat;
    }

    public Fournisseur getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(Fournisseur idFournisseur) {
        this.idFournisseur = idFournisseur;
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
        hash += (idApprovisionnement != null ? idApprovisionnement.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Approvisionnement)) {
            return false;
        }
        Approvisionnement other = (Approvisionnement) object;
        if ((this.idApprovisionnement == null && other.idApprovisionnement != null) || (this.idApprovisionnement != null && !this.idApprovisionnement.equals(other.idApprovisionnement))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Approvisionnement[ idApprovisionnement=" + idApprovisionnement + " ]";
    }
    
}
