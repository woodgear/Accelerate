package com.misakimei.accelerate;

import android.support.annotation.NonNull;

import com.misakimei.accelerate.tool.L;

import java.io.IOException;

/**
 * Created by 18754 on 2016/8/12.
 * 导入导出父类
 */
public abstract class ReLoad {
    private static final String TAG = "ReLoad";

    public byte getByte() {
        return TypeMap.get(this);
    }

    @NonNull
    public ReOutputStream save(@NonNull ReOutputStream out) {
        try {
            ReOutputStream data = new ReOutputStream();
            L.d(TAG, "save " + getByte() + " " + TypeMap.get(getByte()));

            preserve(data);

            out.add(getByte())
                    .add(data.size())
                    .add(data);
            data.close();
            return out;
        } catch (IOException e) {
            throw new RuntimeException("保存错误");
        }
    }

    public void recover(@NonNull ReInputStream in) {
        byte type = (byte) in.read();
        if (type != getByte()) {
            throw new RuntimeException("类型错误 " + "应该是 " + TypeMap.get(getByte()) + "  读取到 " + TypeMap.get(type));
        }
        int len = in.toInt();
        byte[] data = new byte[len];//不能污染了
        try {
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init(new ReInputStream(data), len);
    }

    public abstract void init(ReInputStream in, int len);

    public abstract ReOutputStream preserve(ReOutputStream out);


}
