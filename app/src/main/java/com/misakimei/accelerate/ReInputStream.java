package com.misakimei.accelerate;

import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.misakimei.accelerate.tool.ConvertData.bytesToFloat;
import static com.misakimei.accelerate.tool.ConvertData.bytesToInt;
import static com.misakimei.accelerate.tool.ConvertData.bytesToInts;
import static com.misakimei.accelerate.tool.ConvertData.bytesTofloats;
import static com.misakimei.accelerate.tool.ConvertData.getIntArray;
import static com.misakimei.accelerate.tool.ConvertData.tofloats;

/**
 * Created by 18754 on 2016/8/12.
 * 封装好的用于保存的InputStream
 */


public class ReInputStream extends ByteArrayInputStream {
    public ReInputStream(byte[] buf) {
        super(buf);
    }

    public ReInputStream(byte[] buf, int offset, int length) {
        super(buf, offset, length);
    }

    public int toInt() {
        try {
            byte[] data = new byte[4];
            read(data);
            return bytesToInt(data, 0);
        } catch (IOException e) {
            throw new RuntimeException("toInt出错");
        }
    }

    /**
     * 读取n字节 转换成int[]
     *
     * @param n byte 数
     * @return
     */
    @NonNull
    public int[] toInts(int n) {
        try {
            byte[] data = new byte[n];
            read(data);
            return getIntArray(data);
        } catch (IOException e) {
            throw new RuntimeException("toInts出错");
        }
    }

    @NonNull
    public int[] toNInts(int n) {
        return toInts(n * 4);
    }

    public float toFloat(int n) {
        try {
            byte[] data = new byte[4];
            read(data);
            return bytesToFloat(data, 0);
        } catch (IOException e) {
            throw new RuntimeException("toFloat出错");
        }
    }

    @NonNull
    public float[] toFloats(int n) {
        try {
            byte[] data = new byte[n];
            read(data);
            return tofloats(data);
        } catch (IOException e) {
            throw new RuntimeException("toInts出错");
        }
    }

    @NonNull
    public float[] toNFloas(int n) {
        return toFloats(n * 4);
    }

    @NonNull
    public ReInputStream get(@NonNull int[] d) {
        try {
            bytesToInts(this, d);
            return this;
        } catch (IOException e) {
            throw new RuntimeException("出错");
        }
    }

    @NonNull
    public ReInputStream get(@NonNull float[] d) {
        try {
            bytesTofloats(this, d);
            return this;
        } catch (IOException e) {
            throw new RuntimeException("出错");
        }
    }

}
