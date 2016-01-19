package uuxia.het.com.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import uuxia.het.com.library.utils.DaemonModel;
import uuxia.het.com.library.utils.Prefers;
import uuxia.het.com.library.utils.ServiceUtils;


/**
 * Created by uuxia-mac on 16/1/13.
 */
public class DaemonService extends Service {
    private Thread daemonThread;
    private boolean running = true;
    private static DaemonModel daemonModel;
    private final static String TAG = "uulog.DaemonService";
    private final static String fileName = "daemon_info";

    @Override
    public void onCreate() {
        super.onCreate();
        getPrefers();
        Daemon.run(this, DaemonService.class, daemonModel.getInterval(), 26677, 18866);
        LauchDaemon(daemonModel);
        Log.e(TAG, "DaemonService.onCreate..............=" + daemonModel.toString());
    }

    public static void main(String[] args){
        Class<?> ca = DaemonService.class;
        System.out.println(ca.getName());
    }

    public static void startDaemonService(Context context,Class<?> clasz,String action,String packageName,int inteval){
        daemonModel = new DaemonModel();
        daemonModel.setDestClasz(clasz.getName());
        daemonModel.setDestAction(action);
        daemonModel.setInterval(inteval);
        daemonModel.setDaseAppPakage(packageName);
        Log.w(TAG, "cccc=" + daemonModel.toString());
        savePrefers(context, daemonModel);
        if (!ServiceUtils.isServiceAlive(context, DaemonService.class.getName())) {
            Intent intent = new Intent(context, DaemonService.class);
            intent.putExtra("DaemonModel",daemonModel);
            context.startService(intent);
        }
    }

    private void LauchDaemon(final DaemonModel dm) {
        if (dm == null || TextUtils.isEmpty(dm.getDestClasz()) || TextUtils.isEmpty(dm.getDestAction()))
            return;
        daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    if (!ServiceUtils.isServiceAlive(DaemonService.this, dm.getDestClasz())) {
                        Log.i(TAG,"Found User's Service there is no "+dm.getDestClasz());
                        Intent intent = new Intent(dm.getDestAction());
                        int androidVersion = ServiceUtils.getSDKVersionNumber();
                        if (androidVersion >= 21) {
                            //只一句至关重要，对于android5.0以上，所以minSdkVersion最好小于21；
                            intent.setPackage(dm.getDaseAppPakage());
                        }
                        startService(intent);
                    }
                    try {
                        Thread.sleep(daemonModel.getInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    Log.i(TAG,"DaemonService.tun.. "+daemonThread.isAlive() + " "+daemonThread);
                }
            }
        });
        daemonThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "DaemonService.onDestroy ");
        Daemon.launchAlerm(this, daemonModel,10);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, daemonModel+"DaemonService.onStartCommand " + intent);
//        if (intent != null){
//            Object o = intent.getSerializableExtra("DaemonModel");
//            if (o != null && o instanceof DaemonModel) {
//                DaemonModel dm = (DaemonModel) o;
//                if (dm != null) {
//                    daemonModel = dm;
//                }
//            }
//        }
//        LauchDaemon(daemonModel);
//        return START_NOT_STICKY;
//    }





    private static void savePrefers(Context context,DaemonModel value){

        Prefers.with(context).load(fileName).save("destClasz", value.getDestClasz());
        Prefers.with(context).load(fileName).save("destAction", value.getDestAction());
        Prefers.with(context).load(fileName).save("destAppPackage", value.getDaseAppPakage());
        Prefers.with(context).load(fileName).save("interval", value.getInterval());
    }

    private void getPrefers(){
        if (daemonModel == null){
            daemonModel = new DaemonModel();
            daemonModel.interval = Prefers.with(this).load(fileName).getInt("interval", 60);
            daemonModel.daseAppPakage = Prefers.with(this).load(fileName).getString("destAppPackage",null);
            daemonModel.destAction = Prefers.with(this).load(fileName).getString("destAction",null);
            daemonModel.destClasz = Prefers.with(this).load(fileName).getString("destClasz",null);
        }
    }
}
