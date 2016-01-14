package uuxia.het.com.library;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
 *
 * File: Daemon.java
 * Create: 2016/1/12 10:08
 */
public class Daemon {
    private static final String TAG = Daemon.class.getSimpleName();

    private static final String BIN_DIR_NAME = "bin";
    private static final String DAEMON_BIN_NAME = "daemon";

    public static final int INTERVAL_ONE_MINUTE = 60;
    public static final int INTERVAL_ONE_HOUR = 3600;

    /** start daemon */
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

    public static void killDaemon(Context context) {
        String cmd = context.getDir(BIN_DIR_NAME, Context.MODE_PRIVATE)
                .getAbsolutePath() + File.separator + DAEMON_BIN_NAME;

        String pidStr = "";
		/* create the command string */
        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append(cmd);
        cmdBuilder.append(" -y ");
        cmdBuilder.append(1);

        try {
            int pid = Runtime.getRuntime().exec(cmdBuilder.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "start daemon error: " + e.getMessage());
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

    public static String getBroadCastAddress(Context context){
        String ip = getIp(context);
        String broadcast = "255.255.255.255";
        if (TextUtils.isEmpty(ip)){
            return broadcast;
        }else{
            String[] lines = ip.split("\\.");

            StringBuffer sb = new StringBuffer();
            sb.append(lines[0]);
            sb.append(".");
            sb.append(lines[1]);
            sb.append(".");
            sb.append(lines[2]);
            sb.append(".255");
            broadcast = sb.toString();
            Log.i("uulog.jni",broadcast);
            return broadcast;
        }
    }

    /**
     * Get the ip of current mobile device. This util needs
     * "android.permission.ACCESS_WIFI_STATE" and "android.permission.INTERNET" permission.
     */
    public static String getIp(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ipAddress = "0.0.0.0";

        if (manager == null) {
            return ipAddress;
        }

        if (manager.isWifiEnabled()) {
            WifiInfo info = manager.getConnectionInfo();
            int ip = info.getIpAddress();
            ipAddress = (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." +
                    ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
        } else {
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                for (; en.hasMoreElements();) {
                    NetworkInterface nitf = en.nextElement();
                    Enumeration<InetAddress> inetAddrs = nitf.getInetAddresses();
                    for (;inetAddrs.hasMoreElements();) {
                        InetAddress inetAddr = inetAddrs.nextElement();
                        if (!inetAddr.isLoopbackAddress()) {
                            ipAddress = inetAddr.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
				/* ignore */
            }
        }

        return ipAddress;
    }
}
