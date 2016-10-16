package com.misakimei.accelerate.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.misakimei.accelerate.R;


/**
 * Created by 吴聪 on 2016/6/10.
 */
public class LoadView extends FrameLayout {
    View root;
    TextView textView;

    public LoadView(@NonNull Context context) {
        super(context);
        root = LayoutInflater.from(context).inflate(R.layout.view_load, null);
        addView(root);
        textView = (TextView) root.findViewById(R.id.loadText);
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
