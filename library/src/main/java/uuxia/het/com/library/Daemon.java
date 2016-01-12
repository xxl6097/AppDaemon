package uuxia.het.com.library;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

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
    private static void start(Context context, Class<?> daemonClazzName, int interval) {
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

        try {
            Runtime.getRuntime().exec(cmdBuilder.toString()).waitFor();
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
                           final int interval) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Command.install(context, BIN_DIR_NAME, DAEMON_BIN_NAME);
                start(context, daemonServiceClazz, interval);
            }
        }).start();
    }
}
