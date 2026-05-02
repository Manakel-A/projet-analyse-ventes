/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.io.Serializable;
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
@Table(name = "Stock")
@NamedQueries({
    @NamedQuery(name = "Stock.findAll", query = "SELECT s FROM Stock s"),
    @NamedQuery(name = "Stock.findByIdStock", query = "SELECT s FROM Stock s WHERE s.idStock = :idStock"),
    @NamedQuery(name = "Stock.findByQuantiteDisponible", query = "SELECT s FROM Stock s WHERE s.quantiteDisponible = :quantiteDisponible"),
    @NamedQuery(name = "Stock.findBySeuilAlerte", query = "SELECT s FROM Stock s WHERE s.seuilAlerte = :seuilAlerte")})
public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_stock")
    private Integer idStock;
    @Basic(optional = false)
    @Column(name = "quantite_disponible")
    private int quantiteDisponible;
    @Basic(optional = false)
    @Column(name = "seuil_alerte")
    private int seuilAlerte;
    @JoinColumn(name = "id_medicament", referencedColumnName = "id_medicament")
    @ManyToOne(optional = false)
    private Medicament idMedicament;

    public Stock() {
    }

    public Stock(Integer idStock) {
        this.idStock = idStock;
    }

    public Stock(Integer idStock, int quantiteDisponible, int seuilAlerte) {
        this.idStock = idStock;
        this.quantiteDisponible = quantiteDisponible;
        this.seuilAlerte = seuilAlerte;
    }

    public Integer getIdStock() {
        return idStock;
    }

    public void setIdStock(Integer idStock) {
        this.idStock = idStock;
    }

    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(int quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
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
        hash += (idStock != null ? idStock.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stock)) {
            return false;
        }
        Stock other = (Stock) object;
        if ((this.idStock == null && other.idStock != null) || (this.idStock != null && !this.idStock.equals(other.idStock))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Stock[ idStock=" + idStock + " ]";
    }
    
}
