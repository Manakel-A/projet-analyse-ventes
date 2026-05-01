package entities;

import entities.LigneVente;
import entities.Pharmacien;
import java.math.BigInteger;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Vente.class)
public class Vente_ { 

    public static volatile ListAttribute<Vente, LigneVente> ligneVenteList;
    public static volatile SingularAttribute<Vente, BigInteger> total;
    public static volatile SingularAttribute<Vente, String> numeroFacture;
    public static volatile SingularAttribute<Vente, Pharmacien> idPharmacien;
    public static volatile SingularAttribute<Vente, Date> dateVente;
    public static volatile SingularAttribute<Vente, String> modePaiement;
    public static volatile SingularAttribute<Vente, Integer> idVente;

}