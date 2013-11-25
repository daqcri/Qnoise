/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.DataType;
import qa.qcri.qnoise.IndexGenerationBase;
import qa.qcri.qnoise.NoiseModel;

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
        int rowIndex) {
        playTheJazz(distance, new String[] { column }, profile, rowIndex);
    }

    public static void playTheJazz(
            Optional<Double> distance,
            Optional<String[]> columns,
            DataProfile profile,
            int rowIndex) {
        double d = distance.isPresent() ? distance.get() : 0.0;
        String[] selectedColumns;
        if (!columns.isPresent()) {
            HashMap<String, DataType> types = profile.getTypes();
            selectedColumns = new String[types.size()];
            types.keySet().toArray(selectedColumns);
        } else {
            selectedColumns = columns.get();
        }
        playTheJazz(d, selectedColumns, profile, rowIndex);
    }

    public static void playTheJazz(
        double distance,
        String[] columns,
        DataProfile profile,
        int rowIndex
    ) {
        if (distance == 0.0) {
            return;
        }

        String[] tuple = profile.getTuple(rowIndex);
        BiMap<String, Integer> indexes = profile.getIndexes();
        HashMap<String, DataType> types = profile.getTypes();

        for (int i = 0; i < columns.length; i ++) {
            String columnName = columns[i];
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
            tuple[columnIndex] = newValue;
        }
    }

    private static char getRandomChar() {
        IndexGenerationBase indexStrategy =
                IndexGenerationBase.createIndexStrategy(NoiseModel.RANDOM);
        int r = indexStrategy.nextIndex(0, 52);
        if (r < 26)
            return (char)(r + 'a');
        return (char)(r - 26 + 'A');
    }

    private static int getRandomSign() {
        IndexGenerationBase indexStrategy =
                IndexGenerationBase.createIndexStrategy(NoiseModel.RANDOM);
        int r = indexStrategy.nextIndex(0, 2);
        if (r < 1)
            return -1;
        return 1;
    }
}
