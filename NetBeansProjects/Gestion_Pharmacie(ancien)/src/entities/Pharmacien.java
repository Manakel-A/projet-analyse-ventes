/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion_pharmacie;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author DELL
 */
@Entity
@Table(name = "pharmacien")
@NamedQueries({
    @NamedQuery(name = "Pharmacien.findAll", query = "SELECT p FROM Pharmacien p"),
    @NamedQuery(name = "Pharmacien.findByIdPharmacien", query = "SELECT p FROM Pharmacien p WHERE p.idPharmacien = :idPharmacien"),
    @NamedQuery(name = "Pharmacien.findByNom", query = "SELECT p FROM Pharmacien p WHERE p.nom = :nom"),
    @NamedQuery(name = "Pharmacien.findByPrenom", query = "SELECT p FROM Pharmacien p WHERE p.prenom = :prenom"),
    @NamedQuery(name = "Pharmacien.findByEmail", query = "SELECT p FROM Pharmacien p WHERE p.email = :email"),
    @NamedQuery(name = "Pharmacien.findByMotdepasse", query = "SELECT p FROM Pharmacien p WHERE p.motdepasse = :motdepasse")})
public class Pharmacien implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idPharmacien")
    private String idPharmacien;
    @Basic(optional = false)
    @Column(name = "nom")
    private String nom;
    @Basic(optional = false)
    @Column(name = "prenom")
    private String prenom;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "motdepasse")
    private String motdepasse;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPharmacien")
    private List<Vente> venteList;

    public Pharmacien() {
    }

    public Pharmacien(String idPharmacien) {
        this.idPharmacien = idPharmacien;
    }

    public Pharmacien(String idPharmacien, String nom, String prenom, String email, String motdepasse) {
        this.idPharmacien = idPharmacien;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motdepasse = motdepasse;
    }

    public String getIdPharmacien() {
        return idPharmacien;
    }

    public void setIdPharmacien(String idPharmacien) {
        this.idPharmacien = idPharmacien;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public List<Vente> getVenteList() {
        return venteList;
    }

    public void setVenteList(List<Vente> venteList) {
        this.venteList = venteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPharmacien != null ? idPharmacien.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pharmacien)) {
            return false;
        }
        Pharmacien other = (Pharmacien) object;
        if ((this.idPharmacien == null && other.idPharmacien != null) || (this.idPharmacien != null && !this.idPharmacien.equals(other.idPharmacien))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gestion_pharmacie.Pharmacien[ idPharmacien=" + idPharmacien + " ]";
    }
    
}
