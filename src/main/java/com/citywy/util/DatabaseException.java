package com.citywy.util;

public final class DatabaseException extends RuntimeException {
    public DatabaseException(String operation, java.sql.SQLException cause) {
        super("Database " + operation + " failed", cause);
    }
}
