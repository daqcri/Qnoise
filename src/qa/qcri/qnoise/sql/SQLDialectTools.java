/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.sql;

/**
 * Utility class for cross vendor Database calling.
 */
public abstract class SQLDialectTools {
    /**
     * Builds up JDBC connection url.
     * @param hostname hostname.
     * @param databaseName database name.
     * @param dialect sql dialect.
     * @return JDBC connection url string.
     */
    public static String buildJdbcUrl(String hostname, String databaseName, SQLDialect dialect) {
        StringBuilder jdbcUrl = new StringBuilder("jdbc:");
        switch (dialect) {
            default:
            case DERBY:
                jdbcUrl.append("derby://").append(hostname).append('/').append(databaseName);
                break;
            case DERBYMEMORY:
                jdbcUrl.append("derby:memory:").append(databaseName).append(";create=true");
                break;
            case POSTGRES:
                jdbcUrl.append("postgresql://").append(hostname).append('/').append(databaseName);
                break;
            case MYSQL:
                jdbcUrl.append("mysql://").append(hostname).append('/').append(databaseName);
                break;
        }

        return jdbcUrl.toString();
    }

    /**
     * Gets the {@link SQLDialect} from a string.
     * @param type type string.
     * @return sql dialect.
     */
    public static SQLDialect getSQLDialect(String type) {
        SQLDialect result;
        switch (type) {
            default:
            case "derby":
                result = SQLDialect.DERBY;
                break;
            case "postgres":
                result = SQLDialect.POSTGRES;
                break;
            case "mysql":
                result = SQLDialect.MYSQL;
                break;
            case "derbymemory":
                result = SQLDialect.DERBYMEMORY;
        }
        return result;
    }

    public static String getDriverName(SQLDialect dialect) {
        switch (dialect) {
            case POSTGRES:
                return "org.postgresql.Driver";
            case DERBYMEMORY:
                return "org.apache.derby.jdbc.EmbeddedDriver";
            case DERBY:
                return "org.apache.derby.jdbc.ClientDriver";
            case MYSQL:
                return "com.mysql.jdbc.Driver";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
