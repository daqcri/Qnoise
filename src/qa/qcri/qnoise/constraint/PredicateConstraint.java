/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import org.javatuples.Pair;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.DataType;
import qa.qcri.qnoise.internal.NoiseReport;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.Tracer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Error Predicate constraint (
 */
public class PredicateConstraint extends Constraint {
    private String leftHand;
    private String operator;
    private Double rightValue;
    private static final Pattern pattern =
        Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*(>|<|<=|>=|=|!=)\\s*(\\d+)\\s*");
    private static Tracer tracer = Tracer.getTracer(PredicateConstraint.class);


    @Override
    public PredicateConstraint parse(String text) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            return null;
        }

        leftHand = matcher.group(1);
        operator = matcher.group(2);
        rightValue = Double.parseDouble(matcher.group(3));
        return this;
    }

    @Override
    public boolean isValid(DataProfile profile, int index) {
        String[] tuple = profile.getReadOnlyTuple(index);
        int columnIndex = profile.getColumnIndex(leftHand);
        DataType type = profile.getType(columnIndex);
        if (type != DataType.Numerical) {
            throw new IllegalArgumentException("The given index is not a numerical type.");
        }

        Double value = Double.parseDouble(tuple[columnIndex]);
        boolean result;
        switch (operator) {
            case ">":
                result = value > rightValue;
                break;
            case "<":
                result = value < rightValue;
                break;
            case "=":
                result = value.equals(rightValue);
                break;
            case ">=":
                result = value >= rightValue;
                break;
            case "<=":
                result = value <= rightValue;
                break;
            case "!=":
                result = !value.equals(rightValue);
                break;
            default:
                throw new UnsupportedOperationException("Unknown operator " + operator);
        }
        return result;
    }

    @Override
    public int messIt(DataProfile profile, int rowIndex, double distance, NoiseReport report)
            throws IllegalArgumentException {
        int columnIndex = profile.getColumnIndex(leftHand);
        DataType type = profile.getType(columnIndex);
        if (type != DataType.Numerical) {
            throw new IllegalArgumentException("The given index is not a numerical type.");
        }
        ModelBase indexGen = ModelFactory.createRandomModel();

        double nv;
        int genIndex;
        switch (operator) {
            case ">":
            case ">=":
                genIndex =
                    indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
                while (true) {
                    if (genIndex == Integer.MIN_VALUE) {
                        throw new RuntimeException(
                            "Cannot find solution given the specification."
                        );
                    }

                    nv = profile.getDouble(genIndex, columnIndex);
                    if (nv < rightValue)
                        break;
                    genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
                }
                break;
            case "<":
            case "<=":
                genIndex =
                    indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
                while (true) {
                    if (genIndex == Integer.MIN_VALUE) {
                        nv = Integer.MAX_VALUE;
                        break;
                    }

                    nv = profile.getDouble(genIndex, columnIndex);
                    if (nv > rightValue)
                        break;
                    genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
                }
                break;
            case "=":
                genIndex =
                    indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
                while (true) {
                    if (genIndex == Integer.MIN_VALUE) {
                        nv = Integer.MAX_VALUE;
                        break;
                    }

                    nv = profile.getDouble(genIndex, columnIndex);
                    if (nv == rightValue)
                        break;
                    genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
                }
                break;
            case "!=":
                nv = rightValue;
                break;
            default:
                throw new UnsupportedOperationException("Unknown operator.");
        }

        if (distance != 0.0) {
            double sign = indexGen.nextIndex(0, 2) > 1 ? 1.0 : -1.0;
            nv += distance * profile.getStandardDeviationOn(leftHand) * sign * 0.01;
        }

        String result = Double.toString(nv);
        Pair<Integer, Integer> cellIndex = new Pair<>(rowIndex, columnIndex);
        tracer.verbose(
            String.format(
                "[%d][%s] from %s to %s",
                rowIndex,
                leftHand,
                profile.getCell(cellIndex),
                result
            )
        );

        report.logChange(cellIndex, profile.getCell(cellIndex), result);
        profile.set(cellIndex, result);
        return columnIndex;
    }
}
