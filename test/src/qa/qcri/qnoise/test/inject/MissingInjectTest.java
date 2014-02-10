/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.inject;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.inject.MissingInjector;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseContextBuilder;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.util.OperationType;

import java.io.FileReader;
import java.util.List;

public class MissingInjectTest {
    private DataProfile profile;

    @Before
    public void startup() {
        try {
            CSVReader reader = new CSVReader(new FileReader(TestDataRepository.DUMPTEST));
            List<String> types = Lists.newArrayList();
            types.add("Numerical");
            types.add("Text");
            types.add("Text");
            types.add("Text");
            types.add("Numerical");
            profile = DataProfile.readData(reader, types);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void randomMissingTest() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Missing1.json");
            NoiseContext context = new NoiseContextBuilder().profile(profile).spec(spec).build();
            new MissingInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                    context.report.getLogBook();
            double perc = spec.percentage;
            int changedItem = (int)(perc * profile.getLength());
            for (Quartet<OperationType, Pair<Integer, Integer>, String, String> log : logBook) {
                Pair<Integer, Integer> index = log.getValue1();
                String value = profile.getCell(index.getValue0(), index.getValue1());
                Assert.assertEquals(null, value);
            }
            Assert.assertEquals(logBook.size(), changedItem);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void histogramMissingTest() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Missing2.json");
            NoiseContext context = new NoiseContextBuilder().profile(profile).spec(spec).build();
            new MissingInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                    context.report.getLogBook();
            double perc = spec.percentage;
            int changedItem = (int)(perc * profile.getLength());
            for (Quartet<OperationType, Pair<Integer, Integer>, String, String> log : logBook) {
                Pair<Integer, Integer> index = log.getValue1();
                String value = profile.getCell(index.getValue0(), index.getValue1());
                Assert.assertEquals(null, value);
            }
            Assert.assertEquals(logBook.size(), changedItem);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
