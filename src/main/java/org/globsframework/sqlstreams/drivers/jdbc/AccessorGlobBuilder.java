package org.globsframework.sqlstreams.drivers.jdbc;

import org.globsframework.metamodel.Field;
import org.globsframework.metamodel.GlobType;
import org.globsframework.model.Glob;
import org.globsframework.model.MutableGlob;
import org.globsframework.streams.DbStream;
import org.globsframework.streams.accessors.Accessor;
import org.globsframework.utils.Check;

import java.util.Collection;

public class AccessorGlobBuilder {
    private Field[] fields;
    private Accessor[] accessors;
    private GlobType globType = null;

    private AccessorGlobBuilder(DbStream dbStream, GlobType fallBackType) {
        this(dbStream.getFields(), dbStream::getAccessor, fallBackType);
    }

    private AccessorGlobBuilder(Collection<Field> fields, FieldAccessor fieldAccessor, GlobType fallBackType) {
        this.fields = new Field[fields.size()];
        this.accessors = new Accessor[fields.size()];
        int i = 0;
        for (Field field : fields) {
            GlobType type = field.getGlobType();
            if (globType != null && type != globType) {
                throw new RuntimeException("Different globType in same stream " + type.getName() + " and " + globType.getName());
            }
            globType = type;
            this.fields[i] = field;
            this.accessors[i] = Check.requireNonNull(fieldAccessor.get(field), field);
            ++i;
        }
        if (globType == null) {
            globType = fallBackType;
        }
    }

    public interface FieldAccessor {
        Accessor get(Field field);
    }

    public static AccessorGlobBuilder init(Collection<Field> fields, FieldAccessor fieldAccessor, GlobType fallBackType) {
        return new AccessorGlobBuilder(fields, fieldAccessor, fallBackType);
    }

    public static AccessorGlobBuilder init(DbStream dbStream, GlobType fallBackType) {
        return new AccessorGlobBuilder(dbStream, fallBackType);
    }

    public Glob getGlob() {
        MutableGlob mutableGlob = globType.instantiate();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Accessor accessor = accessors[i];
            mutableGlob.setValue(field, accessor.getObjectValue());
        }
        return mutableGlob;
    }
}
