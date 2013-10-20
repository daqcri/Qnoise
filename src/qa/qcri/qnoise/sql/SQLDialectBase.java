/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.sql;

/**
 * Base class for supporting cross-vendor DB operations.
 */
public abstract class SQLDialectBase {

    /**
     * Creates SQLDialect instance.
     * @param dialect dialect.
     * @return SQLDialectBase instance.
     */
    public static SQLDialectBase createDialectBaseInstance(SQLDialect dialect) {
        SQLDialectBase dialectInstance;
        switch (dialect) {
            default:
            case DERBYMEMORY:
            case DERBY:
                dialectInstance = new DerbySQLDialect();
                break;
        }
        return dialectInstance;
    }

    /**
     * Returns True when bulk loading is supported.
     * @return True when bulk loading is supported.
     */
    public boolean supportBulkLoad() {
        return false;
    }

    /**
     * Bulk load CSV file.
     * @param dbConfig {@link DBConfig}.
     * @param tableName table name.
     * @param file CSV file.
     * @param delimiter CSV delimiter.
     * @return bytes loaded in the target table.
     */
    public int bulkLoad(
        DBConfig dbConfig,
        String tableName,
        String file,
        char delimiter
    ) throws Exception {
        throw new UnsupportedOperationException("Method is not implemented.");
    }
}
