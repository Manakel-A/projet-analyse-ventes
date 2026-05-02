package entities;

import entities.Vente;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Pharmacien.class)
public class Pharmacien_ { 

    public static volatile SingularAttribute<Pharmacien, String> motDePasse;
    public static volatile SingularAttribute<Pharmacien, String> role;
    public static volatile SingularAttribute<Pharmacien, Integer> idPharmacien;
    public static volatile ListAttribute<Pharmacien, Vente> venteList;
    public static volatile SingularAttribute<Pharmacien, String> nom;
    public static volatile SingularAttribute<Pharmacien, String> prenom;
    public static volatile SingularAttribute<Pharmacien, String> email;

}