package com.misakimei.accelerate.manager;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.Camera;
import com.misakimei.accelerate.Master;
import com.misakimei.accelerate.ReInputStream;
import com.misakimei.accelerate.ReOutputStream;
import com.misakimei.accelerate.exception.RecoverFailException;
import com.misakimei.accelerate.path.PathCollection;
import com.misakimei.accelerate.tool.ConvertData;
import com.misakimei.accelerate.tool.L;
import com.misakimei.accelerate.view.DrawView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by 18754 on 2016/8/10.
 * 保存 恢复
 */
public class SaveTool {

    public static final String TAG = "SaveTool";
    private static final String POSTFIX = ".vdg";
    private static File VecDir = Master.getVectorDir();
    private static File ThuDir = Master.getThumbDir();

    public static void save(String name) {

        saveBitmap(name);
        saveVector(name);
    }

    private static void saveVector(String name) {

        try {
            File f = new File(VecDir, name + POSTFIX);
            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();
            saveVector(f);

        } catch (IOException e) {
            throw new RuntimeException("保存矢量文件出错");
        }

    }

    private static void saveVector(@NonNull File f) {
        try {
            String magic = "vdg";//魔数
            int majorVersion = 0;//主要版本号
            int minorVersion = 1;//次要版本号

            //文件头 魔数(3) 主版本号(1) 次版本号(1) md5(16)
            byte[] magbs = ConvertData.getANSCII(magic);
            byte mav = (byte) majorVersion;
            byte minv = (byte) minorVersion;


            ReOutputStream bos = new ReOutputStream();
            Camera.getInstance().save(bos);
            PaintManager.getInstance().save(bos);
            PathCollection.getInstance().save(bos);

            byte[] data = bos.toByteArray();
            byte[] md5 = calMD5(data);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //head
            out.write(magbs);
            out.write(mav);
            out.write(minv);
            out.write(md5);
            out.write(data);

            writeBytesToFile(out.toByteArray(), f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] calMD5(byte[] data) {
        try {
            MessageDigest dig = MessageDigest.getInstance("md5");
            return dig.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成md5摘要出错");
        }
    }

    private static void writeBytesToFile(@NonNull byte[] data, @NonNull File f) throws IOException {
        FileOutputStream out = new FileOutputStream(f);
        out.write(data);
        L.d(TAG, "保存数据到==>" + f.getAbsolutePath() + " 共 " + data.length + " => " + (data.length * 1.0 / 1024) + "kb");
    }

    private static void saveBitmap(String name) {

        try {
            File f = new File(ThuDir, name + ".png");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();

            DrawView view = ViewManager.getInstance().getDrawView();
            view.setDrawingCacheEnabled(true);
            Bitmap cachebitmap = view.getDrawingCache();
            saveBitmap(cachebitmap, f);
            view.setDrawingCacheEnabled(false);

        } catch (IOException e) {
            throw new RuntimeException("保存图片出错");
        }
    }

    private static void saveBitmap(@NonNull Bitmap cachebitmap, @NonNull File f) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            cachebitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //TODO 封装一个类 来读取
    public static void recover(@NonNull File vdata) throws RecoverFailException, IOException {

        byte[] data = readBytesToFile(vdata);//获取到全部的byte
        checkHead(data);
        L.d(TAG, "完整性check ok");

        ReInputStream in = new ReInputStream(data);

        in.read(new byte[21]);//流出头 //丑

        Camera.getInstance().recover(in);
        L.d(TAG, "Camera 初始化完毕");

        PaintManager.getInstance().recover(in);
        L.d(TAG, "PaintManager 初始化完毕");

        PathCollection.getInstance().recover(in);
        L.d(TAG, "PathCollection 初始化完毕");

        L.d(TAG, "初始化完毕");
    }

    private static void checkHead(@NonNull byte[] data) throws RecoverFailException {
        if (data.length < 21) {
            throw new RecoverFailException("文件长度过少");
        }

        if (!Arrays.equals(ConvertData.getANSCII("vdg"), Arrays.copyOfRange(data, 0, 3))) {
            throw new RecoverFailException("magic check fail");
        }
        byte[] md5 = Arrays.copyOfRange(data, 5, 5 + 16);
        byte[] d = Arrays.copyOfRange(data, 21, data.length);
        if (!Arrays.equals(md5, calMD5(d))) {
            throw new RecoverFailException("md5 check fail");
        }
    }

    private static byte[] readBytesToFile(@NonNull File vdata) {
        L.d(TAG, "开始读取文件  " + vdata.getAbsolutePath());

        try {
            byte[] data = new byte[(int) vdata.length()];
            L.d(TAG, "应读取" + data.length + " 字节");
            FileInputStream in = new FileInputStream(vdata);
            int len = in.read(data);
            L.d(TAG, "以读取" + len + " " + (len == data.length));
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

