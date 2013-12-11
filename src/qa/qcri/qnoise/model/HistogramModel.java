/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import qa.qcri.qnoise.DataProfile;

import java.util.HashMap;
import java.util.List;

/**
 * Number model based on histogram.
 */
public class HistogramModel extends ModelBase {
    private DataProfile profile;
    private int columnIndex;

    public HistogramModel(DataProfile profile, String columnName) {
        super();
        this.profile = profile;
        this.columnIndex = profile.getColumnIndex(columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nextIndex(int start, int end) {
        HashMap<String, Integer> count = Maps.newHashMap();
        HashMap<String, List<Integer>> index = Maps.newHashMap();
        int length = end - start;
        for (int i = start; i < end; i ++) {
            String data = profile.getCell(i, columnIndex);
            if (count.containsKey(data)) {
                Integer v = count.get(data);
                v ++;
                count.put(data, v);
                List<Integer> list = index.get(data);
                list.add(i);
            } else {
                count.put(data, 1);
                List<Integer> list = Lists.newArrayList();
                list.add(i);
                index.put(data, list);
            }
        }

        HashMap<String, Double> hist = Maps.newHashMap();
        // normalize the ratio
        double pre = 0.0;
        for (String key : count.keySet()) {
            Integer v = count.get(key);
            double ratio = (double)v / (double)length + pre;
            hist.put(key, ratio);
            pre = ratio;
        }

        double random = Math.random();
        String selectedBin = null;
        for (String key : hist.keySet()) {
            double bound = hist.get(key);
            if (random < bound) {
                selectedBin = key;
                break;
            }
        }

        List<Integer> binIndex = index.get(selectedBin);

        return binIndex.get(0);
    }
}
