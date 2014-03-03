/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Preconditions;
import qa.qcri.qnoise.inject.InjectorFactory;
import qa.qcri.qnoise.internal.*;
import qa.qcri.qnoise.util.NoiseHelper;

import java.util.List;

public class QnoiseFacade {
    public static void inject(DataProfile profile, List<NoiseSpec> specs) {
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(specs);

        for (NoiseSpec spec : specs) {
            new DataProcess(
                new NoiseContextBuilder()
                    .profile(profile)
                    .spec(spec)
                    .build()
            ).process(
                InjectorFactory.createInjector(spec.noiseType)
            );
        }
    }

    public static void inject(
        DataProfile profile,
        List<NoiseSpec> specs,
        IAction<NoiseContext> after
    ) {
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(specs);

        for (NoiseSpec spec : specs) {
            new DataProcess(
                new NoiseContextBuilder()
                    .profile(profile)
                    .spec(spec)
                    .build()
            ).process(
                InjectorFactory.createInjector(spec.noiseType)
            ).after(after);
        }
    }

    public static String verfiy(NoiseSpec spec) {
        return NoiseHelper.verify(spec);
    }
}
