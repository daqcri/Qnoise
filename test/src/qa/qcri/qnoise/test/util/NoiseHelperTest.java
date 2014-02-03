/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.util;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.test.TestSpecFactory;
import qa.qcri.qnoise.util.NoiseHelper;

import java.io.FileReader;
import java.util.List;

public class NoiseHelperTest {
    private DataProfile profile;

    @Before
    public void startup() {
        try {
            CSVReader reader = new CSVReader(new FileReader(TestDataRepository.DUMPTEST));
            List<String> schema = Lists.newArrayList();
            schema.add("Numerical");
            schema.add("Text");
            schema.add("Text");
            schema.add("Text");
            schema.add("Numerical");
            profile = DataProfile.readData(reader, schema);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testNumerical() {
        NoiseReport report = new NoiseReport(TestSpecFactory.createDummySpec());
        double oldVal = Double.parseDouble(profile.getCell(0, 4));
        double std = profile.getStandardDeviationOn("D");
        NoiseHelper.playTheJazz(0.5, "D", profile, 0, report);
        double newVal = Double.parseDouble(profile.getCell(0, 4));
        double diff = Math.abs(newVal - oldVal);
        Assert.assertTrue(diff < 0.5 * std + 0.001f);
        profile.setDirty();

        oldVal = Double.parseDouble(profile.getCell(10, 4));
        std = profile.getStandardDeviationOn("D");
        NoiseHelper.playTheJazz(0.9, "D", profile, 10, report);
        newVal = Double.parseDouble(profile.getCell(10, 4));
        diff = Math.abs(newVal - oldVal);
        Assert.assertTrue(diff < 0.9 * std + 0.001f);
        profile.setDirty();

        oldVal = Double.parseDouble(profile.getCell(11, 4));
        NoiseHelper.playTheJazz(0.0, "D", profile, 11, report);
        newVal = Double.parseDouble(profile.getCell(11, 4));
        diff = Math.abs(newVal - oldVal);
        Assert.assertTrue(diff == 0);
    }

    @Test
    public void testTextDistance() {
        try {
            NoiseReport report = new NoiseReport(TestSpecFactory.createDummySpec());
            String oldVal = profile.getCell(0, 1);
            NoiseHelper.playTheJazz(0.5, "C", profile, 0, report);
            String newVal = profile.getCell(0, 1);
            Assert.assertEquals(1, calcDist(oldVal, newVal));

            oldVal = profile.getCell(7, 2);
            NoiseHelper.playTheJazz(1.0, "A", profile, 7, report);
            newVal = profile.getCell(7, 2);
            Assert.assertEquals(2, calcDist(oldVal, newVal));

            oldVal = profile.getCell(7, 3);
            NoiseHelper.playTheJazz(1.0, "B", profile, 7, report);
            newVal = profile.getCell(7, 3);
            Assert.assertEquals(2, calcDist(oldVal, newVal));

            oldVal = profile.getCell(7, 1);
            NoiseHelper.playTheJazz(0.1, "C", profile, 7, report);
            newVal = profile.getCell(7, 1);
            Assert.assertEquals(0, calcDist(oldVal, newVal));
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    private int calcDist(String oldVal, String newVal) {
        char oc, nc;
        int count = 0;
        for (int i = 0; i < oldVal.length(); i ++) {
            oc = oldVal.charAt(i);
            nc = newVal.charAt(i);
            if (oc != nc) {
                count ++;
            }
        }
        return count;
    }
}
