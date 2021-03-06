package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;

public class AudioDecoder extends Decoder {

    private static final String TAG = "AudioDecoder";
    private static final String MIME = "audio/mp4a-latm";

    private static final int[] SAMPLE_RATES = new int[]
            {
                    96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
                    16000, 12000, 11025, 8000
            };

    /**
     * Returns a constructed AudioDecoder or null upon failure.
     * @param audioProfile The audio profile
     * @param sampleRateIndex The sample rate Index
     * @param channelCount The number of channels
     * @param sampleProvider The sample provider
     * @return a constructed AudioDecoder
     */
    public static AudioDecoder build(
            int audioProfile, int sampleRateIndex, int channelCount, SampleProvider sampleProvider)
    {
        int sampleRate = SAMPLE_RATES[sampleRateIndex];

        MediaFormat format = MediaFormat.createAudioFormat(MIME, sampleRate, channelCount);
        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put((byte) ((audioProfile << 3) | (sampleRateIndex >> 1)));
        csd.position(1);
        csd.put((byte) ((byte) ((sampleRateIndex << 7) & 0x80) | (channelCount << 3)));
        csd.flip();
        format.setByteBuffer("csd-0", csd); // add csd-0

        return new AudioDecoder(format, sampleProvider);
    }

    private ByteBuffer[] mOutputBuffers = null;
    private AudioTrack mAudioTrack = null;

    private AudioDecoder(MediaFormat format, SampleProvider sampleProvider) {
        super(format, MIME, sampleProvider);

        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(
                        sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM);
    }

    @Override
    public void start() {
        mAudioTrack.play();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        mAudioTrack.stop();
    }

    @Override
    protected void outputBuffersChanged(MediaCodec decoder) {
        super.outputBuffersChanged(decoder);
        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
        mOutputBuffers = decoder.getOutputBuffers();
    }

    @Override
    protected void outputFormatChanged(MediaCodec decoder) {
        super.outputFormatChanged(decoder);
        MediaFormat format = decoder.getOutputFormat();
        Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED format : " + format);
        mAudioTrack.setPlaybackRate(format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
    }

    @Override
    protected void render(MediaCodec decoder, MediaCodec.BufferInfo info, int outIndex) {
        if (mOutputBuffers == null)
            mOutputBuffers = decoder.getOutputBuffers();

        ByteBuffer outputBuffer = mOutputBuffers[outIndex];

        final byte[] chunk = new byte[info.size];
        outputBuffer.get(chunk);
        outputBuffer.clear();
        mAudioTrack.write(chunk, info.offset, chunk.length);
        decoder.releaseOutputBuffer(outIndex, false);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mAudioTrack.release();
    }
}
