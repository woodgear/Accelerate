package com.misakimei.accelerate.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.misakimei.accelerate.R;


/**
 * Created by 吴聪 on 2016/5/25.
 */
public class SettingView extends FrameLayout {
    View layout;
    SwitchCompat aSwitch;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    private Context mcontext;

    public SettingView(@NonNull Context context) {
        super(context);
        this.mcontext = context;
        init();
    }

    public void setCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.checkedChangeListener = listener;
    }

    private void init() {
        layout = LayoutInflater.from(mcontext).inflate(R.layout.view_set, null);
        addView(layout);
        aSwitch = (SwitchCompat) layout.findViewById(R.id.shapemode);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }
}
