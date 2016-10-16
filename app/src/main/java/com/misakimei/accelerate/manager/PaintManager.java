package com.misakimei.accelerate.manager;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.PaintStyle;
import com.misakimei.accelerate.ReInputStream;
import com.misakimei.accelerate.ReLoad;
import com.misakimei.accelerate.ReOutputStream;

import java.util.ArrayList;

/**
 * Created by 吴聪 on 2016/5/6.
 * 管理前台画笔
 */
public class PaintManager extends ReLoad {
    public static final String TAG = "PaintManager";
    private static PaintManager manager;
    //这就是全局的画笔
    @NonNull
    private static PaintStyle mPaintStyle = new PaintStyle(Color.BLACK, 255, 10, 0);
    public Paint PointPaint;
    private Paint tempPaint;
    private Paint linePaint;
    private Paint BoundPaint;

    private PaintManager() {
        init();
    }

    public static PaintManager getInstance() {
        if (manager == null) {
            manager = new PaintManager();
        }
        return manager;
    }

    /**
     * 获取全局画笔
     *
     * @return
     */
    @NonNull
    public PaintStyle getPaintStyle() {
        if (mPaintStyle == null) {
            mPaintStyle = new PaintStyle(Color.BLACK, 255, 10, 0);
        }
        return mPaintStyle;
    }

    public Paint getBoundPaint() {
        return BoundPaint;
    }

    private void init() {
        BoundPaint = new Paint();
        BoundPaint.setStrokeWidth(5);
        BoundPaint.setColor(Color.BLUE);
        BoundPaint.setAlpha(100);
        BoundPaint.setStyle(Paint.Style.FILL);


        tempPaint = new Paint(BoundPaint);
        tempPaint.setColor(Color.YELLOW);


        linePaint = new Paint();
        linePaint.setStrokeWidth(5);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.STROKE);

        PointPaint = new Paint(linePaint);
        PointPaint.setStrokeWidth(15);
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    @NonNull
    public Paint getTempPaint() {
        return new Paint(tempPaint);
    }

    @NonNull
    public Paint getTempPaint(int color) {
        Paint p = getTempPaint();
        p.setColor(color);
        return p;
    }

    public void setColor(int color) {
        mPaintStyle.setColor(color);
    }

    public void setSize(float size) {
        mPaintStyle.setSize(size);
    }

    public void setBlur(float blur) {
        mPaintStyle.setBlur(blur);
    }


    public void save(ArrayList<Byte> save) {
        mPaintStyle.save(save);
    }


    public void reset() {
        mPaintStyle.setColor(Color.BLACK);
        mPaintStyle.setAlpha(255);
        mPaintStyle.setBlur(0);
        mPaintStyle.setSize(10);
    }

    public void setAlpha(float alpha) {
        mPaintStyle.setAlpha(Math.round(alpha));
    }


    @Override
    public void init(ReInputStream in, int len) {
        mPaintStyle.recover(in);
    }

    @Override
    public ReOutputStream preserve(ReOutputStream out) {
        mPaintStyle.save(out);
        return out;
    }
}
