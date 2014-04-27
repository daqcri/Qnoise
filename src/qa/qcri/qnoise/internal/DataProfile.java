/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataProfile {
    private List<List<String>> data;
    private HashSet<Pair<Integer, Integer>> mark;
    private List<String> columnNames;
    private HashMap<String, DataType> types;
    private BiMap<String, Integer> indexes;
    private HashMap<String, Double> mean;
    private HashMap<String, Double> var;
    private HashMap<String, Double> min;
    private HashMap<String, Double> max;

    private void internalCreation(
        @NotNull List<List<String>> data,
        @NotNull List<String> columnNames,
        @NotNull List<DataType> types
    ) {
        this.data = data;
        this.columnNames = Lists.newArrayList();
        this.types = Maps.newHashMap();
        this.indexes = HashBiMap.create();
        for (int i = 0; i < columnNames.size(); i ++) {
            this.columnNames.add(columnNames.get(i).trim());
            this.types.put(this.columnNames.get(i), types.get(i));
            this.indexes.put(this.columnNames.get(i), i);
        }

        this.mean = Maps.newHashMap();
        this.var = Maps.newHashMap();
        this.max = Maps.newHashMap();
        this.min = Maps.newHashMap();
        this.mark = Sets.newHashSet();
    }

    public DataProfile(@NotNull List<List<String>> data) {
        Preconditions.checkArgument(data.size() > 0);
        int width = 0;
        for (int i = 0; i < data.size(); i ++) {
            if (data.get(i).size() > 0) {
                width = data.get(i).size();
                break;
            }
        }

        if (width == 0)
            throw new IllegalArgumentException("Data cannot be empty.");

        List<String> columnNames = Lists.newArrayList();
        List<DataType> dataTypes = Lists.newArrayList();
        for (int i = 0; i < width; i ++) {
            // TODO: limited to 52 default columns
            columnNames.add((i > 25 ? (char)((i - 25) + 'A') : (char)(i + 'a')) + "");
            dataTypes.add(DataType.Text);
        }

        internalCreation(data, columnNames, dataTypes);
    }

    @SuppressWarnings("unchecked")
    public <T> DataProfile(
        @NotNull List<List<String>> data,
        @NotNull List<T> params
    ) {
        Preconditions.checkArgument(data.size() > 0);
        Preconditions.checkArgument(params.size() > 0);

        int width = 0;
        for (int i = 0; i < data.size(); i ++) {
            if (data.get(i).size() > 0) {
                width = data.get(i).size();
                break;
            }
        }

        if (width == 0)
            throw new IllegalArgumentException("Data cannot be empty.");

        List<DataType> dataTypes = null;
        List<String> columnNames = null;
        T instance = params.get(0);
        if (instance instanceof DataType) {
            dataTypes = (List<DataType>)params;
            columnNames = Lists.newArrayList();
        }

        if (instance instanceof String) {
            columnNames = (List<String>)params;
            dataTypes = Lists.newArrayList();
        }

        for (int i = 0; i < width; i ++) {
            if (instance instanceof DataType)
                // TODO: limited to 52 default columns
                columnNames.add(i > 25 ? (i - 25) + "A" : i + "a");

            if (instance instanceof String) {
                dataTypes.add(DataType.Text);
            }
        }
        internalCreation(data, columnNames, dataTypes);
    }

    public DataProfile(
        @NotNull List<List<String>> data,
        @NotNull List<String> columnNames,
        @NotNull List<DataType> types
    ) {
        internalCreation(data, columnNames, types);
    }

    public DataProfile append(String[] values) {
        List<String> newList = Lists.newArrayList();
        for (String value : values)
            newList.add(value);
        data.add(newList);
        return this;
    }

    public void mark(Pair<Integer, Integer> index) {
        if (!mark.contains(index))
            mark.add(index);
    }

    public void unmark(Pair<Integer, Integer> index) {
        if (mark.contains(index))
            mark.remove(index);
    }

    public boolean set(Pair<Integer, Integer> index, String value) {
        if (isDirty(index))
            return false;
        int rowIndex = index.getValue0();
        int cellIndex = index.getValue1();
        data.get(rowIndex).set(cellIndex, value);
        mark.add(index);
        return true;
    }

    public boolean isDirty(Pair<Integer, Integer> index) {
        return mark.contains(index);
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    /**
     * Immutable array.
     */
    public String[] getColumnNames() {
        String[] result = new String[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i ++)
            result[i] = columnNames.get(i);
        return result;
    }

    public HashMap<String, DataType> getTypes() {
        return types;
    }

    public BiMap<String, Integer> getIndexes() {
        return indexes;
    }

    public int getColumnIndex(String columnName) {
        if (!indexes.containsKey(columnName)) {
            throw new IllegalArgumentException(columnName + " cannot be found.");
        }
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
    public String[] getReadOnlyTuple(int index) {
        Preconditions.checkArgument(index < getLength());
        String[] tuple = new String[getWidth()];
        data.get(index).toArray(tuple);
        return tuple;
    }

    /**
     * Gets the tuple column value.
     * @param rowIndex row index.
     * @param columnIndex column index.
     * @return cell value.
     */
    public String getCell(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    public String getCell(Pair<Integer, Integer> index) {
        return getCell(index.getValue0(), index.getValue1());
    }

    public double getDouble(int rowIndex, int columnIndex) {
        DataType type = getType(columnIndex);
        Preconditions.checkArgument(type == DataType.Numerical);
        return Double.parseDouble(getCell(rowIndex, columnIndex));
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
            if (type != DataType.Numerical) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            int index = indexes.get(column);
            double sum = 0;
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i).get(index);
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
            if (type != DataType.Numerical) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            int index = indexes.get(column);
            double sum = 0;
            double mean = getMean(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i).get(index);
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
            if (type != DataType.Numerical) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            double cur = Double.MAX_VALUE;
            int index = indexes.get(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i).get(index);
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
            if (type != DataType.Numerical) {
                throw new IllegalArgumentException("Cannot get mean on non-numerical data");
            }

            double cur = Double.MIN_VALUE;
            int index = indexes.get(column);
            for (int i = 0; i < getLength(); i ++) {
                String t = data.get(i).get(index);
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
        List<DataType> types = Lists.newArrayList();
        List<List<String>> data = Lists.newArrayList();

        Preconditions.checkArgument(entries != null && entries.size() > 0);

        for (String[] entry : entries) {
            data.add(Lists.newArrayList(entry));
        }

        for (int i = 0; i < entries.get(0).length; i ++) {
            if (typeList == null) {
                types.add(DataType.Text);
            } else {
                types.add(DataType.fromString(typeList.get(i).toUpperCase()));
            }
        }
        return new DataProfile(data, Lists.newArrayList(header), types);
    }

    public void writeData(CSVWriter writer) throws IOException {
        List<String[]> csvData = Lists.newArrayList();
        for (List<String> entry : data) {
            Object[] data = entry.toArray();
            String[] strData = new String[data.length];
            for (int i = 0; i < data.length; i ++)
                strData[i] = (String)data[i];
            csvData.add(strData);
        }
        writer.writeAll(csvData);
        writer.flush();
    }

    public List<List<String>> getData() {
        return data;
    }
}
