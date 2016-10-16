package com.misakimei.accelerate;

import android.support.annotation.NonNull;

import com.misakimei.accelerate.tool.L;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 18754 on 2016/8/11.
 * 类 type hash
 */
public class TypeMap {

    @NonNull
    private static HashMap<String, Byte> map = new HashMap<>();
    @NonNull
    private static HashMap<Byte, String> map2 = new HashMap<>();

    static {
        map.put("Camera", (byte) 0);
        map.put("PaintManager", (byte) 1);
        map.put("PaintStyle", (byte) 2);
        map.put("PathCollection", (byte) 3);
        map.put("VectorPath", (byte) 4);

        for (Map.Entry<String, Byte> en : map.entrySet()) {
            map2.put(en.getValue(), en.getKey());
        }
    }

    public static byte get(@NonNull Object obj) {
        String name = obj.getClass().getSimpleName();
        L.d("getSimpleName " + name);
        Object v = map.get(name);
        if (v == null) {
            throw new RuntimeException("无法找到类型 " + name);
        }
        return (byte) v;

    }

    public static String get(Byte type) {
        return map2.get(type);
    }

}