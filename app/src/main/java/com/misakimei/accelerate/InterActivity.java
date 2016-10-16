package com.misakimei.accelerate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.misakimei.accelerate.view.BasicActivity;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 启动Activity 目前就让她通过是否有历史文件跳向draw 或者 exploer
 */
public class InterActivity extends BasicActivity {

    private static final String TAG = "InterActivity";
    @Nullable
    @InjectView(R.id.create)
    Button create;
    @Nullable
    @InjectView(R.id.exploer)
    Button exploer;

    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter);
        ButterKnife.inject(this);
        mcontext = this;

        boolean hasFile;
        if (Master.getThumbDir().listFiles() == null) {
            hasFile = false;
        } else {
            hasFile = Master.getThumbDir().listFiles().length != 0;
        }

        if (hasFile) {
            Intent intent = new Intent("exploer");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mcontext.startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent("draw");
            intent.putExtra("from", "NEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mcontext.startActivity(intent);
            finish();
        }


        MI_Update();


    }

    private void MI_Update() {
        XiaomiUpdateAgent.update(this);
    }
}
