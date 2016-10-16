package com.misakimei.accelerate.tool;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.geom.Rect;

/**
 * Created by 18754 on 2016/8/7.
 */
public class DrawTool {
    @NonNull
    private static Paint p = new Paint();

    static {
        p.setColor(Color.RED);
    }

    public static void draw(@NonNull Canvas canvas, @NonNull Rect rect) {
        canvas.drawLine(rect.o.x, rect.o.y, rect.a.x, rect.a.y, p);
        canvas.drawLine(rect.a.x, rect.a.y, rect.b.x, rect.b.y, p);
        canvas.drawLine(rect.b.x, rect.b.y, rect.c.x, rect.c.y, p);
        canvas.drawLine(rect.c.x, rect.c.y, rect.o.x, rect.o.y, p);
    }
}
