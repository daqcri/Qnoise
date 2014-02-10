/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import qa.qcri.qnoise.internal.NoiseType;

public class InjectorFactory {
    public static InjectorBase createInjector(NoiseType type) {
        switch (type) {
            case Missing:
                return new MissingInjector();
            case Duplicate:
                return new DuplicateInjector();
            case Inconsistency:
                return new InconsistencyInjector();
            case Outlier:
                return new OutlierInjector();
            case Error:
                return new ErrorNoiseInjector();
            default:
                throw new UnsupportedOperationException("Unknown noise type.");
        }
    }
}
