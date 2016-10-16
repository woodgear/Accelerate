package com.misakimei.accelerate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by 吴聪 on 2016/6/4.
 * TODO 那么为了斩断view和mode之间的联系 为了解耦到底该怎么做呢?
 * 例如这里的optionview 她现在有四个调用点
 */


public class OptionView extends FrameLayout {
    OnOptionViewItemClickListener lis;

    public OptionView(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_option, null, false);
        addView(view);

        view.findViewById(R.id.create).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lis.onCreateClick(v);
            }
        });

        view.findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lis.onShareClick(v);
            }
        });

        view.findViewById(R.id.exploer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lis.onExploerClick(v);
            }
        });

        view.findViewById(R.id.save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lis.onSaveClick(v);
            }
        });


    }

    public void SetOnOptionViewItemClickListener(OnOptionViewItemClickListener lis) {
        this.lis = lis;
    }

    public interface OnOptionViewItemClickListener {
        void onCreateClick(View view);

        void onShareClick(View view);

        void onExploerClick(View view);

        void onSaveClick(View view);
    }

}
