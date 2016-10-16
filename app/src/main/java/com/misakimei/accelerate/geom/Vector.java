package com.misakimei.accelerate.geom;

import android.support.annotation.NonNull;

/**
 * Created by 吴聪 on 2016/5/30.
 */
public class Vector {
    public double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(@NonNull Point p) {
        this(p.x, p.y);
    }


    public Vector(@NonNull Point s, @NonNull Point e) {
        this.x = e.x - s.x;
        this.y = e.y - s.y;
    }


    public double dot(@NonNull Vector other) {
        return x * other.x + y * other.y;
    }

    public double cross(@NonNull Vector other) {
        return x * other.y - other.x * y;
    }

    public double Length() {
        return Math.sqrt(dot(this));
    }


    @NonNull
    @Override
    public String toString() {
        return "Vector x: " + x + "  y: " + y;
    }
}
