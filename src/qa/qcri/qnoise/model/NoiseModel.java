/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

public enum NoiseModel {
    Random,
    Histogram;

    public static NoiseModel fromString(String model) {
        NoiseModel result;
        if (model.equalsIgnoreCase("r")) {
            result = NoiseModel.Random;
        } else if (model.equalsIgnoreCase("h")) {
            result = NoiseModel.Histogram;
        } else {
            throw new IllegalArgumentException("Unknown modal string " + model);
        }
        return result;
    }

    public static NoiseModel fromInt(int model) {
        switch (model) {
            case 0: return Random;
            case 1: return Histogram;
            default:
                throw new IllegalArgumentException("Unknown noise model");
        }
    }
}
