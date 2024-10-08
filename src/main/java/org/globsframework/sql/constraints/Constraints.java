package org.globsframework.sql.constraints;

import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.FieldValues;
import org.globsframework.core.streams.accessors.*;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;
import org.globsframework.sql.constraints.impl.*;

import java.util.Set;

public class Constraints {
    private Constraints() {
    }

    public static Constraint keyEquals(final KeyConstraint keyAccessor) {
        Field[] list = keyAccessor.getGlobType().getKeyFields();
        Constraint constraint = null;
        for (final Field field : list) {
            constraint = Constraints.and(constraint, Constraints.equalsObject(field,
                    new KeyElementAccessor(keyAccessor, field)));
        }
        return constraint;
    }

    public static Constraint fieldsEqual(FieldValues values) {
        try {
            ConstraintsFunctor functor = new ConstraintsFunctor(values);
            values.apply(functor);
            return functor.getConstraint();
        } catch (Exception e) {
            throw new UnexpectedApplicationState(e);
        }
    }

    /**
     * We use different name to help the IDE giving us the good completion
     */

    public static Constraint equalsObject(Field field, Object value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equalsObject(Field field, Accessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint fieldEqual(Field field1, Field field2) {
        return new EqualConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint fieldNotEqual(Field field1, Field field2) {
        return new NotEqualConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint equal(IntegerField field, Integer value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equal(LongField field, Long value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equal(DoubleField field, DoubleAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint equal(StringField field, StringAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint equal(StringField field, String value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equal(StringArrayField field, StringArrayAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint equal(StringArrayField field, String[] value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equal(LongArrayField field, LongArrayAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint equal(LongArrayField field, long[] value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint equal(IntegerField field, IntegerAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint equal(LongField field, LongAccessor accessor) {
        return new EqualConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint greaterUnchecked(Field field, Object value) {
        return new BiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint greater(Field field1, Field field2) {
        return new BiggerThanConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint greater(IntegerField field, Integer value) {
        return new BiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint greater(LongField field, Long value) {
        return new BiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint greaterUnchecked(Field field, Accessor accessor) {
        return new BiggerThanConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint lessUncheck(Field field, Object value) {
        return new LessThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint lessUnchecked(Field field1, Field field2) {
        return new LessThanConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint less(IntegerField field, Integer value) {
        return new LessThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint less(LongField field, Long value) {
        return new LessThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint lessUnchecked(Field field, Accessor accessor) {
        return new LessThanConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint strictlyGreater(Field field, Object value) {
        return new StrictlyBiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyGreater(Field field1, Field field2) {
        return new StrictlyBiggerThanConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint strictlyGreater(IntegerField field, Integer value) {
        return new StrictlyBiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyGreater(LongField field, Long value) {
        return new StrictlyBiggerThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyBigger(Field field, Accessor accessor) {
        return new StrictlyBiggerThanConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint strictlyLessUnchecked(Field field, Object value) {
        return new StrictlyLesserThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyLess(Field field1, Field field2) {
        return new StrictlyLesserThanConstraint(new FieldOperand(field1), new FieldOperand(field2));
    }

    public static Constraint strictlyLess(IntegerField field, Integer value) {
        return new StrictlyLesserThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyLess(LongField field, Long value) {
        return new StrictlyLesserThanConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint strictlyLess(Field field, Accessor accessor) {
        return new StrictlyLesserThanConstraint(new FieldOperand(field), new AccessorOperand(field, accessor));
    }

    public static Constraint and(Constraint arg1, Constraint arg2) {
        return AndConstraint.build(arg1, arg2);
    }

    public static Constraint and(Constraint arg1, Constraint arg2, Constraint arg3) {
        return and(arg1, and(arg2, arg3));
    }

    public static Constraint and(Constraint... args) {
        return AndConstraint.build(args);
    }

    public static Constraint or(Constraint arg1, Constraint arg2) {
        return OrConstraint.build(arg1, arg2);
    }

    public static Constraint or(Constraint... args) {
        return OrConstraint.build(args);
    }

    public static Constraint in(Field field, Set infos) {
        return new InConstraint(field, infos);
    }

    public static Constraint notEqual(StringField field, String value) {
        return new NotEqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint notEqualUncheck(Field field, Object value) {
        return new NotEqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    public static Constraint notIn(Field field, Set infos) {
        return new NotInConstraint(field, infos);
    }

    public static Constraint contains(StringField field, String value) {
        return new ContainsConstraint(field, value, false, true, false);
    }

    public static Constraint containsIgnoreCase(StringField field, String value) {
        return new ContainsConstraint(field, value, false, true, true);
    }

    public static Constraint notContains(StringField field, String value) {
        return new ContainsConstraint(field, value, false, false, false);
    }

    public static Constraint notContainsIgnoreCase(StringField field, String value) {
        return new ContainsConstraint(field, value, false, false, true);
    }

    public static Constraint startWith(StringField field, String value) {
        return new ContainsConstraint(field, value, true, true, false);
    }

    public static Constraint startWithIgnoreCase(StringField field, String value) {
        return new ContainsConstraint(field, value, true, true, true);
    }

    public static Constraint startWith(StringArrayField field, String[] value) {
        return new ContainsConstraint(field, String.join(",", value), true, true, false);
    }

    public static Constraint notStartWith(StringField field, String value) {
        return new ContainsConstraint(field, value, true, false, false);
    }

    public static Constraint notStartWithIgnoreCase(StringField field, String value) {
        return new ContainsConstraint(field, value, true, false, true);
    }

    public static Constraint regularExpressionCaseSensitive(StringField field, String value) {
        return new RegularExpressionConstraint(field, value, true, false);
    }

    public static Constraint regularExpressionCaseInsensitive(StringField field, String value) {
        return new RegularExpressionConstraint(field, value, false, false);
    }

    public static Constraint notRegularExpressionCaseSensitive(StringField field, String value) {
        return new RegularExpressionConstraint(field, value, true, true);
    }

    public static Constraint notRegularExpressionCaseInsensitive(StringField field, String value) {
        return new RegularExpressionConstraint(field, value, false, true);
    }

    public static Constraint isNull(Field field) {
        return new NullOrNotConstraint(field, true);
    }

    public static Constraint isNotNull(Field field) {
        return new NullOrNotConstraint(field, false);
    }

    public static Constraint equal(BooleanField field, boolean value) {
        return new EqualConstraint(new FieldOperand(field), new ValueOperand(field, value));
    }

    private static class ConstraintsFunctor implements FieldValues.Functor {
        private Constraint constraint = null;
        private final FieldValues key;

        public ConstraintsFunctor(FieldValues key) {
            this.key = key;
        }

        public void process(final Field field, Object value) throws Exception {
            constraint = Constraints.and(constraint, Constraints.equalsObject(field, new Accessor() {
                public Object getObjectValue() {
                    return key.getValue(field);
                }
            }));
        }

        public Constraint getConstraint() {
            return constraint;
        }
    }

    private static class KeyElementAccessor implements Accessor {
        private final KeyConstraint keyAccessor;
        private final Field field;

        public KeyElementAccessor(KeyConstraint keyAccessor, Field field) {
            this.keyAccessor = keyAccessor;
            this.field = field;
        }

        public Object getObjectValue() {
            return keyAccessor.getValue(field);
        }
    }
}
