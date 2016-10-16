package com.misakimei.accelerate;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.misakimei.accelerate.geom.GeomTool;
import com.misakimei.accelerate.geom.Point;

import java.util.Locale;


/**
 * Created by 吴聪 on 2016/5/5.
 * TODO 受浮点精度制约 抖动3.4E-6 到1.8E35丢失
 */
public class Camera extends ReLoad {

    public static final String MODE = "{name:CAMERA,HEIGHT=%f,WIDTH=%f,OFFSETX:%f,OFFSETY:%f,RATIO:%f}";
    private static final String TAG = "CAMERA";
    private static float mratio = 1f;
    RectF orgin;
    Point oc;
    float len;
    @NonNull
    private RectF horizon = new RectF();
    @NonNull
    private RectF prehorizon = new RectF();
    private float mwidth;
    private float mheight;
    private float moffsetX = 0;
    private float moffsetY = 0;
    private float preratio = 1f;
    @Nullable
    private PointF precenter = null;
    @NonNull
    private Matrix mmatrix = new Matrix();
    @NonNull
    private Matrix minversematrix = new Matrix();

    private Camera() {
        DisplayMetrics dm = Master.getContent().getResources().getDisplayMetrics();
        setSize(dm.widthPixels, dm.heightPixels);
        moffsetY = 0;
        moffsetX = 0;
        mmatrix.reset();
        minversematrix.reset();
        mratio = 1f;
        preratio = 1;
        prehorizon = new RectF(horizon);
    }

    @NonNull
    public static Camera getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void onChangeStart(@NonNull RectF rect) {
        orgin = rect;
        oc = new Point(orgin.centerX(), orgin.centerY());
        len = GeomTool.sideLen(rect);
        prehorizon = new RectF(horizon);
        preratio = mratio;
    }

    public void onChange(@NonNull RectF rect) {
        Point ncenter = new Point(rect.centerX(), rect.centerY());
        onChange(oc.x - ncenter.x, oc.y - ncenter.y, ncenter, len / GeomTool.sideLen(rect));
    }

    //TODO 什么才是真正的贴合?
    public void onChange(float offsetX, float offsetY, @NonNull Point center, float ratio) {
        //位移要按照 现实世界来算 用毫米之类的?

        float newwidth = prehorizon.width() * ratio;
        float newheight = prehorizon.height() * ratio;

        precenter = new PointF(center.x - offsetX, center.y - offsetY);
        precenter = translate(precenter);

        float ox = (precenter.x - prehorizon.left);
        float oy = (precenter.y - prehorizon.top);

        horizon.left = precenter.x - ox * ratio;
        horizon.top = precenter.y - oy * ratio;
        horizon.right = horizon.left + newwidth;
        horizon.bottom = horizon.top + newheight;

        offsetX *= getRatio();
        offsetY *= getRatio();
        horizon.offset(offsetX, offsetY);

        moffsetX = horizon.left;
        moffsetY = horizon.top;
        mratio = preratio * ratio;

    }

    public void onChangeEnd() {
        if (mratio < 1.0e-5 || mratio > 1.0e35) {
            mratio = preratio;
            horizon = new RectF(prehorizon);
        }
        updateMatrix();
    }

    private void updateMatrix() {

        mmatrix.reset();
        mmatrix.postScale(mratio, mratio);
        mmatrix.postTranslate(moffsetX, moffsetY);

        minversematrix.reset();
        minversematrix.postTranslate(-moffsetX, -moffsetY);
        minversematrix.postScale(1 / mratio, 1 / mratio);
    }

    @NonNull
    public PointF translate(float x, float y) {
        float f[] = new float[]{x, y};
        mmatrix.mapPoints(f);
        return new PointF(f[0], f[1]);
    }

    @NonNull
    public PointF translate(@NonNull PointF p) {
        return translate(p.x, p.y);
    }

    public void setSize(float width, float height) {
        mwidth = width;
        mheight = height;
        horizon.set(0, 0, width, height);
    }

    @NonNull
    public RectF getBound() {
        return new RectF(horizon);
    }

    public float getRatio() {
        return mratio;
    }

    @NonNull
    public Matrix getInverseMatrix() {
        return minversematrix;
    }

    @NonNull
    public Matrix getMatrix() {
        return new Matrix(mmatrix);
    }

    public float getOffX() {
        return moffsetX;
    }

    public float getOffY() {
        return moffsetY;
    }

    @Override
    public String toString() {
        return String.format(Locale.CHINA, MODE, mheight, mwidth, moffsetX, moffsetY, mratio);
    }

    public void reset() {
        init();
    }

    public float getHeight() {
        return mheight;
    }

    public float getWeight() {
        return mwidth;
    }

    @Override
    public ReOutputStream preserve(@NonNull ReOutputStream out) {
        return out.add(new float[]{mheight, mwidth, moffsetX, moffsetY, horizon.right, horizon.bottom, mratio});
    }

    @Override
    public void init(@NonNull ReInputStream in, int len) {

        float[] v = new float[7];
        in.get(v);

        float height = v[0];
        float width = v[1];
        moffsetX = v[2];
        moffsetY = v[3];
        horizon.set(moffsetX, moffsetY, v[4], v[5]);
        mratio = v[6];

        mwidth = width;
        mheight = height;

        updateMatrix();
    }

    private void init() {
        DisplayMetrics dm = Master.getContent().getResources().getDisplayMetrics();
        setSize(dm.widthPixels, dm.heightPixels);
        moffsetY = 0;
        moffsetX = 0;
        mmatrix.reset();
        minversematrix.reset();
        mratio = 1f;
        preratio = 1;
        prehorizon = new RectF(horizon);
    }

    private static class SingletonHolder {
        private static final Camera INSTANCE = new Camera();
    }

}
