package com.misakimei.accelerate.manager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.Master;
import com.misakimei.accelerate.path.PathCollection;
import com.misakimei.accelerate.path.VectorPath;
import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;

/**
 * Created by 吴聪 on 2016/5/7.
 * 缓存啊 实际上相同大小的缓存估计就已经够了
 * TODO 现在缓存大小是写死的屏幕大小了
 */
public class CacheManager {

    public static final String TAG = "CacheManager";
    private static int CACHE_RATIO = 1;//缓冲大小
    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;
    @NonNull
    private Rect src = new Rect();
    @NonNull
    private RectF dst = new RectF();

    CacheManager() {
        //获取屏幕信息
        DisplayMetrics dm = Master.getContent().getResources().getDisplayMetrics();
        setSize(dm.widthPixels, dm.heightPixels);
    }

    @NonNull
    public static final CacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(cacheBitmap, src, dst, null);
    }

    public synchronized void render() {
        //   L.d(TAG, "render start");
        long start = System.currentTimeMillis();

        cacheCanvas.drawColor(Color.WHITE);
        RectF bound = Camera.getInstance().getBound();
        float ratio = cacheBitmap.getHeight() / bound.height();//TODO 假定视野就是手机屏幕大小的话 那这里实际上就是 invertMatrix
        Matrix matrix = new Matrix();
        matrix.postTranslate(-bound.left, -bound.top);
        matrix.postScale(ratio, ratio);
        ArrayList<VectorPath> list = PathCollection.getInstance().getPathList(bound);

        for (VectorPath path : list) {
            path.draw(cacheCanvas, matrix, ratio);
        }
        long end = System.currentTimeMillis();
        //L.d(TAG, "花费 "+(end - start)+"毫秒  绘制 "+list.size()+"条  数据空间中 "+PathCollection.getInstance().size()+"条 视窗"+bound.toShortString());
        src = new Rect(0, 0, cacheCanvas.getWidth(), cacheCanvas.getHeight());//重置src
        CommandManager.getInstance().postInvalidate();
        CommandManager.getInstance().setSave(false);
    }

    public void onChangeStart() {

    }

    //TODO 贴合失败 应该通过位图的缩放反推矢量图的缩放? 直接render 233 真是大写的尴尬
    public void onChange() {
        render();
    }

    public void onChangeEnd() {
        L.d(TAG, "CacheManager onChangeEnd start");
        render();
    }

    public void drawInCache(@NonNull VectorPath mCurrentVectorPath) {
        cacheCanvas.save();
        cacheCanvas.translate(src.left, src.top);
        mCurrentVectorPath.draw(cacheCanvas);
        cacheCanvas.restore();
        CommandManager.getInstance().setSave(false);
    }

    public void setSize(float width, float height) {
        cacheBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);
        src.set(0, 0, (int) width, (int) height);
        dst = new RectF(src);

    }

    public void clear() {
        cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawColor(Color.WHITE);
    }

    public void reset() {
        DisplayMetrics dm = Master.getContent().getResources().getDisplayMetrics();
        src.set(0, 0, dm.widthPixels, dm.heightPixels);
        dst = new RectF(src);
        cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawColor(Color.WHITE);
    }

    private static class SingletonHolder {
        private static final CacheManager INSTANCE = new CacheManager();
    }
}
