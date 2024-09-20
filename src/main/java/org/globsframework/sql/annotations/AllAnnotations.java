package org.globsframework.sql.annotations;

import org.globsframework.core.metamodel.GlobModel;
import org.globsframework.core.metamodel.annotations.IsDate;
import org.globsframework.core.metamodel.annotations.IsDateTime;
import org.globsframework.core.metamodel.impl.DefaultGlobModel;

public class AllAnnotations {
    public final static GlobModel MODEL =
            new DefaultGlobModel(DbFieldName.TYPE, DbRef.TYPE, IsBigDecimal.TYPE, DbIndex.TYPE, IsDbKey.TYPE, TargetTypeName.TYPE,
                    DbFieldSqlType.TYPE, DbFieldIndex.TYPE, DbFieldIsNullable.TYPE, DbFieldMaxCharSize.TYPE, DbFieldNumericPrecision.TYPE,
                    DbFieldNumericDigit.TYPE, DbFieldMinCharSize.TYPE, IsTimestamp.TYPE, IsDate.TYPE, IsDateTime.TYPE);
}
