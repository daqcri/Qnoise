/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;

import java.util.HashMap;

/**
 * Helper class to play the noise.
 */
public class NoiseHelper {
    private static Tracer tracer = Tracer.getTracer(NoiseHelper.class);

    public static void playTheJazz(
        double distance,
        String column,
        DataProfile profile,
        int rowIndex,
        NoiseReport report
    ) {
        playTheJazz(
            new double[] { distance },
            new String[] { column },
            profile,
            rowIndex,
            report
        );
    }

    /**
     * Change cell value based on distance.
     * @param distances the distance from the new value.
     * @param selectedColumns changed column names.
     * @param profile data profile.
     * @param rowIndex tuple index.
     */
    public static void playTheJazz(
        @NotNull double[] distances,
        @NotNull String[] selectedColumns,
        DataProfile profile,
        int rowIndex,
        NoiseReport report
    ) {
        Preconditions.checkArgument(distances.length > 0);

        String[] tuple = profile.getReadOnlyTuple(rowIndex);
        BiMap<String, Integer> indexes = profile.getIndexes();
        HashMap<String, DataType> types = profile.getTypes();

        for (int i = 0; i < selectedColumns.length; i ++) {
            String columnName = selectedColumns[i];
            int columnIndex = indexes.get(columnName);
            double distance = distances[i];
            DataType type = types.get(columnName);
            String currentValue = tuple[columnIndex];
            String newValue = "";

            switch (type) {
                case Text:
                    if (currentValue != null) {
                        StringBuilder sb = new StringBuilder(currentValue);
                        int len = (int)Math.floor(distance * sb.length());
                        // TODO: currently we start to mess the text from the 1st char.
                        // It might be more reasonable to use a random gen. again to pick
                        // the char to change.
                        for (int j = 0; j < len; j ++) {
                            char c = sb.charAt(j);
                            char nc = getRandomChar();
                            while (nc == c) {
                                nc = getRandomChar();
                            }
                            sb.setCharAt(j, nc);
                        }
                        newValue = sb.toString();
                    }
                    break;
                case Numerical:
                    double std = profile.getStandardDeviationOn(columnName);
                    double nvalue =
                        distance * std * getRandomSign() + Double.parseDouble(currentValue);
                    newValue = Double.toString(nvalue);
                    break;
                case Categorical:
                    for (int j = 0; j < profile.getLength(); j ++) {
                        String t = profile.getCell(j, columnIndex);
                        if (!t.equals(currentValue)) {
                            newValue = t;
                            break;
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown type " + type);
            }

            Pair<Integer, Integer> index = new Pair<>(rowIndex, columnIndex);
            if (profile.set(index, newValue)) {
                tracer.infoChange(index, profile.getCell(index), newValue);
                report.logChange(index, tuple[columnIndex], newValue);
            } else {
                tracer.infoUnchange(index);
            }
        }
    }

    public static String verify(NoiseSpec spec) {
        switch (spec.noiseType) {
            case Inconsistency:
                if (spec.constraint == null)
                    return "Constraint cannot be null.";
                break;
            case Duplicate:
                if (spec.numberOfSeed == null)
                    return "Numer of Seed is missing.";
                break;
            case Error:
                if (spec.granularity == GranularityType.Cell && spec.distance != null)
                    return "Input distance is missing.";
                break;
        }
        return null;
    }

    private static char getRandomChar() {
        ModelBase indexStrategy =
                ModelFactory.createRandomModel();
        int r = indexStrategy.nextIndex(0, 52);
        if (r < 26)
            return (char)(r + 'a');
        return (char)(r - 26 + 'A');
    }

    private static int getRandomSign() {
        ModelBase indexStrategy =
                ModelFactory.createRandomModel();
        int r = indexStrategy.nextIndex(0, 2);
        if (r < 1)
            return -1;
        return 1;
    }
}
