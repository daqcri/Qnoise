/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.sql;

import com.google.common.base.Preconditions;

/**
 * Configuration object for JDBC connection.
 */
public class DBConfig {
    private String userName;
    private String password;
    private SQLDialect dialect;
    private String hostname;
    private String databaseName;

    //<editor-fold desc="Builder pattern">
    /**
     * Builder pattern to build a <code>DBConfig</code> class.
     */
    public static class Builder {
        private String userName = "APP";
        private String password;
        private SQLDialect dialect = SQLDialect.DERBY;
        private String hostname;
        private String databaseName;

        public Builder username(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder url(String hostname, String dbname) {
            this.hostname = Preconditions.checkNotNull(hostname);
            this.databaseName = Preconditions.checkNotNull(dbname);
            return this;
        }

        public Builder dialect(SQLDialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public DBConfig build() {
            return new DBConfig(userName, password, hostname, databaseName, dialect);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Constructor">

    /**
     * DBConfig copy constructor.
     * @param config config.
     */
    public DBConfig(DBConfig config) {
        Preconditions.checkNotNull(config);
        this.userName = config.userName;
        this.password = config.password;
        this.hostname = config.hostname;
        this.databaseName = config.databaseName;
        this.dialect = config.dialect;
    }

    /**
     * Constructor.
     * @param userName DB user name.
     * @param password DB password.
     * @param dialect SQL dialect.
     */
    private DBConfig(
        String userName,
        String password,
        String hostname,
        String databaseName,
        SQLDialect dialect
    ) {
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName;
        this.hostname = hostname;
        this.dialect = dialect;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">

    /**
     * Gets the user name.
     * @return user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the password.
     * @return password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the connection URL.
     * @return url.
     */
    public String getUrl() {
        return SQLDialectTools.buildJdbcUrl(hostname, databaseName, dialect);
    }

    /**
     * Gets the database name.
     * @return database name.
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Gets the host name.
     * @return host name.
     */
    public String getHostName() {
        return hostname;
    }

    /**
     * Gets the SQL dialect.
     * @return sql dialect.
     */
    public SQLDialect getDialect() {
        return dialect;
    }
    //</editor-fold>
}
