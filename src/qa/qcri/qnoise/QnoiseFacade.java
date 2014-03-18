/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.constraint.ConstraintFactory;
import qa.qcri.qnoise.inject.InjectorFactory;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.model.NoiseModel;
import qa.qcri.qnoise.util.NoiseHelper;

import java.util.List;

public class QnoiseFacade {
    /**
     * Call with only primitive types.
     *
     * This is used mainly for cross-language bridging purpose (e.g. call from R).
     */
    public static String[][] inject(
        @NotNull String[][] data,
        int noiseType,
        int granularity,
        double percentage,
        int model,
        String[] filteredColumns,
        double seed,
        double[] distance,
        String[] constraints,
        String logFile
    ) {
        try {
            List<List<String>> dataList = Lists.newArrayList();
            for (int i = 0; i < data.length; i ++) {
                List<String> list = Lists.newArrayList();
                for (int j = 0; j < data[i].length; j ++)
                    list.add(data[i][j]);
                dataList.add(list);
            }
            DataProfile profile = new DataProfile(dataList);

            NoiseSpec spec = new NoiseSpec();
            spec.noiseType = NoiseType.fromInt(noiseType);
            spec.granularity = GranularityType.fromInt(granularity);
            spec.percentage = percentage;
            spec.model = NoiseModel.fromInt(model);
            spec.filteredColumns = filteredColumns;
            spec.numberOfSeed = seed;
            if (distance != null) {
                spec.distance = new double[distance.length];
                for (int i = 0; i < distance.length; i ++)
                    spec.distance[i] = distance[i];
            }

            if (constraints != null) {
                spec.constraint = new Constraint[constraints.length];
                for (int i = 0; i < constraints.length; i ++)
                    spec.constraint[i] = ConstraintFactory.createConstraintFromString(constraints[i]);
            }

            spec.logFile = logFile;
            inject(profile, Lists.newArrayList(spec));

            // copy back the result
            List<List<String>> resultList = profile.getData();
            String[][] result = new String[resultList.size()][];
            for (int i = 0; i < result.length; i ++) {
                result[i] = new String[resultList.get(0).size()];
                for (int j = 0; j < resultList.get(0).size(); j ++)
                    result[i][j] = resultList.get(i).get(j);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void inject(DataProfile profile, List<NoiseSpec> specs) {
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(specs);

        for (NoiseSpec spec : specs) {
            new DataProcess(
                new NoiseContextBuilder()
                    .profile(profile)
                    .spec(spec)
                    .build()
            ).process(
                InjectorFactory.createInjector(spec.noiseType)
            );
        }
    }

    public static void inject(
        DataProfile profile,
        List<NoiseSpec> specs,
        IAction<NoiseContext> after
    ) {
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(specs);

        for (NoiseSpec spec : specs) {
            new DataProcess(
                new NoiseContextBuilder()
                    .profile(profile)
                    .spec(spec)
                    .build()
            ).process(
                InjectorFactory.createInjector(spec.noiseType)
            ).after(after);
        }
    }

    public static String verfiy(NoiseSpec spec) {
        return NoiseHelper.verify(spec);
    }
}
