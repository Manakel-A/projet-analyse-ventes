package entities;

import entities.Medicament;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Stock.class)
public class Stock_ { 

    public static volatile SingularAttribute<Stock, Integer> idStock;
    public static volatile SingularAttribute<Stock, Integer> quantiteDisponible;
    public static volatile SingularAttribute<Stock, Medicament> idMedicament;
    public static volatile SingularAttribute<Stock, Integer> seuilAlerte;

}