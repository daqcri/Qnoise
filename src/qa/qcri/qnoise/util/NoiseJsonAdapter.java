/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import qa.qcri.qnoise.internal.NoiseSpec;

import java.util.List;

public class NoiseJsonAdapter {
    final static char DEFAULT_CSV_SEPARATOR = ';';
    String inputFile;
    List<String> schema;
    char csvSeparator;
    List<NoiseSpec> specs;

    public String getInputFile() {
        return inputFile;
    }

    public List<String> getSchema() {
        return schema;
    }

    public char getCsvSeparator() {
        return csvSeparator;
    }

    public List<NoiseSpec> getSpecs() {
        return specs;
    }
}
