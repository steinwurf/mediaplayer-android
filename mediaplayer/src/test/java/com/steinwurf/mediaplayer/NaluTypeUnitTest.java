package com.steinwurf.mediaplayer;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class NaluTypeUnitTest {

    /**
     * A buffer containing the 3 byte ANNEX B start code
     */
    private static final byte[] ANNEX_B_START_CODE_3 = new byte[]{0x00, 0x00, 0x01};

    /**
     * A buffer containing the 4 byte ANNEX B start code
     */
    private static final byte[] ANNEX_B_START_CODE_4 = new byte[]{0x00, 0x00, 0x00, 0x01};

    @Test
    public void parseAnnexBStartCode_isCorrect() {

        assertTrue(NaluType.parseAnnexBStartCode(ANNEX_B_START_CODE_3));
        assertTrue(NaluType.parseAnnexBStartCode(ANNEX_B_START_CODE_4));

        ByteBuffer buffer1 = ByteBuffer.wrap(ANNEX_B_START_CODE_3);
        ByteBuffer buffer2 = ByteBuffer.wrap(ANNEX_B_START_CODE_4);


        assertTrue(NaluType.parseAnnexBStartCode(buffer1));
        assertEquals(3, buffer1.position());

        assertTrue(NaluType.parseAnnexBStartCode(buffer2));
        assertEquals(4, buffer2.position());

        ByteBuffer buffer3 = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00, 0x00, 0x01});
        assertFalse(NaluType.parseAnnexBStartCode(buffer3));
        assertEquals(0, buffer3.position());
        buffer3.position(1);
        assertTrue(NaluType.parseAnnexBStartCode(buffer3));
        assertEquals(5, buffer3.position());
        buffer3.position(2);
        assertTrue(NaluType.parseAnnexBStartCode(buffer3));
        assertEquals(5, buffer3.position());


        assertFalse(NaluType.parseAnnexBStartCode(buffer3.array()));
        assertTrue(NaluType.parseAnnexBStartCode(buffer3.array(), 1));
        assertTrue(NaluType.parseAnnexBStartCode(buffer3.array(), 2));
    }

    @Test(expected = java.nio.BufferUnderflowException.class)
    public void parseAnnexBStartCode_bufferFail() {
        ByteBuffer buffer3 = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00});
        assertFalse(NaluType.parseAnnexBStartCode(buffer3));
    }


    @Test
    public void parse_isCorrect() {
        {
            ByteBuffer keyframe = ByteBuffer.wrap(new byte[]{0x00, 0x00, 0x01, 0x65});
            assertEquals(NaluType.IdrSlice, NaluType.parse(keyframe.slice()));
            assertEquals(0, keyframe.position());
        }
        {
            ByteBuffer sps = ByteBuffer.wrap(new byte[]{0x00, 0x00, 0x01, 0x67});
            assertEquals(NaluType.SequenceParameterSet, NaluType.parse(sps));
            assertEquals(4, sps.position());
        }
        {
            ByteBuffer pps = ByteBuffer.wrap(new byte[] {0x00, 0x00, 0x00, 0x01, 0x68});
            assertEquals(NaluType.PictureParameterSet, NaluType.parse(pps));
            assertEquals(5, pps.position());
        }
    }
}
