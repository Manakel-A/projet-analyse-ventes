package entities;

import entities.Approvisionnement;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Fournisseur.class)
public class Fournisseur_ { 

    public static volatile SingularAttribute<Fournisseur, Integer> idFournisseur;
    public static volatile SingularAttribute<Fournisseur, String> adresse;
    public static volatile ListAttribute<Fournisseur, Approvisionnement> approvisionnementList;
    public static volatile SingularAttribute<Fournisseur, String> telephone;
    public static volatile SingularAttribute<Fournisseur, String> nom;
    public static volatile SingularAttribute<Fournisseur, String> email;

}