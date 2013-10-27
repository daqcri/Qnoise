/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    private double perc;
    private NoiseGranularity granularity;
    private NoiseModal modal;

    private NoiseSpec() {}
    public NoiseSpec(NoiseSpec spec) {
        this.perc = spec.getPerc();
        this.granularity = spec.getGranularity();
        this.modal = spec.getModal();
    }

    public static class NoiseSpecBuilder {
        private double perc;
        private NoiseGranularity granularity;
        private NoiseModal modal;

        public NoiseSpecBuilder perc(double perc) {
            this.perc = perc;
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
            return result;
        }
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
}
