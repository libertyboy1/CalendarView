package com.view.calendar.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

/**
 * 获取屏幕相关信息/分辨率单位转换工具类
 * Created by 苏奥博 on 2016/12/12.
 */

public class DensityUtil {
    public static int  DECORHEIGHT=0;
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取手机屏幕宽度
     */
    public static int getWindowWidth(Context context) {
        return ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获取手机屏幕高度
     */
    public static int getWindowHeight(Context context) {
        return ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
    }
    /**
     * 获取手机状态栏高度
     */
    public static int getDecorHeight(Context context) {
        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

}
