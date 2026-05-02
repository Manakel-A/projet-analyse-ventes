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
@Table(name = "Alerte")
@NamedQueries({
    @NamedQuery(name = "Alerte.findAll", query = "SELECT a FROM Alerte a"),
    @NamedQuery(name = "Alerte.findByIdAlerte", query = "SELECT a FROM Alerte a WHERE a.idAlerte = :idAlerte"),
    @NamedQuery(name = "Alerte.findByTypeAlerte", query = "SELECT a FROM Alerte a WHERE a.typeAlerte = :typeAlerte"),
    @NamedQuery(name = "Alerte.findByMessage", query = "SELECT a FROM Alerte a WHERE a.message = :message"),
    @NamedQuery(name = "Alerte.findByDateAlerte", query = "SELECT a FROM Alerte a WHERE a.dateAlerte = :dateAlerte"),
    @NamedQuery(name = "Alerte.findByEstLue", query = "SELECT a FROM Alerte a WHERE a.estLue = :estLue")})
public class Alerte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_alerte")
    private Integer idAlerte;
    @Basic(optional = false)
    @Column(name = "type_alerte")
    private String typeAlerte;
    @Basic(optional = false)
    @Column(name = "message")
    private String message;
    @Basic(optional = false)
    @Column(name = "date_alerte")
    @Temporal(TemporalType.DATE)
    private Date dateAlerte;
    @Basic(optional = false)
    @Column(name = "est_lue")
    private boolean estLue;
    @JoinColumn(name = "id_medicament", referencedColumnName = "id_medicament")
    @ManyToOne(optional = false)
    private Medicament idMedicament;

    public Alerte() {
    }

    public Alerte(Integer idAlerte) {
        this.idAlerte = idAlerte;
    }

    public Alerte(Integer idAlerte, String typeAlerte, String message, Date dateAlerte, boolean estLue) {
        this.idAlerte = idAlerte;
        this.typeAlerte = typeAlerte;
        this.message = message;
        this.dateAlerte = dateAlerte;
        this.estLue = estLue;
    }

    public Integer getIdAlerte() {
        return idAlerte;
    }

    public void setIdAlerte(Integer idAlerte) {
        this.idAlerte = idAlerte;
    }

    public String getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(String typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateAlerte() {
        return dateAlerte;
    }

    public void setDateAlerte(Date dateAlerte) {
        this.dateAlerte = dateAlerte;
    }

    public boolean getEstLue() {
        return estLue;
    }

    public void setEstLue(boolean estLue) {
        this.estLue = estLue;
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
        hash += (idAlerte != null ? idAlerte.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alerte)) {
            return false;
        }
        Alerte other = (Alerte) object;
        if ((this.idAlerte == null && other.idAlerte != null) || (this.idAlerte != null && !this.idAlerte.equals(other.idAlerte))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Alerte[ idAlerte=" + idAlerte + " ]";
    }
    
}
