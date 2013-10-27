/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import qa.qcri.qnoise.util.Tracer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NoiseReport {
    private static Map<Metric, List<Object>> stats = Maps.newHashMap();

    public enum Metric {
        Modal,
        Percentage,
        Granularity,
        ChangedItem,
        InjectionTime,
        OutputRow
    }

    public NoiseReport(NoiseSpec spec) {
        appendMetric(Metric.Modal, spec.getModal().toString());
        appendMetric(Metric.Percentage, spec.getPerc());
        appendMetric(Metric.Granularity, spec.getGranularity().toString());
    }

    /**
     * Appends values to the trace statistic metric.
     */
    public synchronized void appendMetric(Metric metric, Object value) {
        if (stats.containsKey(metric)) {
            List<Object> metrics = stats.get(metric);
            metrics.add(value);
        } else {
            List<Object> values = Lists.newArrayList();
            values.add(value);
            stats.put(metric, values);
        }
    }

    /**
     * Accumulate values in the trace statistic entry.
     */
    public synchronized void addMetric(Metric metric, long value) {
        if (!stats.containsKey(metric)) {
            appendMetric(metric, value);
        } else {
            List<Object> values = stats.get(metric);
            if (values.size() > 1) {
                throw new IllegalStateException(
                    "Entry " + metric + " is found more than once in the report."
                );
            }
            Long newValue = (Long)values.get(0) + value;
            values.set(0, newValue);
        }
    }

    /**
     * Print Noise generation result.
     */
    public void print() {
        Tracer tracer = Tracer.getTracer(Tracer.class);
        tracer.info("Noise Generation report:");
        tracer.info("----------------------------------------------------------------");
        tracer.info(formatMetric(Metric.Modal, "Modal"));
        tracer.info(formatMetric(Metric.Percentage, "Percentage"));
        tracer.info(formatMetric(Metric.Granularity, "Granularity"));
        tracer.info(formatMetric(Metric.ChangedItem, "Changed item"));
        tracer.info(formatMetric(Metric.InjectionTime, "Null injection time", "ms"));
        tracer.info(formatMetric(Metric.OutputRow, "New file row count"));
        tracer.info("----------------------------------------------------------------");
    }

    private static String formatMetric(Metric type, String title) {
        return formatMetric(type, title, "");
    }

    private static String formatMetric(Metric type, String title, String suffix) {
        String value;
        if (!stats.containsKey(type)) {
            value = "";
        } else {
            Collection<Object> metrics = stats.get(type);
            StringBuilder outputBuilder = new StringBuilder(50);
            for (Object metric : metrics) {
                if (metric instanceof String) {
                    outputBuilder.append(String.format("%s", metric));
                } else if (metric instanceof Long) {
                    outputBuilder.append(String.format("%-9d", metric));
                } else if (metric instanceof Double) {
                    outputBuilder.append(String.format("%-9.2f", metric));
                }
            }
            value = outputBuilder.toString();
        }
        if (!Strings.isNullOrEmpty(suffix)) {
            title = title + " (" + suffix + ")";
        }
        return String.format("%-40s %s", title, value);
    }
}
