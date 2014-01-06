/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.DataType;
import qa.qcri.qnoise.NoiseReport;
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
        playTheJazz(distance, new String[] { column }, profile, rowIndex, report);
    }

    public static void playTheJazz(
            Optional<Double> distance,
            Optional<String[]> columns,
            DataProfile profile,
            int rowIndex,
            NoiseReport report
    ) {
        double d = distance.isPresent() ? distance.get() : 0.0;
        String[] selectedColumns;
        if (!columns.isPresent()) {
            HashMap<String, DataType> types = profile.getTypes();
            selectedColumns = new String[types.size()];
            types.keySet().toArray(selectedColumns);
        } else {
            selectedColumns = columns.get();
        }
        playTheJazz(d, selectedColumns, profile, rowIndex, report);
    }

    /**
     * Change cell value based on distance.
     * @param distance the distance from the new value.
     * @param selectedColumns changed column names.
     * @param profile data profile.
     * @param rowIndex tuple index.
     */
    public static void playTheJazz(
        double distance,
        String[] selectedColumns,
        DataProfile profile,
        int rowIndex,
        NoiseReport report
    ) {
        Preconditions.checkNotNull(selectedColumns);
        Preconditions.checkArgument(distance >= 0.0);
        if (distance == 0.0) {
            return;
        }

        String[] tuple = profile.getTuple(rowIndex);
        BiMap<String, Integer> indexes = profile.getIndexes();
        HashMap<String, DataType> types = profile.getTypes();

        for (int i = 0; i < selectedColumns.length; i ++) {
            String columnName = selectedColumns[i];
            int columnIndex = indexes.get(columnName);
            DataType type = types.get(columnName);
            String currentValue = tuple[columnIndex];
            String newValue = "";

            switch (type) {
                case TEXT:
                    StringBuilder sb = new StringBuilder(currentValue);
                    int len = (int)Math.floor(distance * sb.length() * 0.01);
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
                    break;
                case NUMERICAL:
                    double std = profile.getStandardDeviationOn(columnName);
                    double nvalue =
                        distance * 0.01 * std * getRandomSign() + Double.parseDouble(currentValue);
                    newValue = Double.toString(nvalue);
                    break;
                case ENUM:
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

            tracer.verbose(
                String.format(
                    "[%d][%s] from %s to %s",
                    rowIndex,
                    columnName,
                    tuple[columnIndex],
                    newValue
                )
            );
            report.logChange(rowIndex, columnIndex, tuple[columnIndex], newValue);
            tuple[columnIndex] = newValue;
        }
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
