package entities;

import entities.Medicament;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Alerte.class)
public class Alerte_ { 

    public static volatile SingularAttribute<Alerte, Integer> idAlerte;
    public static volatile SingularAttribute<Alerte, Medicament> idMedicament;
    public static volatile SingularAttribute<Alerte, Boolean> estLue;
    public static volatile SingularAttribute<Alerte, String> typeAlerte;
    public static volatile SingularAttribute<Alerte, String> message;
    public static volatile SingularAttribute<Alerte, Date> dateAlerte;

}