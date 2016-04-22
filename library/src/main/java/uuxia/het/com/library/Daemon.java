package uuxia.het.com.library;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import uuxia.het.com.library.utils.DaemonModel;

/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-12 10:08
 * Description:
 */
/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife -
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *  /data/data/uuxia.het.com.sample/app_bin/daemon.lock uuxia.het.com.sample uuxia.het.com.sample:daemon11 uuxia.het.com.sample.DaemonService11 1 1
 * File: Daemon.java
 * Create: 2016/1/12 10:08
 */

public class Daemon {
    private static final String TAG = Daemon.class.getSimpleName();

    private static final String BIN_DIR_NAME = "bin";
    private static final String DAEMON_BIN_NAME = "daemon";

    /**
     * start daemon
     * @param context
     * @param daemonClazzName
     * @param interval default 1 seconds
     * @param jniport jni server port
     * @param javaport java local server port
     */
    private static void start(Context context, Class<?> daemonClazzName, int interval ,int jniport, int javaport) {
        String cmd = context.getDir(BIN_DIR_NAME, Context.MODE_PRIVATE)
                .getAbsolutePath() + File.separator + DAEMON_BIN_NAME;
		/* create the command string */
        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append(cmd);
        cmdBuilder.append(" -p ");
        cmdBuilder.append(context.getPackageName());
        cmdBuilder.append(" -s ");
        cmdBuilder.append(daemonClazzName.getName());
        cmdBuilder.append(" -t ");
        cmdBuilder.append(interval);
        cmdBuilder.append(" -z ");
        cmdBuilder.append(getCurProcessName(context));
        cmdBuilder.append(" -y ");
        cmdBuilder.append(jniport);
        cmdBuilder.append(" -x ");
        cmdBuilder.append(javaport);

        try {
            int pid = Runtime.getRuntime().exec(cmdBuilder.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "start daemon error: " + e.getMessage());
        }
    }

    /**
     * Run daemon process.
     *
     * @param context            context
     * @param daemonServiceClazz the name of daemon service class
     * @param interval           the interval to check
     */
    public static void run(final Context context, final Class<?> daemonServiceClazz,
                           final int interval, final int jniport, final int javaport) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.install(context, BIN_DIR_NAME, DAEMON_BIN_NAME);
                start(context, daemonServiceClazz, interval,jniport,javaport);
            }
        }).start();
    }

    /**
     * time:Time delay,example:time=10 mean 10 seconds
     * @param context
     * @param time
     */
    public static void launchAlerm(Context context,Collection<DaemonModel> daemons,int time){
        if (daemons == null || daemons.size() <= 0)
            return;
        for (DaemonModel daemonModel : daemons) {
            if (daemonModel == null) {
                daemonModel = new DaemonModel();
            }
            daemonModel.setCode("restart");
            daemonModel.setDaemonClasz(context.getClass().getName());
            DaemonReceiver.launchAlerm(context, daemonModel, time);
        }
    }

    private static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
