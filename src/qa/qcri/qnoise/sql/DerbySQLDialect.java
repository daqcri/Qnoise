/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.sql;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Derby SQL dialect base.
 */
public class DerbySQLDialect extends SQLDialectBase {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportBulkLoad() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int bulkLoad(
        DBConfig dbConfig,
        String tableName,
        String file,
        char delimiter
    ) throws Exception {
        Path inputFile = Paths.get(file);
        int size = 0;
        // copy to a temp file.
        Path outputFile = Files.createTempFile(inputFile.toFile().getName(), null);
        Files.copy(inputFile, outputFile, StandardCopyOption.REPLACE_EXISTING);

        size = removeFirstLine(outputFile);
        inputFile = outputFile;

        Connection conn = null;
        Statement stat = null;
        try {
            String schema = dbConfig.getUserName().toUpperCase();
            String table = tableName.toUpperCase();
            String filePath = inputFile.toFile().getAbsolutePath();
            String sql =
                String.format(
                    "call SYSCS_UTIL.SYSCS_IMPORT_DATA_LOBS_FROM_EXTFILE " +
                    "('%s', '%s', null, null, '%s', '%s', null, null, 0)",
                    schema,
                    table,
                    filePath,
                    delimiter
                );
            conn = DBTools.createConnection(dbConfig, true);
            stat = conn.createStatement();
            stat.execute(sql);
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }

                if (conn != null) {
                    conn.close();
                }

                // remove the temporary file
                Files.delete(inputFile);
            } catch (Exception ex) {}
        }
        return size;
    }

    private static int removeFirstLine(Path file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw");
        //Initial write position
        long writePosition = raf.getFilePointer();
        raf.readLine();
        // Shift the next lines upwards.
        long readPosition = raf.getFilePointer();

        byte[] buff = new byte[40960];
        int n;
        int size = 0;
        while (-1 != (n = raf.read(buff))) {
            size += n;
            raf.seek(writePosition);
            raf.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            raf.seek(readPosition);
        }

        // TODO: deal with non-eol?
        // String eol = System.lineSeparator();
        // raf.writeChars(eol);
        raf.setLength(writePosition);
        raf.close();
        return size;
    }
}
