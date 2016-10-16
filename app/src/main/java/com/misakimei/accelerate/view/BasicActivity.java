package com.misakimei.accelerate.view;

import android.app.Activity;
import android.os.Bundle;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by 吴聪 on 2016/6/8.
 */
public abstract class BasicActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, "");
    }
}
