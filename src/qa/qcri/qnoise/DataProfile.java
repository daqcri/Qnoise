/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DataProfile {
    private List<String[]> data;
    private HashMap<String, DataType> types;
    private BiMap<String, Integer> indexes;
    private Optional<Double> mean;
    private Optional<Double> var;

    public DataProfile(
        List<String[]> data,
        HashMap<String, DataType> schema,
        BiMap<String, Integer> indexes
    ) {
        this.data = data;
        this.types = schema;
        this.indexes = indexes;
        this.mean = Optional.absent();
        this.var = Optional.absent();
    }

    public List<String[]> getData() {
        return data;
    }

    public HashMap<String, DataType> getTypes() {
        return types;
    }

    public BiMap<String, Integer> getIndexes() {
        return indexes;
    }

    public DataType getType(int i) {
        String columnName = indexes.inverse().get(i);
        return types.get(columnName);
    }

    public int getLength() {
        return data.size();
    }

    public int getWidth() {
        return types.size();
    }

    public synchronized void setDirty() {
        mean = Optional.absent();
        var = Optional.absent();
    }

    public synchronized double getMean(String column) {
        if (!mean.isPresent()) {
            DataType type = types.get(column);
            if (type != DataType.NUMERICAL) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            int index = indexes.get(column);
            double sum = 0;
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i)[index];
                sum += Double.parseDouble(t);
            }

            sum = sum / getLength();
            mean = Optional.of(sum);
        }
        return mean.get();
    }

    public synchronized double getVariance(String column) {
        if (!var.isPresent()) {
            DataType type = types.get(column);
            if (type != DataType.NUMERICAL) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            int index = indexes.get(column);
            double sum = 0;
            double mean = getMean(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i)[index];
                double tmp = Double.parseDouble(t);
                sum += (tmp - mean) * (tmp - mean);
            }

            sum = sum / getLength();
            var = Optional.of(sum);
        }
        return var.get();
    }

    public synchronized double getStandardDeviationOn(String column) {
        double var = getVariance(column);
        return Math.sqrt(var);
    }

    public static DataProfile readData(CSVReader reader, JSONArray schemaObj) throws IOException {
        String[] header = reader.readNext(); // skip the header
        List<String[]> entries = reader.readAll();
        HashMap<String, DataType> schema = Maps.newHashMap();
        BiMap<String, Integer> indexes = HashBiMap.create();
        if (schemaObj == null) {
            for (int i = 0; i < header.length; i ++) {
                schema.put(header[i], DataType.TEXT);
                indexes.put(header[i], i);
            }
        } else {
            for (int i = 0; i < header.length; i ++) {
                String typeString = (String)schemaObj.get(i);
                DataType type = DataType.valueOf(typeString);
                schema.put(header[i], type);
                indexes.put(header[i], i);
            }
        }

        return new DataProfile(entries, schema, indexes);
    }

    public void writeData(CSVWriter writer) throws IOException {
        writer.writeAll(data);
        writer.flush();
    }
}
