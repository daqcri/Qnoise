/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.model.NoiseModel;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    public NoiseType noiseType;
    public GranularityType granularity;
    public Double percentage;
    public NoiseModel model;
    public String[] filteredColumns;
    public Double numberOfSeed;
    public double[] distance;
    public Constraint[] constraint;
    public String logFile;
}
