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
import qa.qcri.qnoise.inject.InconsistencyInjector;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseContextBuilder;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.util.NoiseJsonAdapter;
import qa.qcri.qnoise.util.OperationType;
import qa.qcri.qnoise.util.Tracer;

import java.io.FileReader;
import java.util.List;

public class InconsistencyTest {
    private DataProfile profile;

    @Before
    public void startup() {
        try {
            CSVReader reader = new CSVReader(new FileReader(TestDataRepository.PERSONALDATA));
            List<String> types = Lists.newArrayList();
            types.add("Text");
            types.add("Numerical");
            types.add("Text");
            profile = DataProfile.readData(reader, types);
            Tracer.setVerbose(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void inconsistencyTest1() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist1.json"
                );
            NoiseContext context =
                new NoiseContextBuilder().spec(spec).profile(profile).build();
            new InconsistencyInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                context.report.getLogBook();
            Assert.assertEquals(2, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void inconsistencyTest2() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist2.json"
                );
            NoiseContext context =
                new NoiseContextBuilder().spec(spec).profile(profile).build();
            new InconsistencyInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                context.report.getLogBook();
            double perc = spec.percentage;
            int changedItem = (int)(Math.ceil(perc * profile.getLength()));
            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void inconsistencyTest3() {
        try {
            NoiseJsonAdapter adapter =
                TestDataRepository.getAdapter(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist3.json"
                );

            CSVReader reader =
                new CSVReader(
                    new FileReader(adapter.getInputFile())
                );

            DataProfile profile =
                DataProfile.readData(
                    reader,
                    adapter.getSchema()
                );
            NoiseSpec spec = adapter.getSpecs().get(0);
            NoiseContext context =
                new NoiseContextBuilder().spec(spec).profile(profile).build();

            new InconsistencyInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                context.report.getLogBook();
            double perc = spec.percentage;
            int changedItem = (int)(Math.ceil(perc * profile.getLength()));
            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
