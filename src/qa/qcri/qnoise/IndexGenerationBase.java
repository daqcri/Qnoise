/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

/**
 * Strategy class for generating index.
 */
public abstract class IndexGenerationBase {
    /**
     * Generates an index within the range.
     * @param start range start.
     * @param end range end.
     * @return index.
     */
    public abstract int nextIndex(int start, int end);

    /**
     * Generates an index without replacement (no duplicate).
     * It returns {@code Integer.MIN_VALUE} when there is no more index available.
     * @param start range start.
     * @param end range end.
     * @param reset reset the generation.
     * @return index.
     */
    public abstract int nextIndexWithoutReplacement(int start, int end, boolean reset);

    /**
     * Returns a number in the range of 0 - 1 based on the generation strategy.
     */
    public abstract double dart();

    public static IndexGenerationBase createIndexStrategy(NoiseModel model) {
        switch (model) {
            case RANDOM:
                return new RandomIndex();
            default:
                throw new UnsupportedOperationException("Unknown model " + model);
        }
    }
}
