package com.misakimei.accelerate.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.DrawActivity;
import com.misakimei.accelerate.Master;
import com.misakimei.accelerate.exception.RecoverFailException;
import com.misakimei.accelerate.geom.Point;
import com.misakimei.accelerate.path.PathCollection;
import com.misakimei.accelerate.tool.L;

import java.io.File;
import java.io.IOException;

/**
 * Created by 吴聪 on 2016/5/7.
 */
public class CommandManager {
    //TODO CommandManager 承担的东西有些多了

    private static final String TAG = "CommandManager";
    private static CommandManager manager;
    @NonNull
    DRAWMODE mode = DRAWMODE.DRAW;
    private Context mContext;
    private boolean isShapeDetect = false;
    private Handler handle;//TODO 不应该吧
    private DrawActivity activity;
    private DrawManager drawManager = DrawManager.getInstance();
    private View DrawView;
    @Nullable
    private String mfilename;
    private boolean isSave = false;

    private CommandManager() {
        init();
    }

    public static CommandManager getInstance() {
        if (manager == null) {
            manager = new CommandManager();
        }
        return manager;
    }

    private void init() {
        mContext = Master.getContent();
    }

    public void pathStart(@NonNull Point p) {
        L.d(TAG, "pathStart");
        drawManager.pathStart(p);
    }

    public void pathMove(@NonNull Point p) {
        L.d(TAG, "pathMove");

        drawManager.pathMove(p);
        postInvalidate();
    }

    public void pathEnd() {
        L.d(TAG, "pathEnd");
        drawManager.pathEnd(isShapeDetect);
        postInvalidate();

    }

    public void changeStart(RectF rect) {
        drawManager.changeStart(rect);
    }

    public void onChange(RectF rect) {
        drawManager.onChange(rect);
        postInvalidate();
    }

    public void changeEnd() {
        drawManager.changeEnd();
    }

    public void undo() {
        drawManager.undo();
        postInvalidate();
    }

    public void redo() {
        drawManager.redo();
        postInvalidate();
    }

    public void postInvalidate() {
        DrawView.postInvalidate();
    }

    public void setView(View view, Context context) {
        this.DrawView = view;
        this.mContext = context;
    }

    public void paintColorChange(int color, int alpha, float size) {
        PaintManager.getInstance().setColor(color);
        PaintManager.getInstance().setAlpha(alpha);
        PaintManager.getInstance().setSize(size);
    }

    public boolean isShapeDetect() {
        return isShapeDetect;
    }

    public void setShapeDetectMode(boolean shapeDetectMode) {
        this.isShapeDetect = shapeDetectMode;
    }

    public void timeMachine() {
        DrawManager.getInstance().timeMachine();
    }

    public void save(String filename) {
        SaveTool.save(filename);

    }

    public void read(@NonNull File vdata) {
        try {
            SaveTool.recover(vdata);
            CacheManager.getInstance().render();
            postInvalidate();
        } catch (RecoverFailException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = vdata.getName();
        name = name.substring(0, name.length() - 4);
        mfilename = name;
        isSave = true;
    }

    public void jumptoDraw(String from) {
        Intent intent = new Intent("draw");
        intent.putExtra("from", from);
        mContext.startActivity(intent);
    }

    public void jumptoExploer(String from) {
        Intent intent = new Intent("exploer");
        intent.putExtra("from", from);
        mContext.startActivity(intent);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setMode(@NonNull DRAWMODE mode) {
        this.mode = mode;
        ViewManager.getInstance().changeMode(mode);
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        this.isSave = save;
    }

    @Nullable
    public String getFileName() {
        return mfilename;
    }

    public void setFileName(String name) {
        mfilename = name;
    }


    //新建之前要清空
    //Q:话说为什么我要写成单例的感觉?
    //A:不写成单例 互先直接怎么通信
    public void clear() {
        Camera.getInstance().reset();
        PaintManager.getInstance().reset();
        PathCollection.getInstance().reset();
        CacheManager.getInstance().reset();
        CacheManager.getInstance().render();
        this.reset();
    }

    private void reset() {
        mfilename = null;
        isShapeDetect = false;
    }

    public void save(final String filename, @NonNull final AfterListener afterListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                save(filename);
                afterListener.after();
            }
        });

        thread.start();
    }

    public void delete(final String fileName, @NonNull final AfterListener afterListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                delete(fileName);
                afterListener.after();
            }
        });

        thread.start();
    }

    private void delete(String fileName) {
        L.d(TAG, "delte " + fileName);
        File thumb = new File(Master.getThumbDir(), fileName + ".png");
        File vdata = new File(Master.getVectorDir(), fileName + ".vdg");
        thumb.delete();
        vdata.delete();
    }


    public void setHandle(Handler handle) {
        this.handle = handle;
    }


    public void share() {
        Toast.makeText(mContext, "Not Now", Toast.LENGTH_SHORT).show();
    }


    public void setActivity(DrawActivity activity) {
        this.activity = activity;
    }


}
