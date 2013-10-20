/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database common utility class.
 */
public class DBTools {

    /**
     * Gets the JDBC connection based on the dialect.
     * @param dbConfig dbconfig.
     * @param autoCommit auto commit flag.
     * @return JDBC connection.
     */
    public static Connection createConnection(DBConfig dbConfig, boolean autoCommit)
            throws
            ClassNotFoundException,
            SQLException,
            IllegalAccessException,
            InstantiationException {
        String driverName = SQLDialectTools.getDriverName(dbConfig.getDialect());
        Class.forName(driverName).newInstance();
        Connection conn =
            DriverManager.getConnection(
                dbConfig.getUrl(),
                dbConfig.getUserName(),
                dbConfig.getPassword()
            );
        conn.setAutoCommit(autoCommit);
        return conn;
    }

    /**
     * Returns <code>True</code> when the given table exists in the connection.
     * @param tableName table name.
     * @return <code>True</code> when the given table exists in the connection.
     */
    public static boolean existTable(DBConfig dbConfig, String tableName)
            throws
            SQLException,
            IllegalAccessException,
            InstantiationException,
            ClassNotFoundException {
        Connection conn = null;
        try {
            conn = createConnection(dbConfig, true);
            DatabaseMetaData meta = conn.getMetaData();
            boolean result;
            SQLDialect dialect = dbConfig.getDialect();
            if (dialect == SQLDialect.DERBYMEMORY || dialect == SQLDialect.DERBY) {
                String username = dbConfig.getUserName().toUpperCase();
                result =
                    meta.getTables(null, username, tableName.toUpperCase(), null).next() ||
                    meta.getTables(null, username, tableName.toLowerCase(), null).next();
            } else {
                result =
                    meta.getTables(null, null, tableName.toUpperCase(), null).next() ||
                    meta.getTables(null, null, tableName.toLowerCase(), null).next();
            }
            return result;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
