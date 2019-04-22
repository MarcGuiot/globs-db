package org.globsframework.sqlstreams.annotations;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.GlobTypeLoaderFactory;
import org.globsframework.metamodel.annotations.InitUniqueKey;
import org.globsframework.metamodel.fields.BooleanField;
import org.globsframework.metamodel.fields.IntegerField;
import org.globsframework.model.Key;
import org.globsframework.model.MutableGlob;

public class DbFieldIsNullable {
    public static GlobType TYPE;

    public static BooleanField IS_NULLABLE;

    @InitUniqueKey
    public static Key KEY;

    static {
        GlobTypeLoaderFactory.create(DbFieldIsNullable.class, "DbFieldIsNullable")
              .load();
    }

    public static MutableGlob create(boolean isNullable) {
        return TYPE.instantiate().set(IS_NULLABLE, isNullable);
    }

}
