package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder extends Decoder {

    private static final String TAG = "VideoDecoder";
    private static final String MIME = "video/avc";

    private static class H264HeaderCheckerWrapper implements SampleProvider
    {
        final SampleProvider sampleProvider;

        H264HeaderCheckerWrapper(SampleProvider sampleProvider)
        {
            this.sampleProvider = sampleProvider;
        }

        @Override
        public boolean hasSample() {
            return sampleProvider.hasSample();
        }

        @Override
        public Sample getSample() {
            Sample sample = sampleProvider.getSample();
            if (!NaluType.parseAnnexBStartCode(sample.data)) {
                Log.e(TAG, "Sample missing ANNEX B start code.");
            }
            return sample;
        }
    }

    /**
     * Returns a {@link VideoDecoder} or null upon failure.
     * @param width The maximum width of the video in pixels
     * @param height The maximum height of the video in pixels
     * @param sps The SPS buffer with a NALU header present.
     * @param pps The PPS buffer with a NALU header present.
     * @param sampleProvider The sample provider
     * @return {@link VideoDecoder}
     * @throws IllegalArgumentException If SPS or PPS buffers does not include NALU header.
     */
    public static VideoDecoder build(
            int width,
            int height,
            byte[] sps,
            byte[] pps,
            SampleProvider sampleProvider) throws IllegalArgumentException
    {
        if (!NaluType.parseAnnexBStartCode(sps)) {
            throw new IllegalArgumentException("SPS buffer does not begin with ANNEX B start code.");
        }
        if (!NaluType.parseAnnexBStartCode(pps)) {
            throw new IllegalArgumentException("PPS buffer does not begin with ANNEX B start code.");
        }

        MediaFormat format = MediaFormat.createVideoFormat(MIME, width, height);
        format.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, width * height);
        format.setInteger(MediaFormat.KEY_DURATION, Integer.MAX_VALUE);

        return new VideoDecoder(format, sampleProvider);
    }

    private VideoDecoder(MediaFormat format, SampleProvider sampleProvider) {
        super(format, MIME, new H264HeaderCheckerWrapper(sampleProvider));
    }

    /**
     * Sets the surface to decode the video onto. This needs to be set before
     * calling {@link VideoDecoder#start()}.
     * @param surface The surface to decode the video onto.
     */
    public void setSurface(Surface surface)
    {
        mSurface = surface;
    }


    /**
     * Starts the playback. Call this after {@link VideoDecoder#setSurface(Surface)} returns.
     */
    @Override
    public void start() {
        if (mSurface == null)
            throw new IllegalStateException();
        super.start();
    }

    @Override
    protected void render(MediaCodec decoder, MediaCodec.BufferInfo info, int outIndex) {
        decoder.releaseOutputBuffer(outIndex, true);
    }
}
