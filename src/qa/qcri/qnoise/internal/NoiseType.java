/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

public enum NoiseType {
    Missing,
    Inconsistency,
    Outlier,
    Error,
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

        if (model.equalsIgnoreCase("o")) {
            return Outlier;
        }

        if (model.equalsIgnoreCase("s")) {
            return Error;
        }

        throw new IllegalArgumentException("Unknown model string " + model);
    }
}
