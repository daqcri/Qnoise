/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import org.apache.commons.cli.CommandLine;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    private double perc;
    private int duplicateSeed;
    private int duplicateTime;
    private NoiseGranularity granularity;
    private NoiseModal modal;


    private NoiseSpec() {}
    public NoiseSpec(NoiseSpec spec) {
        this.perc = spec.getPerc();
        this.granularity = spec.getGranularity();
        this.modal = spec.getModal();
        this.duplicateSeed = spec.getDuplicateSeed();
        this.duplicateTime = spec.getDuplicateTime();
    }

    public static class NoiseSpecBuilder {
        private double perc;
        private NoiseGranularity granularity;
        private NoiseModal modal;
        private int duplicateSeed;
        private int duplicateTime;

        public NoiseSpecBuilder perc(double perc) {
            this.perc = perc;
            return this;
        }

        public NoiseSpecBuilder duplicateSeed(int ds) {
            this.duplicateSeed = ds;
            return this;
        }

        public NoiseSpecBuilder duplicateTime(int dt) {
            this.duplicateTime = dt;
            return this;
        }

        public NoiseSpecBuilder granularity(NoiseGranularity granularity) {
            this.granularity = granularity;
            return this;
        }

        public NoiseSpecBuilder modal(NoiseModal modal) {
            this.modal = modal;
            return this;
        }

        public NoiseSpec build() {
            NoiseSpec result = new NoiseSpec();
            result.perc = perc;
            result.granularity = granularity;
            result.modal = modal;
            result.duplicateTime = duplicateTime;
            result.duplicateSeed = duplicateSeed;
            return result;
        }
    }

    public static NoiseSpec valueOf(CommandLine line) {
        NoiseSpec spec = new NoiseSpec();
        if (line.hasOption("p")) {
            spec.perc = Double.parseDouble(line.getOptionValue("p"));
        }

        if (line.hasOption("m")) {
            spec.modal = NoiseModal.getNoiseModal(line.getOptionValue("m"));
        }

        if (line.hasOption("g")) {
            spec.granularity =
                NoiseGranularity.getNoiseGranularity(line.getOptionValue("g"));
        }

        if (line.hasOption("ns")) {
            spec.duplicateSeed = Integer.parseInt(line.getOptionValue("ns"));
        }

        if (line.hasOption("nd")) {
            spec.duplicateTime = Integer.parseInt(line.getOptionValue("nd"));
        }
        return spec;
    }

    public double getPerc() {
        return perc;
    }

    public void setPerc(double perc) {
        this.perc = perc;
    }

    public NoiseGranularity getGranularity() {
        return granularity;
    }

    public void setGranularity(NoiseGranularity granularity) {
        this.granularity = granularity;
    }

    public NoiseModal getModal() {
        return modal;
    }

    public void setModal(NoiseModal modal) {
        this.modal = modal;
    }

    public int getDuplicateTime() {
        return duplicateTime;
    }

    public void setDuplicateTime(int duplicateTime) {
        this.duplicateTime = duplicateTime;
    }

    public int getDuplicateSeed() {
        return duplicateSeed;
    }

    public void setDuplicateSeed(int duplicateSeed) {
        this.duplicateSeed = duplicateSeed;
    }
}
