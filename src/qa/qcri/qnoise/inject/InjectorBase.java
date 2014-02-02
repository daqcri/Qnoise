/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.inject;

import org.jetbrains.annotations.NotNull;
import qa.qcri.qnoise.DataProfile;
import qa.qcri.qnoise.NoiseReport;
import qa.qcri.qnoise.NoiseSpec;

public abstract class InjectorBase {
    public abstract InjectorBase inject(
        @NotNull NoiseSpec spec,
        @NotNull DataProfile profile,
        @NotNull NoiseReport report
    );
}
