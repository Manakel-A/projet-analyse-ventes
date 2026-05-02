package gestion_pharmacie;

import gestion_pharmacie.Pharmacien;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-03-09T09:15:00", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Vente.class)
public class Vente_ { 

    public static volatile SingularAttribute<Vente, String> total;
    public static volatile SingularAttribute<Vente, String> numeroFacture;
    public static volatile SingularAttribute<Vente, Pharmacien> idPharmacien;
    public static volatile SingularAttribute<Vente, Date> dateVente;
    public static volatile SingularAttribute<Vente, String> modePaiement;
    public static volatile SingularAttribute<Vente, String> idVente;

}