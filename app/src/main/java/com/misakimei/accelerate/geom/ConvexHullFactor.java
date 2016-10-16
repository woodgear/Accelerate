package com.misakimei.accelerate.geom;


import android.support.annotation.NonNull;

import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by 吴聪 on 2016/5/17.
 * 给定点集 计算出其集合特征
 */
public class ConvexHullFactor {

    public static final String TAG = "ConvexHullFactor";
    //凸包的最小外接矩形的宽高比
    public static float RECT_RATIO;

    public static Rect mabr;
    public static Triangle tria;

    public static float Alt;
    public static float Ach;
    public static float Plt;
    public static float Pch;
    public static float Aer;
    public static float Alq;
    public static float loseRatio;
    public static float Pps;


    private static boolean status = false;         //计算过程中可能会出现凸包顶点过少的情况 通过置此值为false来提示外部  可以抛出异常

    public static boolean isOK() {
        return status;
    }

    public static void setInput(@NonNull ArrayList<Point> list) {

        Pps = calPerimeter(list);
        list = new ArrayList<>(new LinkedHashSet(list));//去重
        Point[] data = new Point[list.size()];
        list.toArray(data);

        Point[] convex = calConvex(data);
        loseRatio = (float) ((list.size() - convex.length) * 1.0 / list.size());

        //凸包顶点过少
        if (convex.length < 5) {
            L.d("凸包顶点过少");
            status = false;
            return;
        }
        Ach = calArea(convex);
        Pch = calPerimeter(convex);

//        for (Point p:data){
//            L.d(p.toString());
//        }

        mabr = calMABR(convex);
        Aer = mabr.getArea();
        RECT_RATIO = mabr.RECT_RATIO;


        Quad quad = calQuad(convex);
        Alq = quad.area;

        tria = calTria(convex);
        Alt = tria.area;
        Plt = tria.perimeter;

        status = true;
    }

    private static float calPerimeter(@NonNull ArrayList<Point> list) {
        float sum = 0;
        for (int i = 1; i < list.size(); i++) {
            sum += GeomTool.dis(list.get(i - 1), list.get(i));
        }
        return sum;
    }

    private static int calPoint(@NonNull Point[] data) {
        int sum = 0;
        for (int i = 0, n = data.length; i < n; i++) {
            float angle = calAngle(data[(i + 1) % n], data[i], data[(i + 2) % n]);
            if (angle < 100) {
                L.d("=============>" + angle);
            } else {
                L.d(angle);
            }

        }
        return 0;
    }

    /**
     * 已知abc三点求 bac组成的角的角度 角度小于180
     *
     * @param a
     * @param b
     * @param c
     */
    private static float calAngle(@NonNull Point a, @NonNull Point b, @NonNull Point c) {
        float v = GeomTool.dot(a, b, c);
        float d1 = GeomTool.dis(a, b), d2 = GeomTool.dis(a, c);
        float cosv = v / (d1 * d2);
        return (float) Math.toDegrees(Math.acos(cosv));

    }


    private static float calPerimeter(@NonNull Point[] data) {
        float sum = 0;
        for (int i = 1; i < data.length; i++) {
            sum += GeomTool.dis(data[i - 1], data[i]);
        }
        return sum;
    }

    private static float calArea(@NonNull Point[] data) {
        float sum = 0;
        for (int i = 2; i < data.length; i++) {
            sum += GeomTool.cross(data[0], data[i - 1], data[i]);
        }
        return sum / 2;
    }

    /**
     * @param data 点集
     * @return 凸包的点集 将data看作右上为正方向的坐标系的点 从最下最左开始逆时针顺序
     */
    private static Point[] calConvex(Point[] data) {
        return Graham(data);
    }

    /**
     * 求凸包
     * 输入无重复点集(左上为正方向)
     * PS:假定点集坐标轴以右上为正方向
     * PS:输出点集第一个点为点集中最左下的点 接下的点按照逆时针排序
     *
     * @param data 无重复点集
     * @return 凸包点集
     */
    private static Point[] Graham(Point[] data) {

        data = GeomTool.orignSort(data);
        //三角形的凸包是其自身
        if (data.length == 3) {
            return data;
        }

        Point[] stack = new Point[data.length];
        stack[0] = data[0];
        stack[1] = data[1];
        stack[2] = data[2];
        int top = 2;
        for (int size = data.length, i = 3; i < size; i++) {
            //算导中没有考虑到从开始一直需要pop的情况 所以要限定一下top 就是top>0
            while (top > 0 && GeomTool.cross(stack[top - 1], stack[top], data[i]) <= 0) {
                top--;
            }
            top++;
            stack[top] = data[i];
        }

        return Arrays.copyOf(stack, top + 1);
    }


    /**
     * 旋转卡壳 利用凸包上的单锋性质
     *
     * @param ch 点集
     * @return 凸包的最小面积包围框面积(min_area_bound_rect_area)
     */
    @NonNull
    private static Rect calMABR(@NonNull Point[] ch) {
        int n = ch.length;
        double min = Double.MAX_VALUE;
        int i = 0, T = 1, R = 1, L = 1;//i->i+1 边 距离边最上的点(T top)最右的点（R） 最左的点(L) 的下标
        int bi = i, bt = T, br = R, bl = L;

        for (i = 0; i < n; i++) {
            //找到ch[i]->ch[i+1]方向上最上的点
            while (GeomTool.cross(ch[i], ch[(i + 1) % n], ch[(T + 1) % n]) >= GeomTool.cross(ch[i], ch[(i + 1) % n], ch[T]))//单锋性质 cross 简便计算 用于比较长度
            {
                T = (T + 1) % n;
            }
            //找到ch[i]->ch[i+1]方向上最右的点
            while (GeomTool.dot(ch[i], ch[(i + 1) % n], ch[(R + 1) % n]) >= GeomTool.dot(ch[i], ch[(i + 1) % n], ch[R])) {
                R = (R + 1) % n;
            }
            if (i == 0) L = R;//最左的点是在最右点之后
            //找到ch[i]->ch[i+1]方向上最左的点
            while (GeomTool.dot(ch[i], ch[(i + 1) % n], ch[(L + 1) % n]) <= GeomTool.dot(ch[i], ch[(i + 1) % n], ch[L])) {
                L = (L + 1) % n;
            }
            //已知一条边 最上 最右 最左 求面积
            double len = GeomTool.dis(ch[i], ch[(i + 1) % n]);
            double hei = GeomTool.cross(ch[i], ch[(i + 1) % n], ch[T]) / len;
            double width = (GeomTool.dot(ch[i], ch[(i + 1) % n], ch[R]) - GeomTool.dot(ch[i], ch[(i + 1) % n], ch[L])) / len;
            double temp = hei * width;
            if (min > temp) {
                min = temp;
                bi = i;
                bt = T;
                br = R;
                bl = L;
            }
        }
        return calRectPoints(ch[bi], ch[(bi + 1) % n], ch[bt], ch[br], ch[bl]);
    }


    /**
     * 设有一矩形R
     * 已知线段 b->bn在R的底边上 t 在R的顶边上 r在R的右边上 l在R的左边上
     * 求矩形R的四个顶点
     */
    @NonNull
    private static Rect calRectPoints(@NonNull Point b, @NonNull Point bn, @NonNull Point t, @NonNull Point r, @NonNull Point l) {
        Point rb, lb, rt, lt;
        if (b.equals(l) || bn.equals(r)) {//有重合
            if (b.equals(l) && bn.equals(r)) {//完全重合
                lb = b;
                rb = bn;
            } else if (b.equals(l)) {
                lb = calPoint1(b, bn, r);
                rb = l;
            } else {
                lb = calPoint1(b, bn, l);
                rb = r;
            }
            lt = calPoint2(lb, rb, t);
            rt = calPoint2(rb, lb, t);
        } else {
            rb = calPoint1(b, bn, r);
            lb = calPoint1(b, bn, l);
            rt = calPoint1(rb, r, t);
            lt = calPoint1(lb, l, t);
        }
        Point[] points = GeomTool.orignSort(new Point[]{rb, lb, rt, lt});
        Rect rect = new Rect(points[0], points[1], points[2], points[3]);

        return rect;

    }

    /**
     * 已知 a b c 三点
     * 设l1为过ab直线 l2为过c且与l1平行直线 l3为过a且与l1垂直直线 求l3与l2交点
     * 推导过程在 SoftCup/注释/calPoint2
     */
    @NonNull
    private static Point calPoint2(@NonNull Point a, @NonNull Point b, @NonNull Point c) {
        Point res = new Point();
        float a1 = (b.x - a.x), b1 = b.y - a.y, c1 = a.x * (b.x - a.x) + a.y * (b.y - a.y);
        float a2 = b.y - a.y, b2 = a.x - b.x, c2 = c.y * (a.x - b.x) - c.x * (a.y - b.y);

        float base = a1 * b2 - a2 * b1;

        res.x = (b2 * c1 - b1 * c2) / base;
        res.y = (a1 * c2 - a2 * c1) / base;
        return res;
    }

    /**
     * 已知 A B C 三点
     * 设l1为过AB直线 l2为过C且与l1垂直直线 求li l2 交点
     */
    //TODO 联立方程时 斜率会出现问题 目前先补救一下 应该有最优解的
    @NonNull
    private static Point calPoint1(@NonNull Point A, @NonNull Point B, @NonNull Point C) {
        if (A.x == B.x) {
            return new Point(A.x, C.y);
        } else if (A.y == B.y) {
            return new Point(C.x, A.y);
        }

        double a = B.x - A.x, b = B.y - A.y;
        double x = (a * b * (C.y - A.y) + b * b * A.x + a * a * C.x) / (a * a + b * b);
        double y = b / a * (x - A.x) + A.y;
        return new Point(x, y);
    }

    //check ok
    //power by  http://stackoverflow.com/questions/1621364/how-to-find-largest-triangle-in-convex-hull-aside-from-brute-force-search/1621913#1621913
    @NonNull
    private static Triangle calTria(@NonNull Point[] z) {
        Point A = z[0], B = z[1], C = z[2];
        int a = 0, b = 1, c = 2;
        int n = z.length;
        while (true) {
            while (true) {
                while (area(z[a], z[b], z[c]) <= area(z[a], z[b], z[(c + 1) % n])) {
                    c = (c + 1) % n;
                }
                if (area(z[a], z[b], z[c]) > area(z[a], z[(b + 1) % n], z[c])) {
                    break;
                }
                b = (b + 1) % n;
            }
            if (area(z[a], z[b], z[c]) > area(A, B, C)) {
                A = z[a];
                B = z[b];
                C = z[c];
            }
            a = (a + 1) % n;
            if (a == b) {
                b = (b + 1) % n;
            }
            if (b == c) {
                c = (c + 1) % n;
            }
            if (a == 0) {
                break;
            }
        }
        return new Triangle(A, B, C);
    }

    //用于对拍的遍历求三角形的方法
    private static double getTriaForce(@NonNull Point[] ch) {
        double max = -1;
        int n = ch.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (area(ch[i], ch[j], ch[k]) > max) {
                        max = area(ch[i], ch[j], ch[k]);
                    }
                }
            }
        }
        return max;

    }

    //check ok
    //power by  on a general method for maximizing and minimizing among certain geometric problems
    @NonNull
    private static Quad calQuad(@NonNull Point[] z) {
        Point A = z[0], B = z[1], C = z[2], D = z[3];
        int a = 0, b = 1, c = 2, d = 3;
        int n = z.length;
        while (true) {
            while (true) {
                while (true) {
                    while (area(z[a], z[b], z[c], z[d]) <= area(z[a], z[b], z[c], z[(d + 1) % n])) {
                        d = (d + 1) % n;
                    }
                    if (area(z[a], z[b], z[c], z[d]) > area(z[a], z[b], z[(c + 1) % n], z[d])) {
                        break;
                    }
                    c = (c + 1) % n;
                }
                if (area(z[a], z[b], z[c], z[d]) > area(z[a], z[(b + 1) % n], z[c], z[d])) {
                    break;
                }
                b = (b + 1) % n;
            }
            if (area(z[a], z[b], z[c], z[d]) > area(A, B, C, D)) {
                A = z[a];
                B = z[b];
                C = z[c];
                D = z[d];
            }
            a = (a + 1) % n;
            if (a == b) {
                b = (b + 1) % n;
            }
            if (b == c) {
                c = (c + 1) % n;
            }
            if (c == d) {
                d = (d + 1) % n;
            }
            if (a == 0) {
                break;
            }
        }

        return new Quad(A, B, C, D);
    }

    //用于对拍的遍历求四边形的方法
    public static double calQuadForce(@NonNull Point[] data) {
        double area = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                for (int k = 0; k < data.length; k++) {
                    for (int h = 0; h < data.length; h++) {
                        if ((i == j || i == h || i == k || j == h || j == k || h == k)) {
                            continue;
                        }
                        double max = area(data[i], data[j], data[k], data[h]);
                        if (max > area) {
                            area = max;
                        }
                    }
                }
            }
        }
        return area;
    }

    private static float area(@NonNull Point a, @NonNull Point b, @NonNull Point c, @NonNull Point d) {
        return (GeomTool.cross(a, b, c) + GeomTool.cross(a, c, d)) / 2;
    }

    private static float area(@NonNull Point a, @NonNull Point b, @NonNull Point c) {
        return GeomTool.cross(a, b, c) / 2;
    }

}
