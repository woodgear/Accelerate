package com.misakimei.accelerate.path;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.PaintStyle;
import com.misakimei.accelerate.ReInputStream;
import com.misakimei.accelerate.ReLoad;
import com.misakimei.accelerate.ReOutputStream;
import com.misakimei.accelerate.geom.Circle;
import com.misakimei.accelerate.geom.Geom;
import com.misakimei.accelerate.geom.GeomTool;
import com.misakimei.accelerate.geom.Line;
import com.misakimei.accelerate.geom.Point;
import com.misakimei.accelerate.geom.Rect;
import com.misakimei.accelerate.geom.ShapeCorrectFactor;
import com.misakimei.accelerate.geom.ShapeType;
import com.misakimei.accelerate.geom.Triangle;
import com.misakimei.accelerate.tool.CommandMap;
import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.misakimei.accelerate.tool.ConvertData.toPoints;
import static com.misakimei.accelerate.tool.ConvertData.tobytes;

/**
 * Created by 吴聪 on 2016/5/5.
 * TODO 感觉使用View果然不爽 应该换成SurfaceView 这不是VectorPath应该关心的?
 * 矢量线条类 代表的是用户手绘的一条线条
 */
public class VectorPath extends ReLoad {

    private static final String TAG = "VectorPath";
    private static final String MODE = "{name:VectorPath,SHAPETYPE:%s,MATRIX:%s,BOUND:%s,PAINT:%s,SIZE:%d}";
    public Geom geom;//识别出的形状  默认为空
    //TODO 贝塞尔曲线的拟合算法
    //手指滑过屏幕带来的点
    @NonNull
    private ArrayList<Point> points = new ArrayList<>();
    @NonNull
    private Path mPath = new Path();//原始曲线
    @NonNull
    private RectF mBound = new RectF();//此线条的包围框
    private ShapeType type = ShapeType.CURVE;//此线条所代表的形状 默认是曲线
    private Matrix matrix = new Matrix();//模型坐标转换
    private PaintStyle style;//绘制此线条的样式
    private Paint mpaint;//由上面的生成
    //形状修正处理器
    @NonNull
    private ShapeCorrectFactor.OnShapeDetectorListener listener = new ShapeCorrectFactor.OnShapeDetectorListener() {
        @Override
        public void OnLineShape(@NonNull Line line) {
            geom = line;
            type = ShapeType.LINE;
            mPath.reset();
            mPath.moveTo(line.s.x, line.s.y);
            mPath.lineTo(line.e.x, line.e.y);
        }

        @Override
        public void OnTriangleShape(@NonNull Triangle tria) {
            geom = tria;
            type = ShapeType.TRIANGLE;
            mPath.reset();
            mPath.moveTo(tria.a.x, tria.a.y);
            mPath.lineTo(tria.b.x, tria.b.y);
            mPath.lineTo(tria.c.x, tria.c.y);
            mPath.lineTo(tria.a.x, tria.a.y);
        }

        @Override
        public void OnRectangleShape(@NonNull Rect rect) {
            geom = rect;
            type = ShapeType.RECTANGLE;
            mPath.reset();
            mPath.moveTo(rect.o.x, rect.o.y);
            mPath.lineTo(rect.a.x, rect.a.y);
            mPath.lineTo(rect.b.x, rect.b.y);
            mPath.lineTo(rect.c.x, rect.c.y);
            mPath.lineTo(rect.o.x, rect.o.y);
        }

        @Override
        public void OnEllipseShape(@NonNull Rect rect) {
            geom = rect;
            type = ShapeType.ELLIPSES;
            mPath.reset();
            //已知椭圆的obb 画出一个椭圆 因为 android path 绘制椭圆只能接受 AABB的RectF 所以先画一个AABB下的矩形 再旋转
            //将obb 的oa向量边放平得到的AABB的矩形
            RectF regularRect = new RectF(rect.o.x, rect.o.y + GeomTool.dis(rect.o, rect.c), rect.o.x + GeomTool.dis(rect.o, rect.a), rect.o.y);

            mPath.addOval(regularRect, Path.Direction.CCW);

            Matrix roate = new Matrix();
            float angle = (float) Math.toDegrees(Math.atan2(rect.a.y - rect.o.y, rect.a.x - rect.o.x));//得到旋转角度
            roate.postRotate(angle, rect.o.x, rect.o.y);//逆时针转
            mPath.transform(roate);
        }

        @Override
        public void OnCircleShape(@NonNull Circle c) {
            geom = c;
            type = ShapeType.CIRCLE;
            mPath.reset();

            mPath.addCircle(c.center.x, c.center.y, c.radius, Path.Direction.CCW);
        }

        @Override
        public void OnNotShape() {
            type = ShapeType.CURVE;
        }
    };

    public VectorPath() {
    }

    public VectorPath(@NonNull PaintStyle style, Matrix matrix) {
        this.style = style;
        mpaint = style.getPaint();
        this.matrix = matrix;
        ShapeCorrectFactor.setShapeDetectorListener(listener);
    }

    /**
     * 判断路径的包围框是否在bound(视野)中
     *
     * @param bound
     * @return
     */
    public boolean intersects(@NonNull RectF bound) {
        return bound.intersects(mBound.left, mBound.top, mBound.right, mBound.bottom);
    }

    //所有的最后绘制的是这个方法

    public void draw(@NonNull Canvas canvas, final Matrix ma, float ratio) {
        Path path = getPath();
        Matrix matrix1 = new Matrix(ma);
        matrix1.preConcat(matrix);
        path.transform(matrix1);
        expandPanint(ratio);
        canvas.drawPath(path, mpaint);
    }

    public void moveto(@NonNull Point p) {
        mPath = new Path();
        mPath.moveTo(p.x, p.y);
        mBound.set(p.x, p.y, p.x, p.y);
        points.add(new Point(p.x, p.y));
    }


    public void move(@NonNull Point p) {
        points.add(new Point(p.x, p.y));
        mPath.lineTo(p.x, p.y);
        mBound.union(p.x, p.y);
    }

    public void up() {
        float paintwidth = (style.getSize() + style.getBlur()) / 2;
        mBound.inset(-paintwidth, -paintwidth);
        matrix.mapRect(mBound);
    }

    //TODO 这里需要修正
    public void draw(@NonNull Canvas canvas) {
        draw(canvas, Camera.getInstance().getInverseMatrix(), 1 / Camera.getInstance().getRatio());
    }

    @NonNull
    private Path getPath() {
        Path path = new Path(mPath);
        return path;
    }

    public void expandPanint(float ratio) {
        mpaint.setStrokeWidth(style.getSize() * ratio);
        if (style.getBlur() != 0) {
            BlurMaskFilter maskFilter = new BlurMaskFilter(style.getBlur() * ratio, BlurMaskFilter.Blur.NORMAL);
            mpaint.setMaskFilter(maskFilter);
        }
    }


    @NonNull
    public VectorPath getPathRecord(boolean needdetect) {
        if (needdetect) {
            ShapeCorrectFactor.setInput(points);
        }
        return this;
    }

    public boolean getType() {
        return type != ShapeType.CURVE;
    }


    @Override
    public String toString() {
        return String.format(Locale.CHINA, MODE, type.name(), matrix.toString(), mBound.toString(), style.toString(), points.size());
    }

    @NonNull
    public ArrayList<Point> getData() {
        return points;
    }

    @NonNull
    public float[] getMatrix() {
        float[] val = new float[9];
        matrix.getValues(val);
        return val;
    }

    public PaintStyle getPaintStyle() {
        return style;
    }


    @Override
    public void init(@NonNull ReInputStream in, int len) {
        type = CommandMap.getShapeType((byte) in.read());
        int n = in.toInt();
        float[] floats = in.toFloats(n);
        switch (type) {
            case CURVE:
                points = new ArrayList<>(Arrays.asList(toPoints(floats)));
                mPath.reset();
                mPath.moveTo(points.get(0).x, points.get(0).y);
                for (int i = 1; i < points.size(); i++) {
                    mPath.lineTo(points.get(i).x, points.get(i).y);
                }
                break;
            case LINE:
                Point[] lp = toPoints(floats);
                listener.OnLineShape(new Line(lp[0], lp[1]));
                break;
            case RECTANGLE:
                Point[] rp = toPoints(floats);
                listener.OnRectangleShape(new Rect(rp[0], rp[1], rp[2], rp[3]));
                break;
            case ELLIPSES:
                Point[] ep = toPoints(floats);
                listener.OnEllipseShape(new Rect(ep[0], ep[1], ep[2], ep[3]));
                break;
            case CIRCLE:
                listener.OnCircleShape(new Circle(new Point(floats[0], floats[1]), floats[2]));
                break;
            case TRIANGLE:
                Point[] tp = toPoints(floats);
                listener.OnTriangleShape(new Triangle(tp[0], tp[1], tp[2]));
                break;
        }

        //paintstyle
        style = new PaintStyle();
        style.recover(in);
        mpaint = style.getPaint();
        L.d(TAG, "INIT " + "paintstyle  恢复完成");

        //matrix
        byte mtype = (byte) in.read();
        if (mtype != 11) {
            L.d(TAG, "MATRIX 类型错误");
        }
        int mlen = in.toInt();
        L.d(TAG, "Matrix Init " + mtype + " " + mlen);
        float[] md = in.toFloats(mlen);
        matrix.setValues(md);
        L.d(TAG, "INIT " + "matrix  恢复完成");


        //rect
        byte rtype = (byte) in.read();
        int rlen = in.toInt();
        L.d(TAG, "INIT " + rtype + " " + rlen);
        float[] res = in.toFloats(rlen);
        mBound = new RectF(res[0], res[1], res[2], res[3]);
        L.d(mBound.toString());
        L.d(TAG, "INIT " + "rect  恢复完成");

    }

    @NonNull
    @Override
    public ReOutputStream preserve(@NonNull ReOutputStream out) {


        //shape
        //TODO 仍需优化
        byte shape = CommandMap.getShapeTypeByte(type);
        byte[] shapedata = null;

        if (type == ShapeType.CURVE) {
            int size = points.size();
            float[] temp = new float[size * 2];
            for (int i = 0; i < size; i++) {
                temp[i * 2] = points.get(i).x;
                temp[(i * 2) + 1] = points.get(i).y;
            }
            shapedata = tobytes(temp);
        } else {
            switch (type) {
                case LINE:
                    Line line = (Line) geom;
                    shapedata = tobytes(new float[]{line.s.x, line.s.y, line.e.x, line.e.y});
                    break;
                case TRIANGLE:
                    Triangle tri = (Triangle) geom;
                    shapedata = tobytes(new float[]{tri.a.x, tri.a.y, tri.b.x, tri.b.y, tri.c.x, tri.c.y,});
                    break;
                case CIRCLE:
                    Circle cir = (Circle) geom;
                    shapedata = tobytes(new float[]{cir.radius, cir.center.x, cir.center.y});
                    L.d(TAG, "保存圆  " + cir.radius + ":" + cir.center.x + ":" + cir.center.y);

                    break;
                case ELLIPSES:
                    Rect eli = (Rect) geom;
                    shapedata = tobytes(new float[]{eli.o.x, eli.o.y, eli.a.x, eli.a.y, eli.b.x, eli.b.y, eli.c.x, eli.c.y});
                    break;
                case RECTANGLE:
                    Rect rect = (Rect) geom;
                    shapedata = tobytes(new float[]{rect.o.x, rect.o.y, rect.a.x, rect.a.y, rect.b.x, rect.b.y, rect.c.x, rect.c.y});
                    break;
            }
        }

        //shape
        out.add(shape)
                .add(shapedata.length)
                .add(shapedata);

        style.save(out);

        //matrix
        final byte mtype = 11;
        float[] matrixval = new float[9];
        matrix.getValues(matrixval);
        byte[] mdata = tobytes(matrixval);

        out.add(mtype).add(mdata.length).add(mdata);

        //rect
        final byte rtype = 12;

        byte[] rdata = tobytes(new float[]{mBound.left, mBound.top, mBound.right, mBound.bottom});
        L.d(TAG, "save " + mBound.toShortString() + " len " + rdata.length);
        out.add(rtype)
                .add(rdata.length)
                .add(rdata);

        return out;
    }


}