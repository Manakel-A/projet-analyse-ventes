package entities;

import entities.Medicament;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:24", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Categorie.class)
public class Categorie_ { 

    public static volatile SingularAttribute<Categorie, String> nomCategorie;
    public static volatile SingularAttribute<Categorie, Integer> idCategorie;
    public static volatile SingularAttribute<Categorie, String> description;
    public static volatile ListAttribute<Categorie, Medicament> medicamentList;

}