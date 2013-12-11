/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.model;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.model.HistogramModel;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.test.TestDataRepository;

import java.io.FileReader;

public class HistogramModelTest {
    private DataProfile profile;

    @Before
    public void startup() {
        try {
            CSVReader reader = new CSVReader(new FileReader(TestDataRepository.DUMPTEST));
            profile = DataProfile.readData(reader, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }


    @Test
    public void testHist() {
        HistogramModel model = ModelFactory.createHistogramModel(profile, "A");
        Assert.assertEquals(0, model.nextIndex(0, 1));
        Assert.assertEquals(0, model.nextIndex(0, 4));
        Assert.assertEquals(4, model.nextIndex(4, 8));
        Assert.assertEquals(6, model.nextIndex(6, 8));

        for (int i = 0; i < 100; i ++) {
            int v = model.nextIndex(4, 11);
            Assert.assertTrue(v == 4 || v == 8);
        }
    }
}
