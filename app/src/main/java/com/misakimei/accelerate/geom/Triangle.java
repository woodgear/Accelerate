package com.misakimei.accelerate.geom;

import android.support.annotation.NonNull;

/**
 * Created by 吴聪 on 2016/5/31.
 * 三角形
 */
public class Triangle extends Geom {
    public Point a, b, c;
    public float perimeter;
    public float area;


    public Triangle(@NonNull Point a, @NonNull Point b, @NonNull Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        area = GeomTool.cross(a, b, c) / 2;
        perimeter = GeomTool.dis(a, b) + GeomTool.dis(b, c) + GeomTool.dis(c, a);
    }
}
