package com.misakimei.accelerate.geom;


import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by 吴聪 on 2016/5/12.
 * 计算几何工具包
 */
public class GeomTool {
    private static final String TAG = "GeomTool";


    public static float dis(@NonNull Point a, @NonNull Point b) {
        return (float) Math.hypot(a.x - b.x, a.y - b.y);
    }

    public static float cross(@NonNull Point a, @NonNull Point b, @NonNull Point p) {
        return cross(new Point(b.x - a.x, b.y - a.y), new Point(p.x - a.x, p.y - a.y));
    }

    //TODO 像鸭子一样的就是鸭子 Point有两个float参数 Vector也有两个float参数 Vector有一个叉乘方法 现在我想把Point看成Vector来使用叉乘的方法 但是却无能为力
    public static float cross(@NonNull Point a, @NonNull Point b) {
        return a.x * b.y - b.x * a.y;
    }

    public static float dot(@NonNull Point a, @NonNull Point b, @NonNull Point c) {
        return (c.x - a.x) * (b.x - a.x) + (c.y - a.y) * (b.y - a.y);
    }

    /**
     * 求点a b组成的线段的中点
     *
     * @param a
     * @param b
     * @return
     */
    @NonNull
    public static Point center(@NonNull Point a, @NonNull Point b) {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }


    /**
     * 求Rect rect的中点
     *
     * @param rect
     * @return
     */
    @NonNull
    public static Point center(@NonNull Rect rect) {
        return center(rect.o, rect.b, rect.a, rect.c);
    }

    /**
     * 求 线段 l1sl1e l2sl2e的交点
     *
     * @param l1s
     * @param l1e
     * @param l2s
     * @param l2e
     * @return
     */
    //power by http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
    @NonNull
    private static Point center(@NonNull Point l1s, @NonNull Point l1e, @NonNull Point l2s, @NonNull Point l2e) {
        Point res = new Point();
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = l1e.x - l1s.x;
        s1_y = l1e.y - l1s.y;
        s2_x = l2e.x - l2s.x;
        s2_y = l2e.y - l2s.y;

        float s, t;
        s = (-s1_y * (l1s.x - l2s.x) + s1_x * (l1s.y - l2s.y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (l1s.y - l2s.y) - s2_y * (l1s.x - l2s.x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            res.x = l1s.x + (t * s1_x);
            res.y = l1s.y + (t * s1_y);
            return res;
        }
        throw new RuntimeException("计算线段中点出现错误");
    }

    @NonNull
    public static Point[] orignSort(@NonNull Point[] data) {
        //找到最左下的点
        int index = 0;
        for (int size = data.length, i = 1; i < size; i++) {
            Point item = data[i];
            if (item.y < data[index].y || (item.y == data[index].y && item.x < data[index].x)) {
                index = i;
            }
        }
        final Point origin = data[index];
        data[index] = data[0];
        data[0] = origin;
        //极角排序
        Arrays.sort(data, 1, data.length, new Comparator<Point>() {
            @Override
            public int compare(@NonNull Point lt, @NonNull Point rt) {
                //比较角度 运用tan公式 ax/ay>bx/by ===>ax*by>ay*bx ==>ax*by-ay*bx>0  正好是叉乘公式
                float flag = (lt.x - origin.x) * (rt.y - origin.y) - (rt.x - origin.x) * (lt.y - origin.y);
                if (flag != 0) {
                    return flag > 0 ? -1 : 1;
                } else {
                    return dis(origin, lt) < dis(origin, rt) ? -1 : 1;
                }

            }
        });
        return data;

    }

    public static float sideLen(@NonNull RectF rect) {
        return (float) Math.hypot(rect.top - rect.bottom, rect.left - rect.right);
    }
}
