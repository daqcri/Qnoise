/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import org.javatuples.Pair;
import qa.qcri.qnoise.internal.DataProfile;
import qa.qcri.qnoise.internal.NoiseReport;
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
    private final int MAXTRY = 100;
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
        /*
            For FD, we change the left column value to make sure the violations.
         */
        int leftColumnIndex = profile.getColumnIndex(leftHand);
        int rightColumnIndex = profile.getColumnIndex(rightHand);

        String currentLeftValue = profile.getCell(index, leftColumnIndex);
        String currentRightValue = profile.getCell(index, rightColumnIndex);

        ModelBase indexGen =
            ModelFactory.createRandomModel();

        int genIndex =
            indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
        String nv;
        int tryCount = 0;
        while (true) {
            if (genIndex == Integer.MIN_VALUE) {
                tracer.info("There is no extra data to use to make noise.");
                return -1;
            }

            String selectedRightValue = profile.getCell(genIndex, rightColumnIndex);
            if (!selectedRightValue.equals(currentRightValue)) {
                nv = selectedRightValue;
                break;
            }
            tryCount ++;
            if (tryCount > MAXTRY) {
                nv = currentLeftValue;
                tracer.info("Cannot find possible replacement for FD.");
                break;
            }
            genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
        }

        Pair<Integer, Integer> indexPair = new Pair<>(index, rightColumnIndex);
        boolean isSuccess = profile.set(indexPair, nv);
        if (isSuccess) {
            tracer.infoChange(indexPair, currentRightValue, nv);
        } else {
            tracer.infoUnchange(indexPair);
        }

        profile.unmark(indexPair);
        if (isSuccess)
            NoiseHelper.playTheJazz(distance, leftHand, profile, index, report);
        profile.mark(indexPair);
        return rightColumnIndex;
    }
}
