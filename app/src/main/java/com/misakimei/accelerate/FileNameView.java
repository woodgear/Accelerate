package com.misakimei.accelerate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.misakimei.accelerate.tool.Tool;


/**
 * Created by 吴聪 on 2016/6/4.
 */
public class FileNameView extends FrameLayout {
    OnFileNameGetedListener listener;
    private EditText editText;

    public FileNameView(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_filename, null);

        addView(view);
        editText = (EditText) view.findViewById(R.id.filename);
        editText.setText(Tool.getTime());

        view.findViewById(R.id.filenameconfirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDecide(editText.getText().toString());
            }
        });

    }

    public void setListener(OnFileNameGetedListener listener) {
        this.listener = listener;
    }

    public interface OnFileNameGetedListener {
        void onDecide(String filename);
    }

}
