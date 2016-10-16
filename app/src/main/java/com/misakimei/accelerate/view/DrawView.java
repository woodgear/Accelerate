package com.misakimei.accelerate.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.misakimei.accelerate.manager.CommandManager;
import com.misakimei.accelerate.manager.DrawManager;
import com.misakimei.accelerate.manager.GestureManager;

/**
 * Created by 吴聪 on 2016/5/4.
 * 自定义绘制控件
 */
public class DrawView extends View {

    private static final String TAG = "DRAW";
    DrawManager drawManager = DrawManager.getInstance();
    GestureManager gestureManager = GestureManager.getInstance();
    CommandManager commandManager = CommandManager.getInstance();

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        commandManager.setView(this, context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureManager.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawManager.draw(canvas);
    }
}