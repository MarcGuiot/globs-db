package org.globsframework.sql.accessors;

import java.sql.ResultSet;

public interface GeneratedKeyAccessor {
    void setResult(ResultSet generatedKeys);
}
