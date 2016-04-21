package uuxia.het.com.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uuxia.het.com.library.utils.DaemonModel;
import uuxia.het.com.library.utils.Prefers;
import uuxia.het.com.library.utils.ServiceUtils;


/**
 * Created by uuxia-mac on 16/1/13.
 */
public class DaemonService extends Service {
    private Thread daemonThread;
    private boolean running = true;
    private final static String TAG = "uulog.DaemonService";
    private final static String fileName = "daemon_info_ex";
    private static List<DaemonModel> daemons = new ArrayList<>();
    private static int mInterval = 60;

    @Override
    public void onCreate() {
        super.onCreate();
        loadPrefers();
        Daemon.run(this, DaemonService.class, mInterval, 26677, 18866);
        LauchDaemon();
        Log.e(TAG, "DaemonService.onCreate..............=" + getPackageName());
    }

    public static void main(String[] args){
        Class<?> ca = DaemonService.class;
        System.out.println(ca.getName());
    }

    public static void startDaemonService(Context context,Class<?> clasz,String action,String packageName,int inteval){
        DaemonModel daemonModel = new DaemonModel();
        daemonModel.setDestClasz(clasz.getName());
        daemonModel.setDestAction(action);
        daemonModel.setInterval(inteval);
        daemonModel.setDaseAppPakage(packageName);
        mInterval = inteval;
        Log.w(TAG, "DaemonService=" + daemonModel.toString());
        if (!ServiceUtils.isServiceAlive(context, DaemonService.class.getName())) {
            Intent intent = new Intent(context, DaemonService.class);
            intent.putExtra("DaemonModel",daemonModel);
            context.startService(intent);
        }
    }

    private void LauchDaemon() {
        daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (daemons == null || daemons.size() <= 0)
                        continue;
                    for (DaemonModel dm : daemons) {
                        if (dm == null || TextUtils.isEmpty(dm.getDestClasz()) || TextUtils.isEmpty(dm.getDestAction()))
                            continue;
                        if (!ServiceUtils.isServiceAlive(DaemonService.this, dm.getDestClasz())) {
                            Log.i(TAG, "Found User's Service there is no " + dm.getDestClasz());
                            Intent intent = new Intent(dm.getDestAction());
                            int androidVersion = ServiceUtils.getSDKVersionNumber();
                            if (androidVersion >= 21) {
                                //只一句至关重要，对于android5.0以上，所以minSdkVersion最好小于21；
                                intent.setPackage(dm.getDaseAppPakage());
                            }
                            startService(intent);
                        }
                        try {
                            Thread.sleep(mInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                    Log.i(TAG,"DaemonService.tun.. "+daemonThread.isAlive() + " "+daemonThread);
                    }
                }
            }
        });
        daemonThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "DaemonService.onDestroy ");
        Daemon.launchAlerm(this, daemons,10);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand DaemonService.onStartCommand " + intent);
        if (intent != null){
            Object o = intent.getSerializableExtra("DaemonModel");
            if (o != null && o instanceof DaemonModel) {
                DaemonModel dm = (DaemonModel) o;
                if (dm != null && dm.getDestClasz() != null) {
                    Log.i(TAG, "onStartCommand " + dm.toString());
                    if (daemons != null && daemons.size() > 0){
                        for (DaemonModel item : daemons){
                            if (item != null && item.getDestClasz() != null && !item.getDestClasz().equalsIgnoreCase(dm.getDestClasz())){
                                daemons.add(dm);
                            }
                        }
                    }else{
                        daemons.add(dm);
                    }
                    savePrefers();
                }
            }
        }
//        LauchDaemon(daemonModel);

        return START_NOT_STICKY;
    }





    private void savePrefers(){
        if (daemons != null && daemons.size() > 0) {
            Prefers.with(this).load(fileName).setObject("daemons", daemons);
            Log.i(TAG, "savePrefers " + daemons.size()+" "+ daemons.toString());
        }
    }

    private void getPrefers(){
        if (daemons.size() <= 0) {
            List<DaemonModel> daemon = Prefers.with(this).load(fileName).getObject("daemons", List.class);
            if (daemon != null) {
                Log.i(TAG, "read from Prefers " + daemon.toString());
                daemons.addAll(daemon);
            }
            Log.i(TAG, "getPrefers "+ daemons.size()+" " + daemons.toString());
        }
    }

    private void loadPrefers(){
        if (daemons.size() <= 0){
            getPrefers();
        }else{
            savePrefers();
        }
    }
}
