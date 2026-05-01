package entities;

import entities.Alerte;
import entities.Approvisionnement;
import entities.Categorie;
import entities.LigneVente;
import entities.Lotmedicament;
import entities.Stock;
import java.math.BigInteger;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Medicament.class)
public class Medicament_ { 

    public static volatile ListAttribute<Medicament, LigneVente> ligneVenteList;
    public static volatile SingularAttribute<Medicament, String> dosage;
    public static volatile SingularAttribute<Medicament, BigInteger> prixAchat;
    public static volatile ListAttribute<Medicament, Lotmedicament> lotmedicamentList;
    public static volatile ListAttribute<Medicament, Stock> stockList;
    public static volatile ListAttribute<Medicament, Approvisionnement> approvisionnementList;
    public static volatile SingularAttribute<Medicament, BigInteger> prixVente;
    public static volatile SingularAttribute<Medicament, String> forme;
    public static volatile ListAttribute<Medicament, Alerte> alerteList;
    public static volatile SingularAttribute<Medicament, Integer> idMedicament;
    public static volatile SingularAttribute<Medicament, Date> dateExpiration;
    public static volatile SingularAttribute<Medicament, String> nomCommercial;
    public static volatile SingularAttribute<Medicament, Categorie> idCategorie;
    public static volatile SingularAttribute<Medicament, String> nomGenerique;
    public static volatile SingularAttribute<Medicament, String> codeCip;

}