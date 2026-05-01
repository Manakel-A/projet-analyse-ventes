package entities;

import entities.Medicament;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Lotmedicament.class)
public class Lotmedicament_ { 

    public static volatile SingularAttribute<Lotmedicament, Integer> idLot;
    public static volatile SingularAttribute<Lotmedicament, Medicament> idMedicament;
    public static volatile SingularAttribute<Lotmedicament, Date> dateExpiration;
    public static volatile SingularAttribute<Lotmedicament, String> numeroLot;
    public static volatile SingularAttribute<Lotmedicament, Integer> quantite;

}