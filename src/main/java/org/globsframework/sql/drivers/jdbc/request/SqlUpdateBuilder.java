package org.globsframework.sql.drivers.jdbc.request;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.*;
import org.globsframework.core.model.Glob;
import org.globsframework.core.streams.accessors.*;
import org.globsframework.core.streams.accessors.utils.*;
import org.globsframework.sql.BulkDbRequest;
import org.globsframework.sql.SqlRequest;
import org.globsframework.sql.SqlService;
import org.globsframework.sql.UpdateBuilder;
import org.globsframework.sql.constraints.Constraint;
import org.globsframework.sql.drivers.jdbc.BlobUpdater;
import org.globsframework.sql.drivers.jdbc.SqlUpdateRequest;
import org.globsframework.sql.exceptions.SqlException;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class SqlUpdateBuilder implements UpdateBuilder {
    private Map<Field, Accessor> values = new HashMap<Field, Accessor>();
    private Connection connection;
    private GlobType globType;
    private SqlService sqlService;
    private Constraint constraint;
    private BlobUpdater blobUpdater;

    public SqlUpdateBuilder(Connection connection, GlobType globType, SqlService sqlService,
                            Constraint constraint, BlobUpdater blobUpdater) {
        this.blobUpdater = blobUpdater;
        this.connection = connection;
        this.globType = globType;
        this.sqlService = sqlService;
        this.constraint = constraint;
    }

    public UpdateBuilder updateUntyped(Field field, final Object value) {
        field.safeAccept(new FieldVisitor.AbstractWithErrorVisitor() {
            public void visitInteger(IntegerField field) {
                update(field, (Integer) value);
            }

            public void visitLong(LongField field) {
                update(field, (Long) value);
            }

            public void visitDouble(DoubleField field) {
                update(field, (Double) value);
            }

            public void visitString(StringField field) {
                update(field, (String) value);
            }

            public void visitBoolean(BooleanField field) {
                update(field, (Boolean) value);
            }

            public void visitBlob(BlobField field) {
                update(field, (byte[]) value);
            }

            public void visitGlob(GlobField field) {
                update(field, (Glob) value);
            }

            public void visitGlobArray(GlobArrayField field) {
                update(field, (Glob[]) value);
            }

            public void visitDate(DateField field) throws Exception {
                update(field, (LocalDate) value);
            }

            public void visitDateTime(DateTimeField field) throws Exception {
                update(field, (ZonedDateTime) value);
            }
        });
        return this;
    }

    public UpdateBuilder updateUntyped(Field field, Accessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(IntegerField field, IntegerAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(IntegerField field, Integer value) {
        return update(field, new ValueIntegerAccessor(value));
    }

    public UpdateBuilder update(LongField field, LongAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(LongField field, Long value) {
        return update(field, new ValueLongAccessor(value));
    }

    public UpdateBuilder update(DoubleField field, DoubleAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(DoubleField field, Double value) {
        return update(field, new ValueDoubleAccessor(value));
    }

    public UpdateBuilder update(StringField field, StringAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(StringArrayField field, StringArrayAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(StringField field, String value) {
        return update(field, new ValueStringAccessor(value));
    }

    public UpdateBuilder update(DateTimeField field, ZonedDateTime value) {
        return update(field, new ValueDateTimeAccessor(value));
    }

    public UpdateBuilder update(DateField field, LocalDate value) {
        return update(field, new ValueDateAccessor(value));
    }

    public UpdateBuilder update(DateTimeField field, DateTimeAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(DateField field, DateAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(StringArrayField field, String[] value) {
        return update(field, new ValueStringArrayAccessor(value));
    }

    public UpdateBuilder update(BooleanField field, BooleanAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(BooleanField field, Boolean value) {
        return update(field, new ValueBooleanAccessor(value));
    }

    public UpdateBuilder update(BlobField field, byte[] value) {
        return update(field, new ValueBlobAccessor(value));
    }

    public UpdateBuilder update(BlobField field, BlobAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(GlobField field, GlobAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(GlobArrayField field, GlobsAccessor accessor) {
        values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(GlobField field, Glob value) {
        values.put(field, new ValueGlobAccessor(value));
        return this;
    }

    public UpdateBuilder update(GlobArrayField field, Glob[] values) {
        this.values.put(field, new ValueGlobsAccessor(values));
        return this;
    }

    public UpdateBuilder update(LongArrayField field, LongArrayAccessor accessor) {
        this.values.put(field, accessor);
        return this;
    }

    public UpdateBuilder update(LongArrayField field, long[] values) {
        this.values.put(field, new ValueLongArrayAccessor(values));
        return this;
    }

    public SqlRequest getRequest() {
        try {
            if (this.values.isEmpty()) {
                return new SqlRequest() {
                    public int run() throws SqlException {
                        return 0;
                    }

                    public void close() {
                    }
                };
            }
            return new SqlUpdateRequest(globType, constraint, values, connection, sqlService, blobUpdater);
        } finally {
            values.clear();
        }
    }

    public BulkDbRequest getBulkRequest() {
        SqlRequest request = getRequest();
        return new BulkDbRequest() {
            public void flush() {
            }

            public int run() throws SqlException {
                return request.run();
            }

            public void close() {
                request.close();
            }
        };
    }
}
