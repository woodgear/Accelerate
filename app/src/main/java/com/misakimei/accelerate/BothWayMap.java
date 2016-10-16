package com.misakimei.accelerate;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by 18754 on 2016/8/11.
 * 双向列表
 */
public class BothWayMap {
    private HashMap map1;
    private HashMap map2;
    private Object A;
    private Object B;

    public BothWayMap(Object a, Object b) {
        this.A = a;
        this.B = b;

        map1 = new HashMap();
        map2 = new HashMap();
    }

    public void put(@NonNull Object a, Object b) {
        if (a.getClass().equals(A)) {
            map1.put(a, b);
            map2.put(b, a);
        } else if (a.equals(equals(B))) {
            map2.put(a, b);
            map1.put(b, a);
        } else {
            throw new RuntimeException("类型错误");
        }

    }

    public Object get(@NonNull Object k) {
        if (k.getClass().equals(A)) {
            return map1.get(k);
        } else if (k.getClass().equals(B)) {
            return map2.get(k);
        }
        throw new RuntimeException("类型错误");
    }
}
