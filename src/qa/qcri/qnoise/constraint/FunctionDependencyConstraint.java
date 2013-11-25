/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import com.google.common.collect.Sets;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.IndexGenerationBase;
import qa.qcri.qnoise.NoiseModel;
import qa.qcri.qnoise.util.NoiseHelper;
import qa.qcri.qnoise.util.Tracer;

import java.util.HashSet;

/**
 * FD Constraint.
 */
public class FunctionDependencyConstraint extends Constraint {
    private String leftHand;
    private String rightHand;
    private Tracer tracer = Tracer.getTracer(FunctionDependencyConstraint.class);

    public Constraint parse(String text) {
        String[] tokens = text.split("|");
        if (tokens.length != 2) {
            return null;
        }

        leftHand = tokens[0].trim();
        rightHand = tokens[1].trim();
        return this;
    }

    @Override
    public boolean isValid(DataProfile profile, int index) {
        int columnIndex = profile.getColumnIndex(leftHand);
        HashSet<String> set = Sets.newHashSet();
        for (int i = 0; i < profile.getLength(); i ++) {
            set.add(profile.getCell(i, columnIndex));
            if (set.size() > 1)
                return false;
        }
        return true;
    }

    @Override
    public void messIt(DataProfile profile, int index, double distance) {
        int columnIndex = profile.getColumnIndex(rightHand);
        String[] tuple = profile.getTuple(index);
        String currentValue = profile.getCell(index, columnIndex);

        IndexGenerationBase indexGen =
            IndexGenerationBase.createIndexStrategy(NoiseModel.RANDOM);

        int genIndex =
            indexGen.nextIndexWithoutReplacement(0, profile.getLength(), true);
        String nv;
        while (true) {
            if (genIndex == Integer.MIN_VALUE) {
                nv = tuple[columnIndex];
                break;
            }

            String selectedCell = profile.getCell(genIndex, columnIndex);
            if (currentValue != selectedCell) {
                nv = selectedCell;
                break;
            }
            genIndex = indexGen.nextIndexWithoutReplacement(0, profile.getLength(), false);
        }

        tracer.verbose(
            String.format(
                "[%d][%s] from %s to %s",
                index,
                rightHand,
                tuple[columnIndex],
                nv
            )
        );
        tuple[columnIndex] = nv;
        NoiseHelper.playTheJazz(distance, rightHand, profile, index);
    }
}
