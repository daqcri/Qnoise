/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.model.ModelBase;
import qa.qcri.qnoise.model.ModelFactory;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FD Constraint.
 */
public class FunctionDependencyConstraint extends Constraint {
    private String leftHand;
    private String rightHand;
    private static final Pattern pattern =
        Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*\\|\\s*([a-zA-Z]\\w*)\\s*");
    private Tracer tracer = Tracer.getTracer(FunctionDependencyConstraint.class);

    public Constraint parse(String text) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            return null;
        }

        leftHand = matcher.group(1);
        rightHand = matcher.group(2);
        return this;
    }

    @Override
    public boolean isValid(DataProfile profile, int index) {
        // For FD, it is always true by given one tuple.
        return true;
    }

    @Override
    public int messIt(DataProfile profile, int index, double distance, NoiseReport report) {
        int leftColumnIndex = profile.getColumnIndex(leftHand);
        int rightColumnIndex = profile.getColumnIndex(rightHand);

        String[] tuple = profile.getTuple(index);
        String currentLeftValue = profile.getCell(index, leftColumnIndex);

        ModelBase indexGen =
            ModelFactory.createRandomModel();

        int genIndex =
            indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
        String nv;
        while (true) {
            if (genIndex == Integer.MIN_VALUE) {
                tracer.info("There is no extra data to use to make noise.");
                return -1;
            }

            String selectedLeftValue = profile.getCell(genIndex, leftColumnIndex);
            String selectedRightValue = profile.getCell(genIndex, rightColumnIndex);
            if (!selectedLeftValue.equals(currentLeftValue)) {
                nv = selectedRightValue;
                break;
            }
            genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
        }

        tracer.verbose(
            String.format(
                "[%d][%s] from %s to %s",
                index,
                rightHand,
                tuple[rightColumnIndex],
                nv
            )
        );
        tuple[rightColumnIndex] = nv;
        NoiseHelper.playTheJazz(distance, rightHand, profile, index, report);
        return rightColumnIndex;
    }
}
