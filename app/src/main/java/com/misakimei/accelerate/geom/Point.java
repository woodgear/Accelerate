package com.misakimei.accelerate.geom;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by 吴聪 on 2016/5/29.
 */

public class Point {
    public float x, y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point(@NonNull PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        return Double.compare(point.y, y) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "[x:" + x + ",y:" + y + "]";
    }
}
