/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DataProcess {
    private HashMap<String, Object> extras;
    private NoiseContext context;

    public DataProcess(
        @NotNull NoiseContext context,
        @NotNull HashMap<String, Object> extras
    ) {
        this.context = context;
        this.extras = extras;
    }

    public DataProcess(
        @NotNull NoiseContext context
    ) {
        this.context = context;
        this.extras = null;
    }

    public DataProcess context(
        @NotNull NoiseContext context
    ) {
        this.context = context;
        return this;
    }

    public DataProcess before(@NotNull IAction<NoiseContext> beforeAction) {
        beforeAction.act(context, extras);
        return this;
    }

    public DataProcess process(@NotNull IAction<NoiseContext> runAction) {
        runAction.act(context, extras);
        return this;
    }

    public DataProcess after(@NotNull IAction<NoiseContext> afterAction) {
        afterAction.act(context, extras);
        return this;
    }
}
