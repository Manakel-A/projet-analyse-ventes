package entities;

import entities.Fournisseur;
import entities.Medicament;
import java.math.BigInteger;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Approvisionnement.class)
public class Approvisionnement_ { 

    public static volatile SingularAttribute<Approvisionnement, Date> dateApprovisionnement;
    public static volatile SingularAttribute<Approvisionnement, Fournisseur> idFournisseur;
    public static volatile SingularAttribute<Approvisionnement, Medicament> idMedicament;
    public static volatile SingularAttribute<Approvisionnement, BigInteger> prixAchat;
    public static volatile SingularAttribute<Approvisionnement, Integer> idApprovisionnement;
    public static volatile SingularAttribute<Approvisionnement, Integer> quantite;

}