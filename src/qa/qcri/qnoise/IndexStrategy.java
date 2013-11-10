/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

/**
 * Strategy class for generating index.
 */
public abstract class IndexStrategy {
    public abstract int getIndex(int start, int end);

    public static IndexStrategy createIndexStrategy(NoiseModel model) {
        switch (model) {
            case RANDOM:
                return new RandomIndexStrategy();
            default:
                throw new UnsupportedOperationException("Unknown model " + model);
        }
    }
}
