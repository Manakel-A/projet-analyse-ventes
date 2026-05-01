package gestion_pharmacie;

import gestion_pharmacie.Vente;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-03-09T09:15:00", comments="EclipseLink-2.7.10.v20211216-rNA")
@StaticMetamodel(Pharmacien.class)
public class Pharmacien_ { 

    public static volatile SingularAttribute<Pharmacien, String> idPharmacien;
    public static volatile ListAttribute<Pharmacien, Vente> venteList;
    public static volatile SingularAttribute<Pharmacien, String> motdepasse;
    public static volatile SingularAttribute<Pharmacien, String> nom;
    public static volatile SingularAttribute<Pharmacien, String> prenom;
    public static volatile SingularAttribute<Pharmacien, String> email;

}