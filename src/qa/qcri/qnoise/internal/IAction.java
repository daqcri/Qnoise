/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.internal;

import java.util.HashMap;

public interface IAction<T> {
    public void act(T context, HashMap<String, Object> extras);
}
