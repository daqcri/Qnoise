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
import qa.qcri.qnoise.inject.DuplicateInjector;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseContextBuilder;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.test.TestDataRepository;
import qa.qcri.qnoise.util.OperationType;

import java.io.FileReader;
import java.util.List;

public class DuplicateInjectTest {
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
    public void duplicateTest1() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Duplicate1.json");
            NoiseContext context =
                new NoiseContextBuilder().spec(spec).profile(profile).build();

            double seedperc = spec.numberOfSeed;
            double timeperc = spec.percentage;

            int nseed = (int)(Math.ceil(profile.getLength() * seedperc));
            int ntime = (int)(Math.ceil(profile.getLength() * timeperc));

            // first insert, then modify it
            int changedItem = nseed * ntime * profile.getWidth() * 2;

            new DuplicateInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                context.report.getLogBook();

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
            NoiseContext context =
                new NoiseContextBuilder().spec(spec).profile(profile).build();

            double seedperc = spec.numberOfSeed;
            double timeperc = spec.percentage;

            int nseed = (int)(Math.ceil(profile.getLength() * seedperc));
            int ntime = (int)(Math.ceil(profile.getLength() * timeperc));

            int size = profile.getLength();
            new DuplicateInjector().act(context, null);
            List<Quartet<OperationType, Pair<Integer, Integer>, String, String>> logBook =
                context.report.getLogBook();

            Assert.assertEquals(72, logBook.size());
            Assert.assertEquals(size + nseed * ntime, profile.getLength());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
    }
}
