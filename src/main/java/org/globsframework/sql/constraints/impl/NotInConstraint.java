package org.globsframework.sql.constraints.impl;

import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.sql.constraints.Constraint;
import org.globsframework.sql.constraints.ConstraintVisitor;

import java.util.Set;

public class NotInConstraint implements Constraint {
    private Field field;
    private Set values;

    public NotInConstraint(Field field, Set values) {
        this.field = field;
        this.values = values;
    }

    public <T extends ConstraintVisitor> T accept(T visitor) {
        visitor.visitNotIn(this);
        return visitor;
    }

    public Field getField() {
        return field;
    }

    public Set getValues() {
        return values;
    }
}
