package org.globsframework.sql.metadata;

import org.globsframework.metamodel.GlobType;
import org.globsframework.sql.SqlConnection;
import org.globsframework.sql.SqlService;
import org.globsframework.sql.drivers.jdbc.JdbcConnection;
import org.globsframework.utils.exceptions.InvalidData;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbChecker {
    private final DatabaseMetaData metaData;
    private final SqlService sqlService;

    public DbChecker(SqlService sqlService, SqlConnection sqlConnection) {
        this.sqlService = sqlService;
        metaData = getMetaData(sqlConnection);
    }

    private static DatabaseMetaData getMetaData(SqlConnection sqlConnection) {
        try {
            return ((JdbcConnection) sqlConnection).getConnection().getMetaData();
        } catch (SQLException e) {
            throw new InvalidData(e);
        }
    }

    public boolean tableExists(GlobType globType) {
        try {
            String[] names = {"TABLE"};
            ResultSet tableNames = metaData.getTables(null, null, "%", names);
            String tableName = sqlService.getTableName(globType);
            while (tableNames.next()) {
                if (tableName.equalsIgnoreCase(tableNames.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new InvalidData(e);
        }
        return false;
    }
}
