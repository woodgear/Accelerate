package com.misakimei.accelerate;

import android.app.Application;
import android.content.Context;

import com.misakimei.accelerate.tool.L;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.io.File;

import static com.xiaomi.mistatistic.sdk.MiStatInterface.UPLOAD_POLICY_REALTIME;

/**
 * Created by 吴聪 on 2016/6/3.
 */
public class Master extends Application {
    private static final String TAG = "Master";

    private static Context content;


    private static File thumbnail;
    private static File vector;

    public static Context getContent() {
        return content;
    }

    public static File[] getThumbs() {
        return thumbnail.listFiles();
    }

    public static File getVectorDir() {
        return vector;
    }

    public static File getThumbDir() {
        return thumbnail;
    }

    //TODO 这个占位符肯定有问题啊  现在就是针对手机写的了
    public static int getPlaceHolder() {
        return R.drawable.load;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(TAG, "老大哥睁开双眼");
        content = getApplicationContext();
        initFiles();
        initMI();


    }

    private void initMI() {
        //配置统计服务
        String APPID = content.getResources().getString(R.string.MI_APPID);
        String APP_KEY = content.getResources().getString(R.string.MI_APPID);
        String CHANNEL = "MI";
        MiStatInterface.initialize(this, APPID, APP_KEY, CHANNEL);
        MiStatInterface.setUploadPolicy(UPLOAD_POLICY_REALTIME, 1000 * 60 * 60);//每小时上报一次
        MiStatInterface.enableExceptionCatcher(true);//实时崩溃统计


    }

    /**
     * 创建存储文件夹
     */
    private void initFiles() {
        File save = new File(content.getExternalFilesDir(null), "SYSNP");
        thumbnail = new File(save, "thumbnail");
        vector = new File(save, "vector");
        //创建存储文件夹
        if (!save.exists()) {
            L.d(TAG, "保存目录不存在 应该是第一次启动");
            save.mkdir();
            thumbnail.mkdir();
            vector.mkdir();
        }
    }


}
