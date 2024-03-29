package org.globsframework.sqlstreams.annotations.typed;

import org.globsframework.metamodel.GlobType;
import org.globsframework.sqlstreams.annotations.DbFieldIsNullable;
import org.globsframework.sqlstreams.annotations.IsBigDecimal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD})
public @interface TypedIsNullable {
    GlobType TYPE = DbFieldIsNullable.TYPE;

}
