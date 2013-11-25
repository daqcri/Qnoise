/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

// TODO: not a thread-safe class.
public class DataProfile {
    private List<String[]> data;
    private HashMap<String, DataType> types;
    private BiMap<String, Integer> indexes;
    private HashMap<String, Double> mean;
    private HashMap<String, Double> var;
    private HashMap<String, Double> min;
    private HashMap<String, Double> max;

    public DataProfile(
        List<String[]> data,
        HashMap<String, DataType> schema,
        BiMap<String, Integer> indexes
    ) {
        this.data = data;
        this.types = schema;
        this.indexes = indexes;
        this.mean = Maps.newHashMap();
        this.var = Maps.newHashMap();
        this.max = Maps.newHashMap();
        this.min = Maps.newHashMap();
    }

    public DataProfile append(String[] value) {
        data.add(value);
        return this;
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

    public int getColumnIndex(String columnName) {
        return indexes.get(columnName);
    }

    public DataType getType(int i) {
        String columnName = indexes.inverse().get(i);
        return types.get(columnName);
    }

    public DataType getType(String columnName) {
        Preconditions.checkArgument(types.containsKey(columnName));
        return types.get(columnName);
    }

    public int getLength() {
        return data.size();
    }

    public int getWidth() {
        return types.size();
    }

    /**
     * Gets the tuple values based on the order of the stored table.
     * @param index index number.
     * @return tuple in a String array.
     */
    public String[] getTuple(int index) {
        Preconditions.checkArgument(index < getLength());
        return data.get(index);
    }

    /**
     * Gets the tuple column value.
     * @param rowIndex row index.
     * @param columnIndex column index.
     * @return cell value.
     */
    public String getCell(int rowIndex, int columnIndex) {
        String[] tuple = getTuple(rowIndex);
        return tuple[columnIndex];
    }

    /**
     * setDirty is called when the data in this profile has been changed.
     */
    public void setDirty() {
        mean.clear();
        var.clear();
        min.clear();
        max.clear();
    }

    public double getMean(String column) {
        if (!mean.containsKey(column)) {
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
            mean.put(column, sum);
        }
        return mean.get(column);
    }

    public double getVariance(String column) {
        if (!var.containsKey(column)) {
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
            var.put(column, sum);
        }
        return var.get(column);
    }

    public double getMin(String column) {
        if (!min.containsKey(column)) {
            DataType type = types.get(column);
            if (type != DataType.NUMERICAL) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            double cur = Double.MAX_VALUE;
            int index = indexes.get(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i)[index];
                Double tmp = Double.parseDouble(t);
                if (cur > tmp)
                    cur = tmp;
            }
            min.put(column, cur);
        }
        return min.get(column);
    }

    public double getMax(String column) {
        if (!max.containsKey(column)) {
            DataType type = types.get(column);
            if (type != DataType.NUMERICAL) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            double cur = Double.MIN_VALUE;
            int index = indexes.get(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i)[index];
                Double tmp = Double.parseDouble(t);
                if (cur < tmp)
                    cur = tmp;
            }
            max.put(column, cur);
        }
        return max.get(column);
    }

    public double getStandardDeviationOn(String column) {
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
                schema.put(header[i].trim(), DataType.TEXT);
                indexes.put(header[i].trim(), i);
            }
        } else {
            for (int i = 0; i < header.length; i ++) {
                String typeString = (String)schemaObj.get(i);
                DataType type = DataType.valueOf(typeString);
                schema.put(header[i].trim(), type);
                indexes.put(header[i].trim(), i);
            }
        }

        return new DataProfile(entries, schema, indexes);
    }

    public void writeData(CSVWriter writer) throws IOException {
        writer.writeAll(data);
        writer.flush();
    }
}
