package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SampleUnitTest {
    @Test
    public void constructor_isCorrect() {

        long timestamp = 1337;
        byte [] data = new byte[]{0x01, 0x03, 0x03, 0x07};
        Sample sample = new Sample(timestamp, data);

        assertEquals(timestamp, sample.timestamp);
        assertEquals(data, sample.data);
    }
}
