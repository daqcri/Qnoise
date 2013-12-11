/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.inject;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.inject.InconsistencyInjector;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.util.Pair;
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
            types.add("TEXT");
            types.add("NUMERICAL");
            types.add("TEXT");
            profile = DataProfile.readData(reader, types);
            Tracer.setVerbose(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void InconsistencyTest1() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist1.json"
                );
            NoiseReport report = new NoiseReport(spec);
            new InconsistencyInjector().inject(spec, profile, report);
            List<Pair<Pair<Integer, Integer>, String>> logBook = report.getLogBook();
            double perc = spec.getValue(NoiseSpec.SpecEntry.Percentage);
            int changedItem = (int)(Math.ceil(perc * profile.getLength()));
            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void InconsistencyTest2() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist2.json"
                );
            NoiseReport report = new NoiseReport(spec);
            new InconsistencyInjector().inject(spec, profile, report);
            List<Pair<Pair<Integer, Integer>, String>> logBook = report.getLogBook();
            double perc = spec.getValue(NoiseSpec.SpecEntry.Percentage);
            int changedItem = (int)(Math.ceil(perc * profile.getLength()));
            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void InconsistencyTest3() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec(
                    "test/src/qa/qcri/qnoise/test/input/Inconsist3.json"
                );

            CSVReader reader =
                new CSVReader(
                    new FileReader(spec.<String>getValue(NoiseSpec.SpecEntry.InputFile))
                );

            DataProfile profile =
                DataProfile.readData(
                    reader,
                    (List<String>)spec.getValue(NoiseSpec.SpecEntry.Schema)
                );

            NoiseReport report = new NoiseReport(spec);
            new InconsistencyInjector().inject(spec, profile, report);
            List<Pair<Pair<Integer, Integer>, String>> logBook = report.getLogBook();
            double perc = spec.getValue(NoiseSpec.SpecEntry.Percentage);
            int changedItem = (int)(Math.ceil(perc * profile.getLength()));
            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
