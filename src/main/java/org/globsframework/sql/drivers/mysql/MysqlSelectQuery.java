package org.globsframework.sql.drivers.mysql;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.sql.SqlService;
import org.globsframework.sql.accessors.SqlAccessor;
import org.globsframework.sql.constraints.Constraint;
import org.globsframework.sql.drivers.jdbc.BlobUpdater;
import org.globsframework.sql.drivers.jdbc.SqlOperation;
import org.globsframework.sql.drivers.jdbc.SqlSelectQuery;
import org.globsframework.sql.drivers.jdbc.impl.WhereClauseConstraintVisitor;
import org.globsframework.sql.drivers.jdbc.request.SqlQueryBuilder;
import org.globsframework.sql.drivers.mysql.impl.MysqlWhereClauseConstraintVisitor;
import org.globsframework.sql.utils.StringPrettyWriter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MysqlSelectQuery extends SqlSelectQuery {

    public MysqlSelectQuery(Connection connection, Constraint constraint, Map<Field, SqlAccessor> fieldToAccessorHolder, SqlService sqlService, BlobUpdater blobUpdater, boolean autoClose, List<SqlQueryBuilder.Order> orders, List<Field> groupBy, int top, int skip, Set<Field> distinct, List<SqlOperation> sqlOperations, GlobType fallBackType) {
        super(connection, constraint, fieldToAccessorHolder, sqlService, blobUpdater, autoClose, orders, groupBy, top, skip, distinct, sqlOperations, fallBackType);
    }

    protected WhereClauseConstraintVisitor getWhereConstraintVisitor(StringPrettyWriter where) {
        return new MysqlWhereClauseConstraintVisitor(where, sqlService, globTypes);
    }
}
