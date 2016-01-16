package uuxia.het.com.library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;

public class DaemonReceiver extends BroadcastReceiver {
    private final static String TAG = "uulog.DaemonReceiver";
    public final static String RESTARTACTION = "common.het.com.library.intent.action.START";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String msg = intent.getStringExtra("msg");
        String clasz = intent.getStringExtra("clasz");
        Log.e(TAG, "DaemonReceiver.onReceive " + action  + ":"+msg+ " data:" + clasz);
        if (action != null && action.equalsIgnoreCase(RESTARTACTION)){
            if (!TextUtils.isEmpty(clasz) && null != msg && msg.equalsIgnoreCase("restart")) {
                restartService(context, intent, clasz);
            }
        }
    }

    public static void main(String[] args){
        Class<?> clasz = DaemonReceiver.class;
        System.out.println(clasz.getName());
    }

    private static void restartService(Context context, Intent intent,String clasz){
        try {
            try {
                Log.e(TAG, "runService " + intent + "|" + clasz);
                intent.setClassName(context, clasz);
                context.startService(intent);
            } catch (Throwable var7) {
                Log.w("BaseIntentService", "runIntentInService", var7);
            }

        } finally {
            ;
        }
    }

    public static void launchAlerm(Context context,int time){
        final int REQUEST_CODE_1 = 1;
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DaemonReceiver.class);
        intent.putExtra("msg", "restart");
        intent.putExtra("clasz", context.getClass().getName());
        intent.setAction(RESTARTACTION);
        PendingIntent setPendIntent1 = PendingIntent.getBroadcast(context, REQUEST_CODE_1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e(TAG, setPendIntent1 + " startAlarm");
        int triggerAtTime = (int) (SystemClock.elapsedRealtime() + time * 1000);
//        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                triggerAtTime, interval, setPendIntent1);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtTime, setPendIntent1);
    }
}
