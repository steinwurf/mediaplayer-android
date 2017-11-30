package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

public interface SampleProvider {

    /**
     * Returns true if a {@link Sample} is available.
     * @return true if a {@link Sample} is available.
     */
    boolean hasSample();

    /**
     * Returns the next {@link Sample}. The next call will return the subsequent {@link Sample}.
     * @return the next {@link Sample}.
     */
    Sample getSample();
}
