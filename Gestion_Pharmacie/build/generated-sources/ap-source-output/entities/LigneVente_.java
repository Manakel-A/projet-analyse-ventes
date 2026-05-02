package entities;

import entities.Medicament;
import entities.Vente;
import java.math.BigInteger;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(LigneVente.class)
public class LigneVente_ { 

    public static volatile SingularAttribute<LigneVente, BigInteger> sousTotal;
    public static volatile SingularAttribute<LigneVente, BigInteger> prixUnitaire;
    public static volatile SingularAttribute<LigneVente, Medicament> idMedicament;
    public static volatile SingularAttribute<LigneVente, Integer> idLigne;
    public static volatile SingularAttribute<LigneVente, Vente> idVente;
    public static volatile SingularAttribute<LigneVente, Integer> quantite;

}