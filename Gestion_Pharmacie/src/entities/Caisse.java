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
@Table(name = "Caisse")
@NamedQueries({
    @NamedQuery(name = "Caisse.findAll", query = "SELECT c FROM Caisse c"),
    @NamedQuery(name = "Caisse.findByIdOperation", query = "SELECT c FROM Caisse c WHERE c.idOperation = :idOperation"),
    @NamedQuery(name = "Caisse.findByTypeOperation", query = "SELECT c FROM Caisse c WHERE c.typeOperation = :typeOperation"),
    @NamedQuery(name = "Caisse.findByMontant", query = "SELECT c FROM Caisse c WHERE c.montant = :montant"),
    @NamedQuery(name = "Caisse.findByDateOperation", query = "SELECT c FROM Caisse c WHERE c.dateOperation = :dateOperation"),
    @NamedQuery(name = "Caisse.findByDescription", query = "SELECT c FROM Caisse c WHERE c.description = :description")})
public class Caisse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_operation")
    private Integer idOperation;
    @Basic(optional = false)
    @Column(name = "type_operation")
    private String typeOperation;
    @Basic(optional = false)
    @Column(name = "montant")
    private BigInteger montant;
    @Basic(optional = false)
    @Column(name = "date_operation")
    @Temporal(TemporalType.DATE)
    private Date dateOperation;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;

    public Caisse() {
    }

    public Caisse(Integer idOperation) {
        this.idOperation = idOperation;
    }

    public Caisse(Integer idOperation, String typeOperation, BigInteger montant, Date dateOperation, String description) {
        this.idOperation = idOperation;
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.dateOperation = dateOperation;
        this.description = description;
    }

    public Integer getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Integer idOperation) {
        this.idOperation = idOperation;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public BigInteger getMontant() {
        return montant;
    }

    public void setMontant(BigInteger montant) {
        this.montant = montant;
    }

    public Date getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOperation != null ? idOperation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Caisse)) {
            return false;
        }
        Caisse other = (Caisse) object;
        if ((this.idOperation == null && other.idOperation != null) || (this.idOperation != null && !this.idOperation.equals(other.idOperation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Caisse[ idOperation=" + idOperation + " ]";
    }
    
}
