package com.misakimei.accelerate.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.misakimei.accelerate.tool.L;

/**
 * Created by 吴聪 on 2016/5/9.
 * 现在就先固定大小吧
 */
public class PointView extends View {

    private static final String TAG = "PointView";
    private Paint paint;
    private Paint boundpaint;

    private Context mcontext;
    private float diameter = 10;
    private float tempdia = diameter;
    private int color;
    private float size;


    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        boundpaint = new Paint();
        boundpaint.setColor(Color.BLACK);
        boundpaint.setStyle(Paint.Style.STROKE);
        boundpaint.setStrokeWidth(1);
        boundpaint.setAntiAlias(true);
    }

    public void expand(float ratio) {
        drawInCenter(diameter);
    }

    public void setDiameter(float dia) {
        this.diameter = dia;
    }

    public void drawInCenter(float dia) {
        tempdia = dia;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.d(TAG, "touch");
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawCircle(canvas.getClipBounds().centerX(), canvas.getClipBounds().centerY(), tempdia / 2, paint);
        canvas.drawCircle(canvas.getClipBounds().centerX(), canvas.getClipBounds().centerY(), tempdia / 2, boundpaint);

    }

    public void setAlpha(float alpha) {
        paint.setAlpha(Math.round(alpha));
        postInvalidate();
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public int getSrokeAlpha() {
        return paint.getAlpha();
    }

    public float getSize() {
        return tempdia;
    }
}