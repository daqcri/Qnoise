/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public enum NoiseType {
    Missing,
    Inconsistency,
    Outlier,
    Duplicate;

    public static NoiseType fromString(String model) {
        if (model.equalsIgnoreCase("m")) {
            return Missing;
        }

        if (model.equalsIgnoreCase("d")) {
            return Duplicate;
        }

        if (model.equalsIgnoreCase("i")) {
            return Inconsistency;
        }

        throw new IllegalArgumentException("Unknown model string " + model);
    }
}
