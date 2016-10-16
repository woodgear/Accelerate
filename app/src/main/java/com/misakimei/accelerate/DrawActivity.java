package com.misakimei.accelerate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.manager.CommandManager;
import com.misakimei.accelerate.manager.DRAWMODE;
import com.misakimei.accelerate.manager.ViewManager;
import com.misakimei.accelerate.tool.L;
import com.misakimei.accelerate.view.BasicActivity;

import java.io.File;

//TODO 这么多的单例 实际上就是全局变量 我担心驾驭不住啊
//通过加载控件来设置模式
//绘制模式
//
public class DrawActivity extends BasicActivity {

    private static final String TAG = "Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewManager.getInstance().setContentView(findViewById(R.id.container), this);

        CommandManager.getInstance().setActivity(this);


        L.d(TAG, "DrawActivity  onCreate");

        Intent intent = getIntent();

        dealIntent(intent);

        //初始化ViewManage
    }

    private void dealIntent(@NonNull Intent intent) {
        String from = intent.getStringExtra("from");
        L.d(TAG, "from  " + from);

        switch (from) {
            case "NEW":
                changeToDrawMode();
                break;
            case "EXPLOER":
                String file = intent.getStringExtra("file");
                File data = new File(Master.getVectorDir(), file + ".vdg");
                CommandManager.getInstance().read(data);
                changeToWatchMode();
                break;
            case "DRAW"://需要清空
                CommandManager.getInstance().clear();
                changeToDrawMode();
                break;
        }
    }


    private void changeToWatchMode() {
        CommandManager.getInstance().setMode(DRAWMODE.WATCH);
    }

    private void changeToDrawMode() {
        L.d(TAG, "切换到绘制模式");
        CommandManager.getInstance().setMode(DRAWMODE.DRAW);
    }


    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        dealIntent(intent);
    }
}
