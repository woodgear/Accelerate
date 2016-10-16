package com.misakimei.accelerate;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import static com.misakimei.accelerate.tool.ConvertData.getByteList;


/**
 * Created by 吴聪 on 2016/5/5.
 * 记录画笔信息
 */
public class PaintStyle extends ReLoad {


    private static final String TAG = "PaintStyle";

    private int color = Color.BLACK;
    private int alpha = 255;
    private float blur = 0;
    private float size = 10;

    @NonNull
    private Paint paint = new Paint();

    public PaintStyle(int color, int alpha, float size, float blur) {
        this.color = color;
        this.alpha = alpha;
        this.blur = blur;
        this.size = size;
        setPaint(paint, this);
    }

    public PaintStyle() {
        setPaint(paint, this);
    }

    public PaintStyle(@NonNull PaintStyle style) {
        this.color = style.color;
        this.alpha = style.alpha;
        this.blur = style.blur;
        this.size = style.size;
    }

    private static int setAlphaToColor(int c, int alpha) {
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
    }

    public int getColor() {
        return setAlphaToColor(color, alpha);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColor(String color) {
        this.color = Color.parseColor(color);
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public float getBlur() {
        return blur;
    }

    public void setBlur(float blur) {
        this.blur = blur;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    @NonNull
    @Override
    public String toString() {
        return "{name:PaintStyle,color: " + color + ",alpha: " + alpha + ",blur: " + blur + ",size: " + size + "}";
    }

    public void setPaint(@NonNull Paint paint, @NonNull PaintStyle style) {
        paint.setColor(color);
        if (alpha != 0) {
            paint.setAlpha(style.alpha);
        }
        if (style.blur != 0) {
            BlurMaskFilter maskFilter = new BlurMaskFilter(style.blur, BlurMaskFilter.Blur.NORMAL);
            paint.setMaskFilter(maskFilter);
        }
        paint.setStrokeWidth(style.size);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

    }

    @NonNull
    public Paint getPaint() {
        Paint paint = new Paint();
        setPaint(paint, this);
        return paint;
    }

    public void save(@NonNull ArrayList<Byte> save) {
        save.addAll(getByteList(new int[]{color, alpha}));
        save.addAll(getByteList(new float[]{blur, size}));
    }

    @Override
    public void init(@NonNull ReInputStream in, int len) {
        int[] data1 = new int[2];
        float[] data2 = new float[2];
        in.get(data1).get(data2);
        color = data1[0];
        alpha = data1[1];

        blur = data2[0];
        size = data2[1];
        setPaint(paint, this);

    }

    @Override
    public ReOutputStream preserve(@NonNull ReOutputStream out) {
        return out.add(new int[]{color, alpha}).add(new float[]{blur, size});
    }
}
