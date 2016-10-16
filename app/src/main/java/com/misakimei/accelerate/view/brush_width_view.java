package com.misakimei.accelerate.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.misakimei.accelerate.R;
import com.misakimei.accelerate.tool.L;
import com.rey.material.widget.Slider;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by 吴聪 on 2016/5/9.
 */
public class brush_width_view extends FrameLayout {
    private static final String TAG = "brush_width_view";
    View layout;
    @Nullable
    @InjectView(R.id.brush_stroke_width)
    Slider width;
    @Nullable
    @InjectView(R.id.brush_stroke_alpha)
    Slider alpha;
    @Nullable
    @InjectView(R.id.ponitview)
    PointView pointView;
    private Context mcontext;
    private int alphaSlide;
    private int strokeAlpha;
    private BrushChangeListener listener;


    public brush_width_view(@NonNull Context context) {
        this(context, null);
    }

    public brush_width_view(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        init();
    }

    public int getStrokeColor() {
        return pointView.getColor();
    }

    public float getStrokeSize() {
        return pointView.getSize();
    }

    public int getStrokeAlpha() {
        return pointView.getSrokeAlpha();
    }

    private void init() {
        layout = LayoutInflater.from(mcontext).inflate(R.layout.view_brush, null);
        addView(layout);
        ButterKnife.inject(this);
    }

    public void setWidthSlide(float size) {
        L.d(TAG, "brush_view setSlide Width " + size);

        float val = size / (width.getMaxValue() - width.getMinValue());
        L.d("size " + size + "  val " + val);
        val = val >= 1 ? 1 : val;
        width.setPosition(val, false);
    }

    public void setAlphaSlide(float size) {
        L.d(TAG, "brush_view setSlide alpha " + size + " " + (size / 255));
        alpha.setPosition(size / 255, false);
    }

    public void setPointSize(float size) {
        pointView.drawInCenter(size);
    }

    public void setPointAlpha(int size) {
        pointView.setAlpha(size);
    }

    public void setBrushChangeListener(final BrushChangeListener lis) {
        listener = lis;
        width.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                L.d(TAG, "Width onPositionChanged" + newPos + "  " + newValue);
                listener.OnStrokeWidthChange(newValue);
            }
        });
        alpha.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                L.d(TAG, "alpha onPositionChanged" + newPos + "  " + newValue);
                listener.OnStrokeAlphaChange(newValue);
            }
        });
    }


    public void setPointColor(int color) {
        pointView.setColor(color);
        L.d(TAG, "brushview setcolor " + color);
    }

    public interface BrushChangeListener {
        void OnStrokeWidthChange(float val);

        void OnStrokeAlphaChange(int val);

    }
}
