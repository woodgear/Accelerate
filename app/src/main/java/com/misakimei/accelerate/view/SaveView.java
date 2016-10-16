package com.misakimei.accelerate.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.misakimei.accelerate.R;


/**
 * Created by 吴聪 on 2016/6/4.
 */
public class SaveView extends FrameLayout {
    SaveListener listener;

    public SaveView(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_save, null);
        addView(view);
        view.findViewById(R.id.saveconfirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnConfirm();
            }
        });
        view.findViewById(R.id.savecancle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.Cancle();
            }
        });


    }

    public void setListener(SaveListener listener) {
        this.listener = listener;
    }

    public interface SaveListener {
        void OnConfirm();

        void Cancle();
    }
}
