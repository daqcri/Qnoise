/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Optional;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.constraint.ConstraintFactory;

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
    private Optional<Constraint> constraint;

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
        this.constraint = spec.constraint;
    }

    public static NoiseSpec valueOf(JSONObject jsonObject) {
        JSONObject sourceObj = (JSONObject)jsonObject.get("source");
        String inputFile = (String)sourceObj.get("path");
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

            tmp = noise.get("distance");
            spec.approximateDistance =
                    tmp == null ? Optional.<Double>absent() : Optional.of((double)tmp);

            tmp = noise.get("constraint");
            if (tmp == null) {
                spec.constraint = Optional.absent();
            } else {
                Constraint constraint =
                    ConstraintFactory.createConstraintFromString((String) tmp);
                spec.constraint = Optional.of(constraint);
            }

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

    public Optional<Constraint> getConstraint() {
        return constraint;
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

    public Optional<Double> getApproximateDistance() {
        return approximateDistance;
    }

    public Optional<String[]> getApproximateColumns() {
        return approximateCells;
    }
}
