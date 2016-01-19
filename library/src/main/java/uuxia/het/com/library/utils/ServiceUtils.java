package uuxia.het.com.library.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-19 17:31
 * Description:
 */
/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: ServiceUtils.java
 * Create: 2016/1/19 17:31
 */
public class ServiceUtils {

    // 判断服务是否开启
    public static boolean isServiceAlive(Context context, String serviceClassName) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> running = manager
                .getRunningServices(3000);
        for (int i = 0; i < running.size(); i++) {
            if (serviceClassName.equals(running.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

}
