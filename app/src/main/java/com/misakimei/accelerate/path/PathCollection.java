package com.misakimei.accelerate.path;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.ReInputStream;
import com.misakimei.accelerate.ReLoad;
import com.misakimei.accelerate.ReOutputStream;
import com.misakimei.accelerate.tool.L;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by 吴聪 on 2016/5/7.
 * TODO 查找性能优化！！
 */
public class PathCollection extends ReLoad implements Iterable<VectorPath> {


    private static final String TAG = "PathCollection";
    private static final String MODE = "{name:PathCollection,size:%d}";

    private static PathCollection collection;

    @NonNull
    LinkedList<VectorPath> theWrold = new LinkedList<>();

    private int size = 0;

    public static PathCollection getInstance() {
        if (collection == null) {
            collection = new PathCollection();
        }
        return collection;
    }


    public void addPath(VectorPath path) {
        //undo 之后又画了 那么背undo的就可以舍掉
        if (size != theWrold.size()) {
            while (size != theWrold.size()) {
                theWrold.removeLast();
            }
        }
        theWrold.add(path);
        size = theWrold.size();
    }


    @NonNull
    public ArrayList<VectorPath> getPathList(@NonNull RectF bound) {
        ArrayList<VectorPath> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            VectorPath path = theWrold.get(i);
            if (path.intersects(bound)) {
                list.add(path);
            }
        }
        return list;
    }

    public void undo() {
        if (size != 0) {
            size = size - 1;
        }
    }

    public void redo() {
        if (size < theWrold.size()) {
            size = size + 1;

        }
    }

    public void reset() {
        theWrold.clear();
        size = 0;
    }

    public int size() {
        return theWrold.size();
    }


    @Override
    public String toString() {
        return String.format(Locale.CHINA, MODE, size);

    }

    @NonNull
    public ArrayList<VectorPath> getAllPath() {
        return new ArrayList<>(theWrold);
    }


    @Override
    public void init(@NonNull ReInputStream in, int len) {
        int size = in.toInt();
        for (int i = 0; i < size; i++) {
            VectorPath p = new VectorPath();
            p.recover(in);
            L.d(p.toString());
            addPath(p);
        }
    }

    @NonNull
    @Override
    public ReOutputStream preserve(@NonNull ReOutputStream out) {

        out.add(theWrold.size());

        for (int i = 0; i < size(); i++) {
            VectorPath p = theWrold.get(i);
            p.save(out);
        }
        return out;
    }

    @NonNull
    @Override
    public Iterator<VectorPath> iterator() {
        return theWrold.iterator();
    }
}
