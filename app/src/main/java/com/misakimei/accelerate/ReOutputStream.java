package com.misakimei.accelerate;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.misakimei.accelerate.tool.ConvertData.tobytes;


/**
 * Created by 18754 on 2016/8/12.
 * 流畅界面
 */
public class ReOutputStream extends ByteArrayOutputStream {

    private static final String TAG = "ReOutputStream";

    @NonNull
    public ReOutputStream add(byte b) {
        super.write(b);
        return this;
    }

    @NonNull
    public ReOutputStream add(@NonNull byte[] bs) {
        try {
            super.write(bs);
            return this;
        } catch (IOException e) {
            throw new RuntimeException("写入出错");
        }
    }

    @NonNull
    public ReOutputStream add(int data) {
        add(tobytes(data));
        return this;
    }

    @NonNull
    public ReOutputStream add(@NonNull int[] data) {
        add(tobytes(data));
        return this;
    }

    @NonNull
    public ReOutputStream add(float data) {
        add(tobytes(data));
        return this;
    }

    @NonNull
    public ReOutputStream add(@NonNull float[] data) {
        add(tobytes(data));
        return this;
    }

    @NonNull
    public ReOutputStream add(@NonNull ReOutputStream data) {
        add(data.toByteArray());
        return this;
    }
}
