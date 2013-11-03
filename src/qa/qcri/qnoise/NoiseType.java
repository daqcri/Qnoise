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

    public static NoiseType getGeneratorType(String modal) {
        if (modal.equalsIgnoreCase("m")) {
            return Missing;
        }

        if (modal.equalsIgnoreCase("d")) {
            return Duplicate;
        }

        throw new IllegalArgumentException("Unknown modal string " + modal);
    }
}
