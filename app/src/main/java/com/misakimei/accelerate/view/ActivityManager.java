package com.misakimei.accelerate.view;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.LinkedList;

/**
 * Created by 吴聪 on 2016/6/11.
 */
public class ActivityManager {
    private static ActivityManager manager;
    @NonNull
    LinkedList<Activity> activities = new LinkedList<>();

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (manager == null) {
            manager = new ActivityManager();
        }
        return manager;
    }

    public void add(Activity activity) {
        activities.add(activity);
    }

    public void clear() {
        while (!activities.isEmpty()) {
            activities.removeLast().finish();
        }
    }

    public void finshCurrent() {
        activities.removeLast().finish();
    }


    public void finish(@NonNull Activity activity) {
        if (activities.getLast().equals(activity)) {
            activities.removeLast().finish();
        } else {
            activities.remove(activity);
            activity.finish();
        }
    }
}
