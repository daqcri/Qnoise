/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

public enum NoiseModal {
    RANDOM,
    NORMAL;

    public static NoiseModal getNoiseModal(String modal) {
        if (modal.equalsIgnoreCase("random")) {
            return NoiseModal.RANDOM;
        }

        if (modal.equalsIgnoreCase("normal")) {
            return NoiseModal.NORMAL;
        }

        throw new IllegalArgumentException("Unknown modal string " + modal);
    }
}
