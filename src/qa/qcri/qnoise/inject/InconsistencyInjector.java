/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.constraint.ConstraintFactory;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InconsistencyInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    /** {@inheritDoc */
    @Override
    public InjectorBase inject(NoiseSpec spec, DataProfile profile, NoiseReport report) {
        HashSet<Integer> log = Sets.newHashSet();
        Stopwatch stopwatch = new Stopwatch().start();
        ModelBase indexGen =
            ModelFactory.createRandomModel();
        Constraint constraint =
            ConstraintFactory.createConstraintFromString(
                (String)spec.getValue(NoiseSpec.SpecEntry.Constraint)
            );
        double perc = spec.getValue(NoiseSpec.SpecEntry.Percentage);
        double distance = spec.getValue(NoiseSpec.SpecEntry.Distance);
        int[] filteredResult = filter(profile, constraint);
        int nseed = (int)(Math.ceil(perc * profile.getLength()));
        int size = Math.min(nseed, filteredResult.length);
        for (int i = 0; i < size; i ++) {
            int index;
            do {
                index = indexGen.nextIndex(0, filteredResult.length);
            } while (log.contains(index));

            log.add(index);
            int columnIndex = constraint
                .messIt(
                    profile,
                    filteredResult[index],
                    distance,
                    report
                );

            // TODO; where is the old value
            if (columnIndex == -1) {
                tracer.info("No possible element is found.");
            }
        }

        report.appendMetric(NoiseReport.Metric.ChangedItem, nseed);
        report.addMetric(
            NoiseReport.Metric.InjectionTime,
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );

        stopwatch.stop();
        return this;
    }

    private int[] filter(DataProfile profile, Constraint constraint) {
        List<Integer> result = Lists.newArrayList();
        List<String[]> data = profile.getData();

        for (int i = 0; i < data.size(); i ++) {
            if (constraint.isValid(profile, i)) {
                result.add(i);
            }
        }

        int[] tmp = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            tmp[i] = result.get(i);
        }
        return tmp;
    }
}
