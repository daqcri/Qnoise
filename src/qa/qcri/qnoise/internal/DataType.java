/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

public enum DataType {
    Numerical,
    Text,
    Categorical;

    public static DataType fromString(String type) {
        if (type.equalsIgnoreCase("numerical")) {
            return DataType.Numerical;
        }

        if (type.equalsIgnoreCase("text")) {
            return DataType.Text;
        }

        if (type.equalsIgnoreCase("categorical")) {
            return DataType.Categorical;
        }

        throw new IllegalArgumentException("Unknown data type string " + type);
    }
}
