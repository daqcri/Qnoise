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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DataProfile {
    private List<String[]> data;
    private String[] columnNames;
    private HashMap<String, DataType> types;
    private BiMap<String, Integer> indexes;
    private HashMap<String, Double> mean;
    private HashMap<String, Double> var;
    private HashMap<String, Double> min;
    private HashMap<String, Double> max;

    DataProfile(
        List<String[]> data,
        String[] columnNames,
        DataType[] types
    ) {
        this.data = data;
        this.columnNames = new String[columnNames.length];
        this.types = Maps.newHashMap();
        this.indexes = HashBiMap.create();
        for (int i = 0; i < columnNames.length; i ++) {
            this.columnNames[i] = columnNames[i].trim();
            this.types.put(this.columnNames[i], types[i]);
            this.indexes.put(this.columnNames[i], i);
        }

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

    public String[] getColumnNames() {
        return columnNames;
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

    public double getDouble(int rowIndex, int columnIndex) {
        DataType type = getType(columnIndex);
        Preconditions.checkArgument(type == DataType.NUMERICAL);
        String[] tuple = getTuple(rowIndex);
        return Double.parseDouble(tuple[columnIndex]);
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

    /**
     * Creates a DataProfile from a CSV file.
     * @param reader reader instance.
     * @param typeList column type list.
     * @return a DataProfile instance.
     * @throws IOException
     */
    public static DataProfile readData(
        CSVReader reader,
        List<String> typeList
    ) throws IOException {
        Preconditions.checkNotNull(reader);
        String[] header = reader.readNext();
        List<String[]> entries = reader.readAll();
        DataType[] types = new DataType[header.length];

        for (int i = 0; i < types.length; i ++) {
            if (typeList == null) {
                types[i] = DataType.TEXT;
            } else {
                types[i] = DataType.valueOf(typeList.get(i).toUpperCase());
            }
        }
        return new DataProfile(entries, header, types);
    }

    public void writeData(CSVWriter writer) throws IOException {
        writer.writeAll(data);
        writer.flush();
    }
}
