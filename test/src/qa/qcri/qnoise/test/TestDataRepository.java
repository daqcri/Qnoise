/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.util.NoiseJsonAdapter;
import qa.qcri.qnoise.util.NoiseJsonAdapterDeserializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestDataRepository {
    public static final String DUMPTEST =
        "test/src/qa/qcri/qnoise/test/input/dumptest.csv";
    public static final String PERSONALDATA =
            "test/src/qa/qcri/qnoise/test/input/personal.csv";

    public static NoiseSpec getSpec(String fileName) throws FileNotFoundException{
        return getAdapter(fileName).getSpecs().get(0);
    }

    public static NoiseJsonAdapter getAdapter(String fileName) throws FileNotFoundException{
        if (Files.notExists(Paths.get(fileName))) {
            throw new FileNotFoundException("Input file " + fileName + " does not exist.");
        }

        JsonReader jsonReader =
            new JsonReader(
                new InputStreamReader(
                    new FileInputStream(fileName),
                    Charset.forName("UTF-8")
                )
            );

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(NoiseJsonAdapter.class, new NoiseJsonAdapterDeserializer());
        NoiseJsonAdapter adapter = gson.create().fromJson(jsonReader, NoiseJsonAdapter.class);
        return adapter;
    }
}
