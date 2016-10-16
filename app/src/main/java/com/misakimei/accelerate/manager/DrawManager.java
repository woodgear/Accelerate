package com.misakimei.accelerate.manager;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.PaintStyle;
import com.misakimei.accelerate.geom.Point;
import com.misakimei.accelerate.path.PathCollection;
import com.misakimei.accelerate.path.VectorPath;
import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;

/**
 * Created by 吴聪 on 2016/5/5.
 */
public class DrawManager {


    public static final String TAG = "DRAWMANAGER";
    private static DrawManager manager;
    private Camera camera = Camera.getInstance();

    private CacheManager cacheManager = CacheManager.getInstance();

    private PathCollection pathCollection = PathCollection.getInstance();

    @Nullable
    private VectorPath mCurrentPath;

    private DrawManager() {
    }

    public static DrawManager getInstance() {
        if (manager == null) {
            manager = new DrawManager();
        }
        return manager;
    }

    public void draw(@NonNull Canvas canvas) {
        cacheManager.draw(canvas);
        if (mCurrentPath != null) {
            mCurrentPath.draw(canvas);//当前的还未加入到cache的
        }
    }


    public void pathStart(@NonNull Point p) {
        L.d(TAG, "pathStart");
        mCurrentPath = new VectorPath(new PaintStyle(PaintManager.getInstance().getPaintStyle()), Camera.getInstance().getMatrix());
        mCurrentPath.moveto(p);
    }

    public void pathMove(@NonNull Point p) {

        if (mCurrentPath != null) {
            mCurrentPath.move(p);
        }
    }


    public void pathEnd(boolean dete) {
        mCurrentPath.up();
        L.d(TAG, "up=>add");
        pathCollection.addPath(mCurrentPath.getPathRecord(dete));
        cacheManager.drawInCache(mCurrentPath);
        mCurrentPath = null;
    }

    public void changeStart(RectF rect) {
        camera.onChangeStart(rect);
        cacheManager.onChangeStart();
    }


    public void onChange(RectF rect) {
        camera.onChange(rect);
        cacheManager.onChange();
    }


    public void onChange(float dx, float dy, Point center, float ratio) {
        camera.onChange(dx, dy, center, ratio);
        cacheManager.onChange();
    }

    public void changeEnd() {
        camera.onChangeEnd();
        cacheManager.onChangeEnd();
    }

    public void undo() {
        PathCollection.getInstance().undo();
        cacheManager.render();
    }

    public void redo() {
        PathCollection.getInstance().redo();
        cacheManager.render();
    }

    public void timeMachine() {
//TODO 哎 垃圾啊
        cacheManager.clear();
        CommandManager.getInstance().postInvalidate();

        final ArrayList<VectorPath> paths = PathCollection.getInstance().getPathList(Camera.getInstance().getBound());
        L.d(TAG, "时光机开始");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);

                    for (int i = 0; i < paths.size(); i++) {
                        L.d(TAG, "时光机  " + i);
                        VectorPath p = paths.get(i);
                        cacheManager.drawInCache(p);
                        CommandManager.getInstance().postInvalidate();

                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();


    }


}
