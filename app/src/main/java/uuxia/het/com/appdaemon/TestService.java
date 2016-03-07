package uuxia.het.com.appdaemon;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import uuxia.het.com.library.DaemonService;
import uuxia.utils.Logc;

public class TestService extends Service {
    public static final String ACTION = "common.het.com.library.intent.action.TestService";
    @Override
    public void onCreate() {
        super.onCreate();
        Logc.e("  TestService      DaemonService..............=");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Logc.i("uulog=====do something"+System.currentTimeMillis());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        DaemonService.startDaemonService(this, TestService.class, ACTION, getPackageName(), 5);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logc.e("...TestService...................onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logc.e("   TestService     onBind..............=");
        return null;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Logc.e("   TestService     unbindService..............=");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logc.e("   TestService     onUnbind..............=");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		/* do something here */
        Logc.e("   TestService     onStartCommand..............=");
        return START_NOT_STICKY;
    }
}
