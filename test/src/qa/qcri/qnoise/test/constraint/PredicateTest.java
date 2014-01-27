/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.constraint;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.constraint.PredicateConstraint;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.test.TestSpecFactory;

import java.io.FileReader;
import java.util.List;

public class PredicateTest {
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
    public void validTest() {
        PredicateConstraint predicate = new PredicateConstraint().parse("ID >= 4");
        for (int i = 4; i < profile.getLength(); i ++) {
            Assert.assertTrue(predicate.isValid(profile, i));
        }

        for (int i = 0; i < 4; i ++) {
            Assert.assertEquals(false, predicate.isValid(profile, i));
        }
    }

    @Test
    public void messTest() {
        NoiseReport report = new NoiseReport(TestSpecFactory.createDummySpec());
        PredicateConstraint predicate = new PredicateConstraint().parse("D >= 50");
        int index = 0;
        for (int i = 0; i < profile.getLength(); i ++) {
            if (predicate.isValid(profile, i)) {
                index = i;
                break;
            }
        }

        predicate.messIt(profile, index, 0, report);
        double val = Double.parseDouble(profile.getCell(index, 4));
        Assert.assertEquals(true, val < 50.0);
    }
}
