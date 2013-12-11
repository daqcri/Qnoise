/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.test.model;

import org.junit.Assert;
import org.junit.Test;
import qa.qcri.qnoise.model.RandomModel;

public class RandomModelTest {
    @Test
    public void testRandom() {
        RandomModel model = new RandomModel();
        Assert.assertEquals(Integer.MIN_VALUE, model.nextIndex(0, 0));
        Assert.assertEquals(Integer.MIN_VALUE, model.nextIndex(0, -1));
        Assert.assertNotEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(0, 3, true));
        Assert.assertNotEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(0, 3, false));
        Assert.assertNotEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(0, 3, false));
        Assert.assertEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(0, 3, false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        RandomModel model = new RandomModel();
        Assert.assertNotEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(0, 3, true));
        Assert.assertNotEquals(Integer.MIN_VALUE, model.nextIndexWithoutReplacement(1, 3, false));
    }
}
