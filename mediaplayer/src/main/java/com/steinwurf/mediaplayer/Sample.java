package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

public class Sample
{
    /**
     * The timestamp in microseconds
     */
    final long timestamp;

    /**
     * The data buffer
     */
    final byte[] data;

    /**
     * Constructs a sample
     * @param timestamp timestamp in microseconds
     * @param data data buffer
     */
    public Sample(long timestamp, byte[] data)
    {
        this.timestamp = timestamp;
        this.data = data;
    }
}
