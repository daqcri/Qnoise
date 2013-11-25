/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.DataType;
import qa.qcri.qnoise.IndexGenerationBase;
import qa.qcri.qnoise.NoiseModel;
import qa.qcri.qnoise.util.Tracer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Predicate constraint (
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
        String[] tuple = profile.getTuple(index);
        int columnIndex = profile.getColumnIndex(leftHand);
        DataType type = profile.getType(columnIndex);
        if (type != DataType.NUMERICAL) {
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
                result = value == rightValue;
                break;
            case ">=":
                result = value >= rightValue;
                break;
            case "<=":
                result = value <= rightValue;
                break;
            case "!=":
                result = value != rightValue;
                break;
            default:
                throw new UnsupportedOperationException("Unknown operator " + operator);
        }
        return result;
    }

    @Override
    public void messIt(
        DataProfile profile,
        int index,
        double distance
    ) throws IllegalArgumentException{
        String[] tuple = profile.getTuple(index);
        int columnIndex = profile.getColumnIndex(leftHand);
        DataType type = profile.getType(columnIndex);
        if (type != DataType.NUMERICAL) {
            throw new IllegalArgumentException("The given index is not a numerical type.");
        }
        IndexGenerationBase indexGen =
            IndexGenerationBase.createIndexStrategy(NoiseModel.RANDOM);

        double nv = 0.0;
        int genIndex;
        switch (operator) {
            case ">":
            case ">=":
                genIndex =
                    indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
                while (true) {
                    if (genIndex == Integer.MIN_VALUE) {
                        nv = Integer.MIN_VALUE;
                        break;
                    }

                    String[] selectedTuple = profile.getTuple(genIndex);
                    nv = Double.parseDouble(selectedTuple[columnIndex]);
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

                    String[] selectedTuple = profile.getTuple(genIndex);
                    nv = Double.parseDouble(selectedTuple[columnIndex]);
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

                    String[] selectedTuple = profile.getTuple(genIndex);
                    nv = Double.parseDouble(selectedTuple[columnIndex]);
                    if (nv != rightValue)
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
        tracer.verbose(
            String.format(
                "[%d][%s] from %s to %s",
                index,
                leftHand,
                tuple[columnIndex],
                result
            )
        );
        tuple[columnIndex] = result;

    }
}
