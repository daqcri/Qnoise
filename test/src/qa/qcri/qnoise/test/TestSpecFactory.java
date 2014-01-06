/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test;

import qa.qcri.qnoise.NoiseSpec;

/**
 */
public class TestSpecFactory {
    public static NoiseSpec createDummySpec() {
        try {
            NoiseSpec spec =
                TestDataRepository.getSpec("test/src/qa/qcri/qnoise/test/input/Duplicate1.json");
            return spec;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
