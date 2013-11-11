/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Optional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    private Optional<Double> perc;
    private Optional<Double> duplicateSeed;
    private Optional<Double> duplicateTime;

    private Optional<Double> approximateDistance;
    private Optional<String[]> approximateCells;

    private String inputFile;
    private String outputFile;

    private NoiseType type;
    private NoiseGranularity granularity;
    private NoiseModel model;

    private NoiseSpec() {}
    public NoiseSpec(NoiseSpec spec) {
        this.perc = spec.getPerc();
        this.granularity = spec.getGranularity();
        this.model = spec.getModel();
        this.duplicateSeed = spec.getDuplicateSeedPerc();
        this.duplicateTime = spec.getDuplicateTimePerc();
        this.type = spec.type;
        this.inputFile = spec.inputFile;
        this.outputFile = spec.outputFile;
        this.approximateCells = spec.getApproximateColumns();
        this.approximateDistance = spec.getApproximateDistance();
    }

    public static class NoiseSpecBuilder {
        private NoiseGranularity granularity;
        private NoiseModel modal;

        private Optional<Double> perc;
        private Optional<Double> duplicateSeed;
        private Optional<Double> duplicateTime;

        private NoiseType type;
        private String inputFile;
        private String outputFile;

        public NoiseSpecBuilder perc(Double perc) {
            this.perc = Optional.of(perc);
            return this;
        }

        public NoiseSpecBuilder inputFile(String inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public NoiseSpecBuilder outputFile(String outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public NoiseSpecBuilder duplicateSeed(Double ds) {
            this.duplicateSeed = Optional.of(ds);
            return this;
        }

        public NoiseSpecBuilder duplicateTime(Double dt) {
            this.duplicateTime = Optional.of(dt);
            return this;
        }

        public NoiseSpecBuilder type(NoiseType type) {
            this.type = type;
            return this;
        }

        public NoiseSpecBuilder granularity(NoiseGranularity granularity) {
            this.granularity = granularity;
            return this;
        }

        public NoiseSpecBuilder modal(NoiseModel modal) {
            this.modal = modal;
            return this;
        }

        public NoiseSpec build() {
            NoiseSpec result = new NoiseSpec();
            result.perc = perc;
            result.granularity = granularity;
            result.model = modal;
            result.duplicateTime = duplicateTime;
            result.duplicateSeed = duplicateSeed;
            return result;
        }
    }

    public static NoiseSpec valueOf(JSONObject jsonObject) {
        String inputFile = (String)jsonObject.get("source");
        File file = new File(inputFile);

        JSONArray noises = (JSONArray)jsonObject.get("noises");
        NoiseSpec spec = new NoiseSpec();

        for (Object obj : noises) {
            JSONObject noise = (JSONObject)obj;
            NoiseType type = NoiseType.getGeneratorType((String) noise.get("type"));
            NoiseGranularity granularity =
                NoiseGranularity.fromString((String) noise.get("granularity"));
            NoiseModel model = NoiseModel.fromString((String) noise.get("model"));

            Object tmp = noise.get("percentage");
            Optional<Double> percentage =
                tmp == null ? Optional.<Double>absent() : Optional.of((double)tmp);

            tmp = noise.get("ns");
            Optional<Double> ns =
                tmp == null ? Optional.<Double>absent() : Optional.of((double)tmp);

            tmp = noise.get("nt");
            Optional<Double> nt =
                tmp == null ? Optional.<Double>absent() : Optional.of((double)tmp);

            tmp = noise.get("distant");
            Optional<Double> distant =
                tmp == null ? Optional.<Double>absent() : Optional.of((double)tmp);
            spec.approximateDistance = distant;

            tmp = noise.get("distant cells");
            if (tmp == null) {
                spec.approximateCells = Optional.<String[]>absent();
            } else {
                JSONArray jsonArray = (JSONArray)tmp;
                String[] cells = new String[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i ++) {
                    cells[i] = (String)jsonArray.get(i);
                }
                spec.approximateCells = Optional.of(cells);
            }

            spec.inputFile = file.getAbsolutePath();
            spec.type = type;
            spec.granularity = granularity;
            spec.model = model;
            spec.perc = percentage;
            spec.duplicateSeed = ns;
            spec.duplicateTime = nt;
        }

        return spec;
    }

    public Optional<Double> getPerc() {
        return perc;
    }

    public NoiseGranularity getGranularity() {
        return granularity;
    }

    public NoiseModel getModel() {
        return model;
    }

    public NoiseType getType() {
        return type;
    }

    public Optional<Double> getDuplicateTimePerc() {
        return duplicateTime;
    }

    public Optional<Double> getDuplicateSeedPerc() {
        return duplicateSeed;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public Optional<Double> getApproximateDistance() {
        return approximateDistance;
    }

    public Optional<String[]> getApproximateColumns() {
        return approximateCells;
    }
}
