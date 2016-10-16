package com.misakimei.accelerate.geom;

import android.support.annotation.NonNull;

import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 吴聪 on 2016/5/27.
 * 使用方式 先设置接口setShapeDetectorListener  再setInput()输入数据
 */
//TODO 现在的问题是 画一个矩形可以识别 但画一个8也会被识别
public class ShapeCorrectFactor {

    private static final String TAG = "形状修正工厂";
    public static OnShapeDetectorListener listener;
    private static Rect mabr;
    private static Triangle tria;

    public static Rect getMabr() {
        return mabr;
    }

    public static void setShapeDetectorListener(OnShapeDetectorListener lis) {
        listener = lis;
    }

    public static void setInput(@NonNull ArrayList<Point> data) {


        if (data.size() <= 5) {
            L.d(TAG, "==============>点数为 " + data.size() + "  过少 判定为线");
            return;
        }
        //凸包的缺点是 会忽视凸包之内的部分 类似螺旋会被识别成圆
        ConvexHullFactor.setInput(data);

        if (!ConvexHullFactor.isOK()) {
            L.d(TAG, "几何特征提取出错 无法识别此形状");
            return;
        }

        //下面开始识别
        float narrow = ConvexHullFactor.RECT_RATIO;   //最小外接矩形长宽比
        float Alt = ConvexHullFactor.Alt;             //最大内接三角形面积
        float Ach = ConvexHullFactor.Ach;             //凸包面积
        float Plt = ConvexHullFactor.Plt;             //最大内接三角形周长
        float Pch = ConvexHullFactor.Pch;             //凸包周长
        float Aer = ConvexHullFactor.Aer;             //最小外接矩形面积
        float Alq = ConvexHullFactor.Alq;             //最大内接四边形面积
        float Pps = ConvexHullFactor.Pps;             //线条的长度

        mabr = ConvexHullFactor.mabr;
        tria = ConvexHullFactor.tria;


        L.d(TAG, "开始识别");

        L.d("pps  " + Pps + "  Pch" + Pch + "==>" + Pch / Pps);

        if ((Pch / Pps) < 0.6) {//如果线条长度严重长度凸包周长 那么他不被识别   螺旋
            listener.OnNotShape();
            return;
        }

        if (narrow < 0.1) {
            L.d(TAG, "==============>判定线 窄度 " + narrow + "  " + "应<0.1");
            Point start = data.get(0);
            Point end = data.get(data.size() - 1);
            listener.OnLineShape(new Line(start, end));
            return;
        }//线条是强特征 直接返回即可

        float ci = calGrade((Pch * Pch) / Ach, 4 * Math.PI, 0.1);
        float Acher = Ach / Aer;
        float tr = calGrade(Acher, 0.5, 0.1);
        float Alqch = Alq / Ach;
        float el = calGrade(Alqch, 0.7, 0.1);
        float re = calGrade(Acher, 0.9, 0.1);


        //打分策略
        get(new float[]{ci, tr, el, re}, new ShapeType[]{ShapeType.CIRCLE, ShapeType.TRIANGLE, ShapeType.ELLIPSES, ShapeType.RECTANGLE});
    }

    private static void get(@NonNull float[] grad, ShapeType[] type) {
        L.d(Arrays.toString(grad));
        for (int i = 0; i < grad.length; i++) {
            int maxi = i;
            for (int j = i + 1; j < grad.length; j++) {
                if (grad[j] > grad[maxi]) {
                    maxi = j;
                }
            }
            float tf = grad[maxi];
            grad[maxi] = grad[i];
            grad[i] = tf;
            ShapeType ts = type[maxi];
            type[maxi] = type[i];
            type[i] = ts;
        }//排序


        if (grad[0] != 0) {
            switch (type[0]) {
                case CIRCLE:
                    Point center = mabr.center();
                    float radius = (mabr.lefthand() + mabr.righthand()) / 4;//设定:   当确定为圆时 圆心为mabr中点 半径为mabr的(宽+高)/4
                    listener.OnCircleShape(new Circle(center, radius));
                    break;
                case RECTANGLE:
                    listener.OnRectangleShape(mabr);
                    break;
                case TRIANGLE:
                    listener.OnTriangleShape(tria);
                    break;
                case ELLIPSES:
                    listener.OnEllipseShape(mabr);
                    break;
            }
        } else {
            listener.OnNotShape();
        }
    }

    //TODO 其函数应该是类似指数级 y=ex 的而不是 y=x
    //计算相似度
    private static float calGrade(float v, double res, double offset) {
        if (res == v) {
            return 100;
        }
        if (v < res - offset || v > res + offset) {
            return 0;
        }
        double len = offset;
        double g = v < res ? (res - v) : (v - res);
        return (float) (g / len);
    }


    public interface OnShapeDetectorListener {

        void OnLineShape(Line line);

        void OnTriangleShape(Triangle tria);

        void OnRectangleShape(Rect rect);

        void OnEllipseShape(Rect rect);

        void OnCircleShape(Circle rect);

        void OnNotShape();
    }
}
