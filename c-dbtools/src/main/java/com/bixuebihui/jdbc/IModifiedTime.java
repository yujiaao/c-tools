package com.bixuebihui.jdbc;

import java.sql.Timestamp;

/**
 * Used when want automatic update object's modified time to current time
 */
public interface IModifiedTime {
    void setModifiedTime(Timestamp time);
}
