/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import qa.qcri.qnoise.NoiseSpec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestDataRepository {
    public static final String DUMPTEST =
        "test/src/qa/qcri/qnoise/test/input/dumptest.csv";
    public static final String PERSONALDATA =
            "test/src/qa/qcri/qnoise/test/input/personal.csv";


    public static NoiseSpec getSpec(String fileName) throws FileNotFoundException{
        if (Files.notExists(Paths.get(fileName))) {
            throw new FileNotFoundException("Input file " + fileName + " does not exist.");
        }

        JSONObject input = (JSONObject) JSONValue.parse(new FileReader(fileName));
        if (input == null) {
            throw new IllegalArgumentException("Input file is not a valid JSON file.");
        }

        NoiseSpec spec = NoiseSpec.valueOf(input);
        return spec;
    }
}
