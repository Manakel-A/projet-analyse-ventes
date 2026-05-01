package entities;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2026-04-03T16:11:23", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Caisse.class)
public class Caisse_ { 

    public static volatile SingularAttribute<Caisse, Integer> idOperation;
    public static volatile SingularAttribute<Caisse, String> typeOperation;
    public static volatile SingularAttribute<Caisse, String> description;
    public static volatile SingularAttribute<Caisse, BigInteger> montant;
    public static volatile SingularAttribute<Caisse, Date> dateOperation;

}