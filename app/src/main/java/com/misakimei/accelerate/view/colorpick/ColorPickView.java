package com.misakimei.accelerate.view.colorpick;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.misakimei.accelerate.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 吴聪 on 2016/5/9.
 */
public class ColorPickView extends FrameLayout {
    private static final String TAG = "ColorPickView";
    View layout;
    @Nullable
    @InjectView(R.id.picker)
    ColorPicker colorPicker;
    @Nullable
    @InjectView(R.id.svbar)
    SVBar svBar;
    private Context mcontext;
    private ColorPicker.OnColorChangedListener listener;

    public ColorPickView(@NonNull Context context) {
        super(context);
        this.mcontext = context;
        init();
    }

    public void setOnColorChangedListener(ColorPicker.OnColorChangedListener lis) {
        this.listener = lis;
        colorPicker.setOnColorChangedListener(listener);
    }

    private void init() {
        layout = LayoutInflater.from(mcontext).inflate(R.layout.view_colorpick, null);
        addView(layout);
        ButterKnife.inject(this);
        colorPicker.addSVBar(svBar);
    }
}
