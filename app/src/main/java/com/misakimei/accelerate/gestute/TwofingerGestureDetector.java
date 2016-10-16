package com.misakimei.accelerate.gestute;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.misakimei.accelerate.geom.GeomTool;
import com.misakimei.accelerate.geom.Point;
import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;

/**
 * Created by 吴聪 on 2016/5/6.
 * 绘画手势解析器
 * 可以识别 单指绘制 双指拖动 双指缩放
 */
public class TwofingerGestureDetector {

    public static final String TAG = "GestureDetector";
    private static final float MIN_TOUCH = 0.1f;
    @Nullable
    private OnTwofingerGestureListener listener = null;
    @NonNull
    private GestureState state = GestureState.UNKNOW;
    private MotionEvent preEvent;
    @NonNull
    private ArrayList<Side> switchs = new ArrayList<>();

    {
        switchs.add(new Side_3_Unknow());
        switchs.add(new Side_5_Draw());
        switchs.add(new Side_5_Unknow());
        switchs.add(new Side_6_Draw());
        switchs.add(new Side_7_Draw());

        switchs.add(new Side_9_Unknow());
        switchs.add(new Side_10_Control());
        switchs.add(new Side_10_Unknow());
        switchs.add(new Side_11_Control());
    }

    public TwofingerGestureDetector(OnTwofingerGestureListener listener) {
        this.listener = listener;
    }

    public void setListener(OnTwofingerGestureListener listener) {
        this.listener = listener;
    }

    public void onTouchEvent(MotionEvent cur) {
        turn(preEvent, cur, state);
        preEvent = MotionEvent.obtain(cur);
    }

    //单指绘制

    private void turn(@Nullable MotionEvent pre, MotionEvent cur, GestureState state) {
        if (pre == null) {
            return;
        }

        for (Side s : switchs) {
            if (s.check(pre, cur, state)) {
                s.run(pre, cur);
                break;
            }
        }
    }

    @NonNull
    private RectF getRect(@NonNull Point p1, @NonNull Point p2) {
        float left, top, right, bottom;
        if (p1.x > p2.x) {
            left = p2.x;
            right = p1.x;
        } else {
            right = p2.x;
            left = p1.x;
        }
        if (p1.y > p2.y) {
            top = p2.y;
            bottom = p1.y;
        } else {
            bottom = p1.y;
            top = p2.y;
        }

        return new RectF(left, top, right, bottom);
    }

    @NonNull
    private ArrayList<Point> getHistory(@NonNull MotionEvent cur) {
        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < cur.getHistorySize(); i++) {
            list.add(new Point(cur.getHistoricalX(i), cur.getHistoricalY(i)));
        }
        return list;
    }

    private enum GestureState {
        DRAW,
        CONTROL,
        UNKNOW,
    }

    public interface OnTwofingerGestureListener {
        void onDrawStart(Point p);

        void onDrawMove(Point p);

        void onDrawEnd();

        void CameraChangeStart(RectF rect);

        void CameraChange(RectF rect);

        void CameraChangeEnd();
    }

    private abstract class Side {
        abstract boolean check(MotionEvent pre, MotionEvent cur, GestureState state);

        abstract void run(MotionEvent pre, MotionEvent cur);

        //只有确保MotionEvent持有两个触控点时才有意义 本方法并不检查
        float toDis(@NonNull MotionEvent event) {
            Point p0 = new Point(event.getX(0), event.getY(0));
            Point p1 = new Point(event.getX(1), event.getY(1));
            return GeomTool.dis(p0, p1);
        }

        //检测a与b之间有没有差距
        boolean diff(float a, float b) {
            return Math.abs(a - b) > 0.1;
        }

        @NonNull
        Point getPoint(@NonNull MotionEvent event, int index) {
            return new Point(event.getX(index), event.getY(index));
        }

    }

    class Side_3_Unknow extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.UNKNOW
                    && pre.getPointerCount() == 1 && pre.getActionMasked() == MotionEvent.ACTION_DOWN
                    && cur.getPointerCount() == 1 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            L.d("Side_3_Unknow");
            Point p1 = new Point(pre.getX(), pre.getY());
            Point p2 = new Point(cur.getX(), cur.getY());
            if (GeomTool.dis(p1, p2) > MIN_TOUCH) {
                state = GestureState.DRAW;
                listener.onDrawStart(p1);
            }
        }
    }

    class Side_5_Draw extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.DRAW
                    && pre.getPointerCount() == 1 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 1 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            L.d("Side_5_Draw");


            Point p1 = new Point(pre.getX(), pre.getY());
            Point p2 = new Point(cur.getX(), cur.getY());
            if (GeomTool.dis(p1, p2) > MIN_TOUCH) {

                ArrayList<Point> his = getHistory(cur);
                L.d("his " + his.size());
                for (Point p : his) {
                    listener.onDrawMove(p);
                }
                listener.onDrawMove(p2);
            }
        }
    }

    class Side_5_Unknow extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.UNKNOW
                    && pre.getPointerCount() == 1 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 1 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            L.d("Side_5_Unknow");
            Point p1 = new Point(pre.getX(), pre.getY());
            Point p2 = new Point(cur.getX(), cur.getY());
            if (GeomTool.dis(p1, p2) > MIN_TOUCH) {
                state = GestureState.DRAW;
                listener.onDrawStart(p1);
            }
        }
    }

    class Side_6_Draw extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.DRAW
                    && pre.getPointerCount() == 1 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 1 && cur.getActionMasked() == MotionEvent.ACTION_UP;
        }

        @Override
        void run(MotionEvent pre, MotionEvent cur) {
            L.d("Side_6_Draw");
            listener.onDrawEnd();
            state = GestureState.UNKNOW;
        }
    }

    class Side_7_Draw extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.DRAW
                    && pre.getPointerCount() == 1 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 2 && cur.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN;
        }

        @Override
        void run(MotionEvent pre, MotionEvent cur) {
            L.d("Side_7_Draw");
            listener.onDrawEnd();
            state = GestureState.UNKNOW;
        }
    }

    //双指操作
    class Side_9_Unknow extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {

            return state == GestureState.UNKNOW
                    && pre.getPointerCount() == 2 && pre.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN
                    && cur.getPointerCount() == 2 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            L.d("Side_9_Unknow");
            float predis = toDis(pre);
            float curdis = toDis(cur);
            if (diff(predis, curdis)) {
                state = GestureState.CONTROL;
                L.d(TAG, "判定双指操作 开始");
                listener.CameraChangeStart(getRect(getPoint(pre, 0), getPoint(pre, 1)));
            }
        }
    }

    class Side_10_Control extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.CONTROL
                    && pre.getPointerCount() == 2 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 2 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            L.d("Side_10_Control");
            float predis = toDis(pre);
            float curdis = toDis(cur);
            if (diff(predis, curdis)) {
                listener.CameraChange(getRect(getPoint(pre, 0), getPoint(pre, 1)));
            }
        }
    }

    class Side_10_Unknow extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.UNKNOW
                    && pre.getPointerCount() == 2 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 2 && cur.getActionMasked() == MotionEvent.ACTION_MOVE;
        }

        @Override
        void run(@NonNull MotionEvent pre, @NonNull MotionEvent cur) {
            float predis = toDis(pre);
            float curdis = toDis(cur);

            if (diff(predis, curdis)) {
                state = GestureState.CONTROL;
                listener.CameraChangeStart(getRect(getPoint(pre, 0), getPoint(pre, 1)));
            }
        }

    }

    class Side_11_Control extends Side {

        @Override
        boolean check(@NonNull MotionEvent pre, @NonNull MotionEvent cur, GestureState state) {
            return state == GestureState.CONTROL
                    && pre.getPointerCount() == 2 && pre.getActionMasked() == MotionEvent.ACTION_MOVE
                    && cur.getPointerCount() == 2 && cur.getActionMasked() == MotionEvent.ACTION_POINTER_UP;
        }

        @Override
        void run(MotionEvent pre, MotionEvent cur) {
            L.d("Side_11_Control");

            state = GestureState.UNKNOW;
            listener.CameraChangeEnd();
        }
    }
}
