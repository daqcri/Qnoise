/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import qa.qcri.qnoise.util.Pair;
import qa.qcri.qnoise.util.Tracer;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NoiseReport {
    private static Map<Metric, List<Object>> stats = Maps.newHashMap();
    private List<Pair<Pair<Integer, Integer>, String>> logBook = Lists.newArrayList();

    public enum Metric {
        Type,
        Model,
        Percentage,
        PercentageOfSeed,
        PercentageOfDuplicate,
        InputRow,
        OutputRow,
        Granularity,
        ChangedItem,
        InjectionTime,
        InjectionTimestamp,
        InputFilePath,
        OutputFilePath,
        Schema
    }

    public NoiseReport(NoiseSpec spec) {
        appendMetric(Metric.Type, spec.getValue(NoiseSpec.SpecEntry.NoiseType));
        appendMetric(Metric.Model, spec.getValue(NoiseSpec.SpecEntry.Model));
        appendMetric(Metric.Percentage, spec.getValue(NoiseSpec.SpecEntry.Percentage));
        appendMetric(Metric.Granularity, spec.getValue(NoiseSpec.SpecEntry.Granularity));
        appendMetric(Metric.PercentageOfSeed, spec.getValue(NoiseSpec.SpecEntry.NumberOfSeed));
        appendMetric(Metric.InjectionTimestamp, new Timestamp(new Date().getTime()).toString());
        appendMetric(Metric.InputFilePath, spec.getValue(NoiseSpec.SpecEntry.InputFile));
    }

    public synchronized void logChange(int i, int j, String value) {
        Pair<Integer, Integer> pair = new Pair<>(i, j);
        logBook.add(new Pair<>(pair, value));
    }

    public List<Pair<Pair<Integer, Integer>, String>> getLogBook() {
        return logBook;
    }

    /**
     * Appends values to the trace statistic metric.
     */
    public synchronized void appendMetric(Metric metric, Object value) {
        if (value == null) {
            return;
        }

        Object obj = value;
        if (value instanceof Optional) {
            Optional optional = (Optional)value;
            if (!optional.isPresent()) {
                return;
            }
            obj = optional.get();
        }

        if (stats.containsKey(metric)) {
            List<Object> metrics = stats.get(metric);
            metrics.add(obj);
        } else {
            List<Object> values = Lists.newArrayList();
            values.add(obj);
            stats.put(metric, values);
        }
    }

    /**
     * Accumulate values in the trace statistic entry.
     */
    public synchronized <T> void addMetric(Metric metric, T value) {
        if (!stats.containsKey(metric)) {
            appendMetric(metric, value);
        } else {
            List<Object> values = stats.get(metric);
            if (values.size() > 1) {
                throw new IllegalStateException(
                    "Entry " + metric + " is found more than once in the report."
                );
            }

            if (value instanceof Integer)
                values.set(0, (Integer)values.get(0) + (Integer)value);
            else if (value instanceof Double)
                values.set(0, (Double)values.get(0) + (Double)value);
            else if (value instanceof Long)
                values.set(0, (Long)values.get(0) + (Long)value);
            else
                throw new IllegalArgumentException("Unknown addition type.");
        }
    }

    /**
     * Print Noise generation result.
     */
    public void print() {
        Tracer tracer = Tracer.getTracer(Tracer.class);
        tracer.info("Noise Generation report:");
        tracer.info("----------------------------------------------------------------");
        tracer.info(formatMetric(Metric.Type, "Type"));
        tracer.info(formatMetric(Metric.Model, "Model"));
        tracer.info(formatMetric(Metric.Granularity, "Granularity"));
        tracer.info(formatMetric(Metric.Percentage, "Noise Percentage"));
        tracer.info(formatMetric(Metric.PercentageOfSeed, "Percentage Seeds"));
        tracer.info(formatMetric(Metric.PercentageOfDuplicate, "Percentage Duplicate"));
        tracer.info(formatMetric(Metric.ChangedItem, "Changed item"));
        tracer.info(formatMetric(Metric.InjectionTime, "Injection time", "ms"));
        tracer.info(formatMetric(Metric.InputRow, "Original file record number"));
        tracer.info(formatMetric(Metric.OutputRow, "Output file record number"));
        tracer.info(formatMetric(Metric.InjectionTimestamp, "Injection Timestamp"));
        tracer.info(formatMetric(Metric.InputFilePath, "Input file path"));
        tracer.info(formatMetric(Metric.OutputFilePath, "Output file path"));
        tracer.info(formatMetric(Metric.Schema, "Schema"));
        tracer.info("----------------------------------------------------------------");
    }

    private static String formatMetric(Metric type, String title) {
        return formatMetric(type, title, "");
    }

    private static String formatMetric(Metric type, String title, String suffix) {
        String result;
        if (!stats.containsKey(type)) {
            result = null;
        } else {
            Collection<Object> metrics = stats.get(type);
            StringBuilder outputBuilder = new StringBuilder(50);
            for (Object metric : metrics) {
                if (metric instanceof String) {
                    outputBuilder.append(String.format("%s", metric));
                } else if (metric instanceof Long || metric instanceof Integer) {
                    outputBuilder.append(String.format("%-9d", metric));
                } else if (metric instanceof Double) {
                    outputBuilder.append(String.format("%-9.2f", metric));
                }
            }
            result = outputBuilder.toString();
        }

        if (result != null) {
            if (!Strings.isNullOrEmpty(suffix)) {
                title = title + " (" + suffix + ")";
            }
            result = String.format("%-40s %s", title, result);
        }
        return result;
    }
}
