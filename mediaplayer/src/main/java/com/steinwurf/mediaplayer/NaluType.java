package com.steinwurf.mediaplayer;
/*-
 * Copyright (c) 2017 Steinwurf ApS
 * All Rights Reserved
 *
 * Distributed under the "BSD License". See the accompanying LICENSE.rst file.
 */

import java.nio.ByteBuffer;

/**
 * NALU Type enum
 */
public enum NaluType {

        Unspecified0,
        NonIdrSlice,
        ASlice,
        BSlice,
        CSlice,
        IdrSlice,
        Sei,
        SequenceParameterSet,
        PictureParameterSet,
        AccessUnitDelimiter,
        EndOfSequence,
        EndOfStream,
        FillerData,
        SpsExtension,
        PrefixNalUnit,
        SubsetSequenceParameterSet,
        DepthParameterSet,
        Reserved17,
        Reserved18,
        AuxiliarySlice,
        Extension,
        DepthViewSlice,
        Reserved22,
        Reserved23,
        Unspecified24,
        Unspecified25,
        Unspecified26,
        Unspecified27,
        Unspecified28,
        Unspecified29,
        Unspecified30,
        Unspecified31,
        Unknown;

    /**
     * Check if given buffer begins with a ANNEX B start code
     * @param buffer the buffer to check.
     * @return true if the buffer does contain the start code, otherwise false.
     */
    public static boolean parseAnnexBStartCode(byte[] buffer) {
        return parseAnnexBStartCode(ByteBuffer.wrap(buffer, 0, buffer.length));
    }

    /**
     * Check if given buffer begins with a ANNEX B start code
     * @param buffer the buffer to check.
     * @param offset offset of the buffer
     * @return true if the buffer does contain the start code, otherwise false.
     */
    public static boolean parseAnnexBStartCode(byte[] buffer, int offset) {

        return parseAnnexBStartCode(ByteBuffer.wrap(buffer, offset, buffer.length - offset));
    }

    /**
     * Check if given buffer begins with a ANNEX B start code
     * @param buffer the buffer to check.
     * @return true and moves the buffer position if the buffer does contain the start code,
     * otherwise false.
     */
    public static boolean parseAnnexBStartCode(ByteBuffer buffer) {

        // Both
        // 0x000001 and,
        // 0x00000001 are valid NALU headers.
        // Checking
        // 0x0000 part
        int initialPosition = buffer.position();
        if (buffer.get() == 0x00 && buffer.get() == 0x00)
        {
            // Checking
            // 0x01, or
            // 0x0001 part
            byte byte3 = buffer.get();
            if (byte3 == 0x01 || (byte3 == 0x00 && buffer.get() == 0x01))
            {
                return true;
            }
        }
        buffer.position(initialPosition);
        return false;
    }



    /**
     * Parses the ANNEX B start code and returns the NALU type.
     * The position of the given ByteBuffer will be moved on a successful parse
     * @param buffer The buffer containing the NALU
     * @return the NALU type.
     */
    public static NaluType parse(ByteBuffer buffer)
    {
        if (!parseAnnexBStartCode(buffer))
            throw new IllegalArgumentException("NALU header not found");
        int typeId = (buffer.get() & 0x1F);
        return typeId > NaluType.values().length ? NaluType.Unknown : NaluType.values()[typeId];
    }
}
