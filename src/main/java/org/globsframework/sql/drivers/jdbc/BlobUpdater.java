package org.globsframework.sql.drivers.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BlobUpdater {

    void setBlob(PreparedStatement preparedStatement, int index, byte[] bytes) throws SQLException;

    int getBlobType();
}
