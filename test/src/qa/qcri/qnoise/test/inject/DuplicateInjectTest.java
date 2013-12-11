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
import qa.qcri.qnoise.inject.DuplicateInjector;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.util.Pair;

import java.io.FileReader;
import java.util.List;

public class DuplicateInjectTest {
    private DataProfile profile;

    @Before
    public void startup() {
        try {
            CSVReader reader = new CSVReader(new FileReader(TestDataRepository.DUMPTEST));
            List<String> schema = Lists.newArrayList();
            schema.add("NUMERICAL");
            schema.add("TEXT");
            schema.add("TEXT");
            schema.add("TEXT");
            schema.add("NUMERICAL");
            profile = DataProfile.readData(reader, schema);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void duplicateTest1() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Duplicate1.json");
            NoiseReport report = new NoiseReport(spec);
            double seedperc = spec.getValue(NoiseSpec.SpecEntry.NumberOfSeed);
            double timeperc = spec.getValue(NoiseSpec.SpecEntry.Percentage);

            int nseed = (int)(Math.ceil(profile.getLength() * seedperc));
            int ntime = (int)(Math.ceil(profile.getLength() * timeperc));

            int changedItem = nseed * ntime * profile.getWidth();

            new DuplicateInjector().inject(spec, profile, report);
            List<Pair<Pair<Integer, Integer>, String>> logBook = report.getLogBook();

            Assert.assertEquals(changedItem, logBook.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void duplicateTest2() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Duplicate2.json");
            NoiseReport report = new NoiseReport(spec);
            double seedperc = spec.getValue(NoiseSpec.SpecEntry.NumberOfSeed);
            double timeperc = spec.getValue(NoiseSpec.SpecEntry.Percentage);

            int nseed = (int)(Math.ceil(profile.getLength() * seedperc));
            int ntime = (int)(Math.ceil(profile.getLength() * timeperc));

            int changedItem = nseed * ntime * profile.getWidth();
            int size = profile.getLength();
            new DuplicateInjector().inject(spec, profile, report);
            List<Pair<Pair<Integer, Integer>, String>> logBook = report.getLogBook();

            Assert.assertEquals(changedItem, logBook.size());
            Assert.assertEquals(size + nseed * ntime, profile.getLength());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
