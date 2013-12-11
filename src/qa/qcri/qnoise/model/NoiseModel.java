/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

public enum NoiseModel {
    Random,
    Histogram;

    public static NoiseModel fromString(String modal) {
        NoiseModel result;
        if (modal.equalsIgnoreCase("r")) {
            result = NoiseModel.Random;
        } else if (modal.equalsIgnoreCase("h")) {
            result = NoiseModel.Histogram;
        } else {
            throw new IllegalArgumentException("Unknown modal string " + modal);
        }
        return result;
    }
}
