/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.model.NoiseModel;

import java.util.List;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    final static char DEFAULT_CSV_SEPARATOR = ';';

    public String inputFile;
    public List<String> schema;
    public Character csvSeparator;
    public NoiseType noiseType;
    public GranularityType granularity;
    public Double percentage;
    public NoiseModel model;
    public List<String> filteredColumns;
    public Double numberOfSeed;
    public Double distance;
    public Constraint constraint;
    public String logFile;

    NoiseSpec() {}
}
