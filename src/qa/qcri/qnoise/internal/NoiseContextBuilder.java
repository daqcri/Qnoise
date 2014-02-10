/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Date;

public class NoiseContextBuilder {
    DataProfile profile;
    NoiseReport report;
    NoiseSpec spec;

    public NoiseContextBuilder profile(@NotNull DataProfile profile) {
        this.profile = profile;
        return this;
    }

    public NoiseContextBuilder spec(@NotNull NoiseSpec spec) {
        this.spec = spec;
        this.report = new NoiseReport();
        return this;
    }

    public NoiseContext build() {
        NoiseContext context = new NoiseContext();
        context.profile = this.profile;
        context.report = this.report;
        context.spec = this.spec;

        this.report.appendMetric(NoiseReport.Metric.Type, spec.noiseType);
        this.report.appendMetric(NoiseReport.Metric.Model, spec.model);
        this.report.appendMetric(NoiseReport.Metric.Percentage, spec.percentage);
        this.report.appendMetric(NoiseReport.Metric.Granularity, spec.granularity);
        this.report.appendMetric(NoiseReport.Metric.PercentageOfSeed, spec.numberOfSeed);
        this.report.appendMetric(
            NoiseReport.Metric.InjectionTimestamp,
            new Timestamp(new Date().getTime()).toString()
        );
        this.report.appendMetric(NoiseReport.Metric.LogFile, spec.logFile);

        return context;
    }
}
