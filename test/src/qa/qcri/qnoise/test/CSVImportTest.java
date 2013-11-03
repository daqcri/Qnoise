/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.sql.DBConfig;
import qa.qcri.qnoise.sql.DBTools;
import qa.qcri.qnoise.sql.SQLDialect;
import qa.qcri.qnoise.util.CSVTools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CSVImportTest {
    private DBConfig derbyConfig;

    @Before
    public void before() {
        derbyConfig =
            new DBConfig.Builder().
                dialect(SQLDialect.DERBYMEMORY).
                url("localhost", "unittest").
                build();
    }

    @Test
    public void goodTest() {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            int size =
                CSVTools.load(
                    "test/src/qa/qcri/qnoise/test/input/dumptest.csv",
                    derbyConfig,
                    "DUMPTEST",
                    ','
                );
            Assert.assertTrue(size > 0);
            conn = DBTools.createConnection(derbyConfig, true);
            stat = conn.createStatement();

            rs = stat.executeQuery("SELECT * FROM DUMPTEST");
            int line = 0;
            while (rs.next()) {
                // TODO: do exact compare inside.
                line ++;
            }

            Assert.assertEquals(12, line);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }

                if (rs != null) {
                    rs.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {}
        }
    }

    /*
    public boolean isNumber(String s) {

        boolean isNumber = true;
        String ss = s.trim();
        if (ss.length() == 0)
            return false;
        char lastC = ss.charAt(ss.length() - 1);
        if (lastC == '.')
            ss = ss.substring(0, ss.length() - 1);
        if (ss.length() == 0)
            return false;
        lastC = ss.charAt(ss.length() - 1);

        if (lastC < '0' || lastC > '9')
            return false;
        try {
            Double.parseDouble(ss);
        } catch(Exception ex) {
            isNumber = false;
        }
        return isNumber;
    }

    @Test
    public void test2() {
        String[] ins = { "0..", ".", "0", "...", ".1." };
        boolean[] rs = { false, false, true, false, false };

        for (int i = 0; i < ins.length; i ++) {
            Assert.assertEquals(rs[i], isNumber(ins[i]));
        }
    }
    */
}
