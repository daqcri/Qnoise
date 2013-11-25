/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.constraint;

/**
 * ConstraintFactory.
 */
public class ConstraintFactory {
    public static Constraint createConstraintFromString(String input) {
        Constraint constraint = new PredicateConstraint().parse(input);
        if (constraint != null) {
            return constraint;
        }

        constraint = new FunctionDependencyConstraint().parse(input);
        if (constraint != null) {
            return constraint;
        }
        throw new IllegalArgumentException("Given text is not a known constraint.");
    }
}
