package org.globsframework.sql.constraints.impl;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.sql.constraints.Constraint;
import org.globsframework.sql.constraints.ConstraintVisitor;

public class ContainsConstraint implements Constraint {
    public final Field field;
    public final String value;
    public final boolean startWith;
    private final boolean contains;
    private final boolean ignoreCase;

    public ContainsConstraint(Field field, String value, boolean startWith, boolean contains, boolean ignoreCase) {
        this.field = field;
        this.value = value;
        this.startWith = startWith;
        this.contains = contains;
        this.ignoreCase = ignoreCase;
    }

    public <T extends ConstraintVisitor> T accept(T visitor) {
        visitor.visitContains(field, value, contains, startWith, ignoreCase);
        return visitor;
    }

}
