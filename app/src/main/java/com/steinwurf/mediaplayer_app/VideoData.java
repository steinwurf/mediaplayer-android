package com.steinwurf.mediaplayer_app;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

import android.content.Context;
import android.content.res.Resources;

import com.steinwurf.mediaplayer.NaluType;
import com.steinwurf.mediaplayer.Sample;
import com.steinwurf.mediaplayer.SampleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class providing the video data.
 * The content is extracted from a real video file using the mp4 parsing library petro
 * https://github.com/steinwurf/petro
 * Android bindings are available here:
 * https://github.com/steinwurf/petro-android
 */
public class VideoData implements SampleProvider
{
    private static byte[] toBytes(int... ints) {
        byte[] result = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[i] = (byte) ints[i];
        }
        return result;
    }

    private final static int[] nalusPerSample = {
            5,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
    };

    private final static long mediaDuration = 5588888;
    private final static int[] timestamps = {
            33333, 66666, 100000, 133333, 166666, 200000, 233333, 266666, 300000, 333333, 366666,
            400000, 433333, 466666, 500000, 533333, 566666, 600000, 633333, 666666, 700000, 733333,
            766666, 800000, 833333, 866666, 900000, 933333, 966666, 1000000, 1033333, 1066666,
            1100000, 1133333, 1166666, 1200000, 1233333, 1266666, 1300000, 1333333, 1366666,
            1400000, 1433333, 1466666, 1500000, 1533333, 1566666, 1600000, 1633333, 1666666,
            1700000, 1733333, 1766666, 1800000, 1833333, 1866666, 1900000, 1933333, 1966666,
            2000000, 2033333, 2066666, 2100000, 2133333, 2166666, 2200000, 2233333, 2266666,
            2300000, 2333333, 2366666, 2400000, 2433333, 2466666, 2500000, 2533333, 2566666,
            2600000, 2633333, 2666666, 2700000, 2733333, 2766666, 2800000, 2833333, 2866666,
            2900000, 2933333, 2966666, 3000000, 3033333, 3066666, 3100000, 3133333, 3166666,
            3200000, 3233333, 3266666, 3300000, 3333333, 3366666, 3400000, 3433333, 3466666,
            3500000, 3533333, 3566666, 3600000, 3633333, 3666666, 3700000, 3733333, 3766666,
            3800000, 3833333, 3866666, 3900000, 3933333, 3966666, 4000000, 4033333, 4066666,
            4100000, 4133333, 4166666, 4200000, 4233333, 4266666, 4300000, 4333333, 4366666,
            4400000, 4433333, 4466666, 4500000, 4533333, 4566666, 4600000, 4633333, 4666666,
            4700000, 4733333, 4766666, 4800000, 4833333, 4866666, 4900000, 4933333, 4966666,
            5000000, 5033333, 5066666, 5100000, 5133333, 5166666, 5200000, 5233333, 5266666,
            5300000, 5333333, 5366666, 5400000, 5433333, 5466666, 5500000, 5533333
    };

    final byte[] sps = toBytes(
            0x00, 0x00, 0x00, 0x01, 0x67, 0x42, 0xC0, 0x1e, 0xd9, 0x00, 0x8c, 0x29, 0xb0,
            0x11, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0x03, 0x00, 0x3c, 0x8f, 0x16, 0x2e,
            0x48);

    final byte[] pps = toBytes(0x00, 0x00, 0x00, 0x01, 0x68, 0xcb, 0x8c, 0xb2);

    final int width = 560;
    final int height = 320;

    private final ByteBuffer data;

    private int sampleCount = 0;
    private long timestampOffset = 0;

    VideoData(Context context)
    {
        Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(R.raw.samples);
        byte[] data;
        try {
            data = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Unable to read resource file");
        }
        this.data = ByteBuffer.wrap(data);
    }

    @Override
    public boolean hasSample() {
        return true;
    }

    @Override
    public Sample getSample() {
        int NALUs = 0;
        int offset = data.position();
        while(data.hasRemaining())
        {
            data.mark();
            if (NaluType.parseAnnexBStartCode(data))
            {
                NALUs++;
                if (nalusPerSample[sampleCount] < NALUs) {
                    data.reset();
                    break;
                }
            }
            else
            {
                data.position(data.position() + 1);
            }
        }
        long timestamp = timestamps[sampleCount] + timestampOffset;
        byte[] buffer = Arrays.copyOfRange(data.array(), offset, data.position());
        sampleCount++;
        Sample sample = new Sample(timestamp, buffer);
        if (!data.hasRemaining())
        {
            sampleCount = 0;
            timestampOffset += mediaDuration;
            data.clear();
        }
        return sample;
    }
}
