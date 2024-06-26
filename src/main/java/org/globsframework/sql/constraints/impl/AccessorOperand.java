package org.globsframework.sql.constraints.impl;

import org.globsframework.metamodel.fields.Field;
import org.globsframework.sql.constraints.Operand;
import org.globsframework.sql.constraints.OperandVisitor;
import org.globsframework.streams.accessors.Accessor;

public class AccessorOperand implements Operand {
    private Field field;
    private Accessor accessor;

    public AccessorOperand(Field field, Accessor accessor) {
        this.field = field;
        this.accessor = accessor;
    }

    public <T extends OperandVisitor> T visitOperand(T visitor) {
        visitor.visitAccessorOperand(this);
        return visitor;
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public Field getField() {
        return field;
    }
}
