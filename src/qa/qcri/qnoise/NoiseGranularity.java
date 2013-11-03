/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public enum NoiseGranularity {
    COLUMN,
    ROW,
    CELL;

    public static NoiseGranularity fromString(String granularity) {
        if (granularity.equalsIgnoreCase("row")) {
            return NoiseGranularity.ROW;
        }

        if (granularity.equalsIgnoreCase("cell")) {
            return NoiseGranularity.CELL;
        }

        if (granularity.equalsIgnoreCase("column")) {
            return NoiseGranularity.COLUMN;
        }

        throw new IllegalArgumentException("Unknown granularity string " + granularity);
    }
}
