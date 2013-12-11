/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

import qa.qcri.qnoise.DataProfile;

/**
 * Model Factory.
 */
public class ModelFactory {
    public static RandomModel createRandomModel() {
        return new RandomModel();
    }

    public static HistogramModel createHistogramModel(DataProfile profile, String columnName) {
        return new HistogramModel(profile, columnName);
    }
}
