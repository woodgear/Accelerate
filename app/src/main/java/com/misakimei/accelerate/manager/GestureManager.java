package com.misakimei.accelerate.manager;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.misakimei.accelerate.geom.Point;
import com.misakimei.accelerate.gestute.TwofingerGestureDetector;

/**
 * Created by 吴聪 on 2016/5/7.
 * 手势处理 在这里命令发给receiver
 */
public class GestureManager {
    private static final String TAG = "GestureManager";
    private static GestureManager gesture;
    TwofingerGestureDetector detector;
    CommandManager cmdMaster = CommandManager.getInstance();

    private GestureManager() {
        init();
    }

    public static GestureManager getInstance() {
        if (gesture == null) {
            gesture = new GestureManager();
        }
        return gesture;
    }

    private void init() {

        detector = new TwofingerGestureDetector(new TwofingerGestureDetector.OnTwofingerGestureListener() {
            @Override
            public void onDrawStart(@NonNull Point p) {
                cmdMaster.pathStart(p);
            }

            @Override
            public void onDrawMove(@NonNull Point p) {
                cmdMaster.pathMove(p);
            }

            @Override
            public void onDrawEnd() {
                cmdMaster.pathEnd();
            }

            @Override
            public void CameraChangeStart(RectF rect) {
                cmdMaster.changeStart(rect);
            }

            @Override
            public void CameraChange(RectF rect) {
                cmdMaster.onChange(rect);
            }


            @Override
            public void CameraChangeEnd() {
                cmdMaster.changeEnd();
            }

        });
    }

    public void onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
    }
}
