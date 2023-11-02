package org.jorm.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The Database class provides a centralized connection to the database using JDBC.
 * It ensures that only one connection is created and reused throughout the application.
 */
public class database {
    private static volatile Connection connection = null;

    private database() {
    }

    /**
     * Initializes the database connection.
     */
    public static void init() {
        if (connection == null) {
            synchronized (database.class) {
                if (connection == null) {
                    try {
                        Class.forName(environment.get("JDBC_DRIVER"));
                        connection = DriverManager.getConnection(
                                environment.get("DB_URL"),
                                environment.get("DB_USERNAME"),
                                environment.get("DB_PASSWORD")
                        );
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static {
        init();
    }

    /**
     * Closes the database connection.
     *
     * @return The closed connection, or null if an error occurs during the closing process.
     */
    public static Connection closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                return connection;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Retrieves the database connection. If the connection is null, it initializes it first.
     *
     * @return The database connection.
     */
    public static Connection getConnection() {
        if (connection == null) {
            init();
        }
        return connection;
    }
}