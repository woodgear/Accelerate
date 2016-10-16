package com.misakimei.accelerate.geom;

import android.support.annotation.NonNull;

/**
 * Created by 吴聪 on 2016/5/15.
 * obb 包围框
 */
public class Rect extends Geom {
    private static final String TAG = "RECT";
    public Point o, a, b, c;
    public float RECT_RATIO;

    //ra 是最下最左的点 ra rb rc 相对于ra 逆时针排序 (极角序)
    public Rect(Point o, Point a, Point b, Point c) {
        this.o = o;
        this.a = a;
        this.b = b;
        this.c = c;


        float w = GeomTool.dis(o, a);
        float h = GeomTool.dis(a, b);
        RECT_RATIO = w > h ? h / w : w / h;
    }


    public Point center() {
        return GeomTool.center(this);
    }

    public float lefthand() {
        return GeomTool.dis(o, c);
    }

    public float righthand() {
        return GeomTool.dis(o, a);
    }

    @NonNull
    @Override
    public String toString() {
        return o.toString() + a.toString() + b.toString() + c.toString();
    }

    //TODO 现有有矩形 已知四顶点求 面积  (矩形可看作坐标系  假设一点是点 00 ) 应该是简便的方式的
    public float getArea() {
        return lefthand() * righthand();
    }
}
