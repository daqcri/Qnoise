/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

import qa.qcri.qnoise.DataProfile;

/**
 * Constraint class for inconsistency.
 */
public abstract class Constraint {
    public abstract Constraint parse(String text);

    public abstract boolean isValid(DataProfile profile, int index);

    public abstract void messIt(DataProfile profile, int index, double distance);
}
