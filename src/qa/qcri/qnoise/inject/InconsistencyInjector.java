/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseContext;
import qa.qcri.qnoise.internal.NoiseReport;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InconsistencyInjector extends InjectorBase {
    private Tracer tracer = Tracer.getTracer(this.getClass());

    /** {@inheritDoc */
    @Override
    public void act(
        NoiseContext context,
        HashMap<String, Object> extras
    ) {
        Stopwatch stopwatch = new Stopwatch().start();
        HashSet<Integer> log = Sets.newHashSet();
        NoiseSpec spec = context.spec;
        DataProfile profile = context.profile;
        NoiseReport report = context.report;

        ModelBase indexGen =
            ModelFactory.createRandomModel();
        Constraint[] constraint = spec.constraint;
        double perc = spec.percentage;
        // by default, we keep it in the active domain
        double distances = spec.distance == null ? 0.0 : spec.distance[0];
        int[] filteredResult = filter(profile, constraint);
        int nseed = (int)(Math.ceil(perc * profile.getLength()));
        int size = Math.min(nseed, filteredResult.length);
        for (int i = 0; i < size; i ++) {
            int index;
            do {
                index = indexGen.nextIndex(0, filteredResult.length);
            } while (log.contains(index));

            log.add(index);
            int columnIndex = constraint[0]
                .messIt(
                    profile,
                    filteredResult[index],
                    distances,
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
    }

    private int[] filter(DataProfile profile, Constraint[] constraint) {
        List<Integer> result = Lists.newArrayList();
        boolean isValid;
        for (int i = 0; i < profile.getLength(); i ++) {
            isValid = true;
            for (int j = 0; j < constraint.length; j ++)
                if (!constraint[j].isValid(profile, i)) {
                    isValid = false;
                    break;
                }
            if (isValid) {
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
