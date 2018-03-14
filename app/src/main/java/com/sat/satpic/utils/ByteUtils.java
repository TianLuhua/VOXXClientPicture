package com.sat.satpic.utils;

/**
 * Created by Tianluhua on 2018/3/14.
 */

public class ByteUtils {
    public static int bufferToInt(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF) | ((src[1] & 0xFF) << 8) | ((src[2] & 0xFF) << 16));
        return value;
    }
}
