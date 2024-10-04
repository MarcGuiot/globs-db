package org.globsframework.sql.drivers.jdbc;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.streams.accessors.Accessor;
import org.globsframework.core.utils.collections.Pair;
import org.globsframework.core.utils.exceptions.UnexpectedApplicationState;
import org.globsframework.json.GSonUtils;
import org.globsframework.sql.SqlRequest;
import org.globsframework.sql.SqlService;
import org.globsframework.sql.drivers.jdbc.impl.SqlValueFieldVisitor;
import org.globsframework.sql.drivers.jdbc.request.GeneratedKeyAccessor;
import org.globsframework.sql.utils.PrettyWriter;
import org.globsframework.sql.utils.StringPrettyWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SqlCreateRequest implements SqlRequest {
    static private final Logger LOGGER = LoggerFactory.getLogger(SqlCreateRequest.class);
    private PreparedStatement preparedStatement;
    private List<Pair<Field, Accessor>> fields;
    private SqlValueFieldVisitor sqlValueVisitor;
    private GeneratedKeyAccessor generatedKeyAccessor;
    private GlobType globType;
    private SqlService sqlService;
    private JdbcConnection jdbcConnection;

    public SqlCreateRequest(List<Pair<Field, Accessor>> fields, GeneratedKeyAccessor generatedKeyAccessor,
                            Connection connection,
                            GlobType globType, SqlService sqlService, BlobUpdater blobUpdater, JdbcConnection jdbcConnection) {
        this.generatedKeyAccessor = generatedKeyAccessor;
        this.fields = fields;
        this.globType = globType;
        this.sqlService = sqlService;
        this.jdbcConnection = jdbcConnection;
        String sql = prepareRequest(fields, this.globType, new Value() {
            public String get(Pair<Field, Accessor> pair) {
                return "?";
            }
        });
        try {
            if (generatedKeyAccessor != null) { // hsqlDb don"t like autogenerated keys
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(sql);

            }
        } catch (SQLException e) {
            throw new UnexpectedApplicationState("In prepareStatement for request : " + sql, e);
        }
        this.sqlValueVisitor = new SqlValueFieldVisitor(preparedStatement, blobUpdater);
    }

    private String prepareRequest(List<Pair<Field, Accessor>> fields, GlobType globType, Value value) {
        PrettyWriter writer = new StringPrettyWriter();
        writer.append("INSERT INTO ")
                .append(sqlService.getTableName(globType, true))
                .append(" (");
        int columnCount = 0;
        for (Pair<Field, Accessor> pair : fields) {
            String columnName = sqlService.getColumnName(pair.getFirst(), true);
            writer.appendIf(", ", columnCount > 0);
            columnCount++;
            writer.append(columnName);
        }
        writer.append(") VALUES (");
        for (Iterator<Pair<Field, Accessor>> it = fields.iterator(); it.hasNext(); ) {
            Pair<Field, Accessor> pair = it.next();
            writer.append(value.get(pair)).appendIf(",", it.hasNext());
        }
        writer.append(")");
        return writer.toString();
    }

    public int run() {
        try {
            int index = 0;
            for (Pair<Field, Accessor> pair : fields) {
                Object value = pair.getSecond().getObjectValue();
                sqlValueVisitor.setValue(value, ++index);
                pair.getFirst().safeAccept(sqlValueVisitor);
            }
            final int result = preparedStatement.executeUpdate();
            if (generatedKeyAccessor != null) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedKeyAccessor.setResult(generatedKeys, sqlService);
                } else {
                    generatedKeyAccessor.reset();
                }
            }
            return result;
        } catch (SQLException e) {
            String debugRequest = getDebugRequest();
            LOGGER.error("In run " + debugRequest, e);
            throw jdbcConnection.getTypedException(debugRequest, e);
        }
    }

    public void close() {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error("In close", e);
            throw new UnexpectedApplicationState("In close", e);
        }
    }

    private String getDebugRequest() {
        return prepareRequest(fields, globType, new DebugValue());
    }

    interface Value {
        String get(Pair<Field, Accessor> pair);
    }

    private static class DebugValue extends FieldVisitor.AbstractWithErrorVisitor implements Value {
        private Object value;
        private String convertValue;

        public String get(Pair<Field, Accessor> pair) {
            value = pair.getSecond().getObjectValue();
            if (value != null) {
                pair.getFirst().safeAccept(this);
            } else {
                convertValue = "'NULL'";
            }
            return convertValue;
        }

        public void visitInteger(IntegerField field) {
            convertValue = value.toString();
        }

        public void visitDate(DateField field) throws Exception {
            convertValue = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
        }

        public void visitDateTime(DateTimeField field) throws Exception {
            convertValue = DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime) value);
        }

        public void visitDouble(DoubleField field) {
            convertValue = value.toString();
        }

        public void visitString(StringField field) {
            convertValue = "'" + value.toString() + "'";
        }

        public void visitBoolean(BooleanField field) {
            convertValue = value.toString();
        }

        public void visitBlob(BlobField field) {
            convertValue = "'" + value.toString() + "'";
        }

        public void visitLong(LongField field) {
            convertValue = value.toString();
        }


        public void visitGlob(GlobField field) {
            convertValue = GSonUtils.encode((Glob) value, true);
        }

        public void visitGlobArray(GlobArrayField field) {
            convertValue = GSonUtils.encode((Glob[]) value, true);
        }

        public void visitUnionGlob(GlobUnionField field) throws Exception {
            convertValue = GSonUtils.encode((Glob) value, true);
        }

        public void visitUnionGlobArray(GlobArrayUnionField field) throws Exception {
            convertValue = GSonUtils.encode((Glob[]) value, true);
        }

        public void visitStringArray(StringArrayField field) throws Exception {
            convertValue = String.join(",", (String[]) value);
        }

        public void visitLongArray(LongArrayField field) throws Exception {
            convertValue = Arrays.stream((long[]) value).mapToObj(Long::toString).collect(Collectors.joining(","));
        }
    }

    public String toString() {
        return getDebugRequest();
    }
}