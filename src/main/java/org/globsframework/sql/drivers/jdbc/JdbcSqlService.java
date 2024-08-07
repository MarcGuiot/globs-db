package org.globsframework.sql.drivers.jdbc;

import org.globsframework.metamodel.GlobType;
import org.globsframework.metamodel.fields.Field;
import org.globsframework.sql.annotations.DbFieldName;
import org.globsframework.sql.annotations.TargetTypeName;
import org.globsframework.sql.drivers.hsqldb.HsqlConnection;
import org.globsframework.sql.drivers.mysql.MysqlConnection;
import org.globsframework.sql.drivers.postgresql.PostgresqlConnection;
import org.globsframework.sql.drivers.postgresql.ToPostgreCaseNamingMapping;
import org.globsframework.sql.utils.AbstractSqlService;
import org.globsframework.utils.exceptions.ItemNotFound;
import org.globsframework.utils.exceptions.UnexpectedApplicationState;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcSqlService extends AbstractSqlService {
    private static Map<String, Driver> loadedDrivers = new ConcurrentHashMap<>();
    private Driver driver;
    private String dbName;
    private Properties dbInfo;
    private DbFactory dbFactory;
    private NamingMapping namingMapping;

    public JdbcSqlService(String dbName, String user, String password, NamingMapping namingMapping) {
        this.dbName = dbName;
        this.namingMapping = namingMapping;
        dbInfo = new Properties();
        dbInfo.put("user", user);
        dbInfo.put("password", password);
        loadDriver();
    }

    public JdbcSqlService(String dbName, String user, String password) {
        this(dbName, user, password, DefaultNamingMapping.INSTANCE);
    }

    public interface NamingMapping {
        default String getTableName(GlobType globType) {
            return getTableName(TargetTypeName.getOptName(globType).orElse(globType.getName()));
        }

        String getTableName(String typeName);

        default String getColumnName(Field field) {
            return getColumnName(DbFieldName.getOptName(field).orElse(field.getName()));
        }

        String getColumnName(String fieldName);

        default String getLikeIgnoreCase() {
            return null;
        }
    }

    interface DbFactory {
        JdbcConnection create(boolean autoCommit);
    }

    public NamingMapping getNamingMapping() {
        return namingMapping;
    }

    public String getTableName(GlobType globType) {
        return namingMapping.getTableName(globType);
    }

    public String getTableName(String name) {
        return namingMapping.getTableName(name);
    }

    public String getColumnName(String field) {
        return namingMapping.getColumnName(field);
    }

    public String getLikeIgnoreCase() {
        return namingMapping.getLikeIgnoreCase();
    }

    public String getColumnName(Field field) {
        return namingMapping.getColumnName(field);
    }

    private void loadDriver() {
        try {
            if (dbName.contains("hsqldb")) {
                setupHsqldb();
            } else if (dbName.contains("mysql")) {
                setupMySql();
            } else if (dbName.startsWith("jdbc:mariadb:")) {
                setupMariaDb();
            } else if (dbName.startsWith("jdbc:postgresql:")) {
                setupPostgresql();
            }
        } catch (Exception e) {
            throw new ItemNotFound(e);
        }
    }

    private void setupPostgresql() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        driver = loadedDrivers.get(dbName);
        if (driver == null) {
            driver = (Driver) Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            loadedDrivers.put(dbName, driver);
        }
        namingMapping = new ToPostgreCaseNamingMapping(namingMapping) {
            public String getLikeIgnoreCase() {
                return "iLike";
            }
        };
        dbFactory = new DbFactory() {
            public JdbcConnection create(boolean autoCommit) {
                Connection connection = getConnection();
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new UnexpectedApplicationState(e);
                }
                return new PostgresqlConnection(autoCommit, connection, JdbcSqlService.this);
            }
        };
    }

    private void setupMariaDb() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        driver = loadedDrivers.get(dbName);
        if (driver == null) {
            driver = (Driver) Class.forName("org.mariadb.jdbc.Driver").getDeclaredConstructor().newInstance();
            loadedDrivers.put(dbName, driver);
        }
        dbInfo.put("zeroDateTimeBehavior", "convertToNull");
        dbFactory = new DbFactory() {
            public JdbcConnection create(boolean autoCommit) {
                Connection connection = getConnection();
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new UnexpectedApplicationState(e);
                }

                return new MysqlConnection(autoCommit, connection, JdbcSqlService.this);
            }
        };
    }

    private void setupMySql() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        driver = loadedDrivers.get(dbName);
        if (driver == null) {
            driver = (Driver) Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            loadedDrivers.put(dbName, driver);
        }
        dbInfo.put("zeroDateTimeBehavior", "convertToNull");
        dbFactory = new DbFactory() {
            public JdbcConnection create(boolean autoCommit) {
                Connection connection = getConnection();
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new UnexpectedApplicationState(e);
                }

                return new MysqlConnection(autoCommit, connection, JdbcSqlService.this);
            }
        };
    }

    private void setupHsqldb() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        driver = loadedDrivers.get(dbName);
        if (driver == null) {
            driver = (Driver) Class.forName("org.hsqldb.jdbcDriver").getDeclaredConstructor().newInstance();
            loadedDrivers.put(dbName, driver);
        }
        if (namingMapping == DefaultNamingMapping.INSTANCE) {
            namingMapping = new NamingMapping() {
                public String getTableName(String typeName) {
                    return toSqlName(typeName);
                }

                public String getColumnName(String fieldName) {
                    return toSqlName(fieldName);
                }
            };
        }
        dbFactory = new DbFactory() {
            public JdbcConnection create(boolean autoCommit) {
                Connection connection = getConnection();
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new UnexpectedApplicationState(e);
                }
                return new HsqlConnection(autoCommit, connection, JdbcSqlService.this);
            }
        };
    }

    public JdbcConnection getDb() {
        return dbFactory.create(false);
    }

    public JdbcConnection getAutoCommitDb() {
        return dbFactory.create(true);
    }

    synchronized public Connection getConnection() {
        try {
            return driver.connect(dbName, dbInfo);
        } catch (SQLException e) {
            throw new UnexpectedApplicationState("for " + dbInfo.get("user") + " on " + dbName, e);
        }
    }

    private static class DefaultNamingMapping implements NamingMapping {
        public static NamingMapping INSTANCE = new DefaultNamingMapping();

        public String getTableName(String typeName) {
            return typeName;
        }

        public String getColumnName(String fieldName) {
            return fieldName;
        }
    }

}
