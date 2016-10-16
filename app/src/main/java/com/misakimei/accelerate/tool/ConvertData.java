package com.misakimei.accelerate.tool;


import android.support.annotation.NonNull;

import com.misakimei.accelerate.geom.Point;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;

public class ConvertData {
    private static int intTobytes(byte[] data, int start, int source) {
        data[start + 3] = (byte) (source >> 24);
        data[start + 2] = (byte) (source >> 16);
        data[start + 1] = (byte) (source >> 8);
        data[start + 0] = (byte) (source);
        return start + 4;
    }

    public static int bytesToInt(byte[] data, int start) {
        return (data[start + 3] & 0xFF) << 24 | (data[start + 2] & 0xFF) << 16 | (data[start + 1] & 0xFF) << 8 | (data[start] & 0xFF);
    }

    public static int FloatTobytes(byte[] data, int start, float source) {
        int fbits = Float.floatToIntBits(source);
        return intTobytes(data, start, fbits);
    }

    public static float bytesToFloat(byte[] data, int start) {
        return Float.intBitsToFloat(bytesToInt(data, start));
    }

    public static int IntTobytes(byte[] data, int start, int source) {
        return intTobytes(data, start, source);
    }


    /**
     * 将data数组转换成float数组
     *
     * @param data
     * @return
     */
    @NonNull
    public static float[] tofloats(@NonNull byte[] data) {
        return tofloats(data, 0, data.length / 4);
    }

    @NonNull
    public static float[] tofloats(byte[] data, int start, int num) {
        float[] res = new float[num];
        for (int i = 0; i < num; i++) {
            res[i] = bytesToFloat(data, start);
            start += 4;
        }
        return res;
    }

    @NonNull
    public static byte[] tobytes(int v) {
        byte[] data = new byte[4];
        intTobytes(data, 0, v);
        return data;
    }

    @NonNull
    public static byte[] tobytes(@NonNull int[] ints) {
        byte[] data = new byte[ints.length * 4];
        for (int i = 0; i < ints.length; i++) {
            intTobytes(data, i * 4, ints[i]);
        }
        return data;
    }

    @NonNull
    public static byte[] tobytes(float source) {
        byte[] data = new byte[4];
        int fbits = Float.floatToIntBits(source);
        intTobytes(data, 0, fbits);
        return data;
    }

    @NonNull
    public static byte[] tobytes(@NonNull float[] data) {
        byte[] res = new byte[data.length * 4];
        int start = 0;
        for (int i = 0; i < data.length; i++) {
            start = FloatTobytes(res, start, data[i]);
        }
        return res;
    }

    @NonNull
    public static byte[] tobytes(@NonNull ArrayList<Byte> list) {
        byte[] data = new byte[list.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = list.get(i);
        }
        return data;
    }

    public static byte[] tobytes(@NonNull LinkedList<byte[]> list) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] v : list) {
            out.write(v);
        }
        return out.toByteArray();
    }


    @NonNull
    public static ArrayList<Byte> getByteList(@NonNull float[] data) {
        ArrayList<Byte> list = new ArrayList<>();
        byte[] temp = new byte[4];
        for (int i = 0; i < data.length; i++) {
            FloatTobytes(temp, 0, data[i]);
            for (int j = 0; j < 4; j++) {
                list.add(new Byte(temp[j]));
            }
        }
        return list;
    }

    @NonNull
    public static ArrayList<Byte> getByteList(@NonNull int[] data) {
        ArrayList<Byte> list = new ArrayList<>();
        byte[] temp = new byte[4];
        for (int i = 0; i < data.length; i++) {
            IntTobytes(temp, 0, data[i]);
            for (int j = 0; j < 4; j++) {
                list.add(new Byte(temp[j]));
            }
        }
        return list;
    }

    @NonNull
    public static int[] getIntArray(byte[] data, int start, int num) {
        int[] res = new int[num];
        for (int i = 0; i < num; i++) {
            res[i] = bytesToInt(data, start);
            start += 4;
        }
        return res;
    }


    public static byte[] getANSCII(@NonNull String msg) {
        return msg.getBytes(StandardCharsets.US_ASCII);
    }


    public static int getInt(@NonNull InputStream in) throws IOException {
        byte[] data = new byte[4];
        in.read(data);
        return bytesToInt(data, 0);
    }


    /**
     * 从流中 读取 len个byte 并转换成int[]
     *
     * @param in
     * @param len
     * @return
     */
    @NonNull
    public static int[] getIntArray(@NonNull InputStream in, int len) throws IOException {
        byte[] data = new byte[len];
        in.read(data);
        return getIntArray(data);
    }

    @NonNull
    public static int[] getIntArray(@NonNull byte[] data) {
        return getIntArray(data, 0, data.length / 4);
    }

    /**
     * 从流中 读取 len个byte 并转换成float[]
     *
     * @param in
     * @param len
     * @return
     */
    @NonNull
    public static float[] tofloats(@NonNull InputStream in, int len) throws IOException {
        byte[] data = new byte[len];
        in.read(data);
        return tofloats(data);
    }


    //TODO 不美  耦合有点高
    @NonNull
    public static Point[] toPoints(@NonNull InputStream in, int len) throws IOException {
        float[] floats = tofloats(in, len);
        return toPoints(floats);
    }

    @NonNull
    public static Point[] toPoints(@NonNull float[] floats) {
        int n = floats.length / 2;
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(floats[i * 2], floats[i * 2 + 1]);
        }
        return points;
    }

    /**
     * 将byte[]转换成int[]
     *
     * @param d
     * @param bd
     */
    public static void bytesToInts(byte[] bd, @NonNull int[] d) {
        for (int i = 0; i < d.length; i++) {
            d[i] = bytesToInt(bd, i * 4);
        }
    }

    public static void bytesToInts(@NonNull InputStream in, @NonNull int[] d) throws IOException {
        byte[] bd = new byte[d.length * 4];
        in.read(bd);
        bytesToInts(bd, d);
    }

    public static void bytesTofloats(@NonNull InputStream in, @NonNull float[] d) throws IOException {
        byte[] bd = new byte[d.length * 4];
        in.read(bd);
        bytesTofloats(bd, d);
    }

    public static void bytesTofloats(byte[] bd, @NonNull float[] d) {
        for (int i = 0; i < d.length; i++) {
            d[i] = bytesToFloat(bd, i * 4);
        }
    }
}
