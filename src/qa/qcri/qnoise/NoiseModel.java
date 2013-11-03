/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public enum NoiseModel {
    RANDOM;

    public static NoiseModel fromString(String modal) {
        if (modal.equalsIgnoreCase("r")) {
            return NoiseModel.RANDOM;
        }

        throw new IllegalArgumentException("Unknown modal string " + modal);
    }
}
