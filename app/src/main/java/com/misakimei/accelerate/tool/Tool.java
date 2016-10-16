package com.misakimei.accelerate.tool;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import com.misakimei.accelerate.PaintStyle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 吴聪 on 2016/5/6.
 */
public class Tool {
    private static final String TAG = "TOOL";
    private static String time;
    private static Object imageList;

    /**
     * 返回以center为中心 边长为2*radius的矩形
     *
     * @param center
     * @param radius
     * @return
     */
    @NonNull
    public static RectF getRectF(@NonNull PointF center, float radius) {
        return new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
    }

    /**
     * 以center为中心 长为 2*radiusX 宽为2*radiusY
     *
     * @param center
     * @param radiusX
     * @param radiusY
     * @return
     */
    @NonNull
    public static RectF getRectF(@NonNull PointF center, float radiusX, float radiusY) {
        return new RectF(center.x - radiusX, center.y - radiusY, center.x + radiusX, center.y + radiusY);
    }

    /**
     * orgin 偏移dx dy 缩小 ratio之后的矩形
     *
     * @param dx
     * @param dy
     * @param ratio
     * @param orgin
     * @return
     */
    @NonNull
    public static RectF getRectF(@NonNull PointF center, float dx, float dy, float ratio, @NonNull RectF orgin) {
        RectF rect = new RectF(orgin);
        PointF newCenter = new PointF(center.x, center.y);
        float newwidth = orgin.width() * ratio;
        float newheight = orgin.height() * ratio;
        rect.left = newCenter.x - (newCenter.x - orgin.left) * ratio;
        rect.top = newCenter.y - (newCenter.y - orgin.top) * ratio;
        rect.right = rect.left + newwidth;
        rect.bottom = rect.top + newheight;
        return rect;
    }

    public static float getDistance(@NonNull PointF p1, @NonNull PointF p2) {
        return (float) Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }

    @NonNull
    public static PointF getCenter(@NonNull PointF p1, @NonNull PointF p2) {
        return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    @NonNull
    public static Rect RectFToRect(@NonNull RectF rectF) {
        Rect rect = new Rect();
        rect.left = Math.round(rectF.left);
        rect.top = Math.round(rectF.top);
        rect.right = Math.round(rectF.right);
        rect.bottom = Math.round(rectF.bottom);
        return rect;
    }


    @NonNull
    public static String getDiff(@NonNull RectF bound, @NonNull RectF prebound) {
        return "偏移 " + (prebound.centerX() - bound.centerX()) + " " + (prebound.centerY() - bound.centerY()) +
                " 缩放 " + (prebound.height() / bound.height());
    }

    public static String getTime() {
        Date current = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMss", Locale.CHINA);
        return dateFormat.format(current);

    }

    @NonNull
    public static ArrayList<File> getImageList(@NonNull File file) {
        ArrayList<File> images = new ArrayList<>();

        File[] list = file.listFiles();
        for (File f : list) {
            String path = f.getAbsolutePath();
            if (path.endsWith(".png")) {
                images.add(f);
            }
        }
        return images;
    }

    public static boolean MatrixEquals(Matrix matrix, Matrix matrix1) {
        return false;
    }


    public static boolean PaintEquals(PaintStyle paintStyle, PaintStyle style) {
        return false;
    }

    @NonNull
    public static String getLocalHostIp(@NonNull Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            return "";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String ip = formatIpAddress(dhcpInfo.ipAddress);
        String mask = formatNetmask(dhcpInfo.netmask);
        L.d(TAG, "ip " + ip);
        L.d(TAG, "mask " + mask);

        return ip;

    }

    @NonNull
    private static String formatNetmask(int netmask) {
        return (netmask & 0xFF) + "." +
                ((netmask >> 8) & 0xFF) + "." +
                ((netmask >> 16) & 0xFF) + "." +
                (netmask >> 24 & 0xFF);
    }

    @NonNull
    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }


    public static boolean isReachable(@NonNull String ipaddress, @NonNull Context context) {
        L.d(TAG, "isReachable");
        if (ipaddress.equals("127.0.0.1")) {
            return false;
        }
        String phoneIP = getLocalHostIp(context);
        L.d(TAG, "phoneIP " + phoneIP);
        if (phoneIP.length() == ipaddress.length()) {
            L.d(TAG, "phone ip " + phoneIP);
            L.d(TAG, "pc    ip " + ipaddress);
            return phoneIP.substring(0, phoneIP.length() - 4).equals(ipaddress.substring(0, ipaddress.length() - 4));
        }
        return false;
    }

    public static boolean checkType(int read, byte type) {
        return type == (byte) read;
    }
}
