package uuxia.het.com.appdaemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import uuxia.het.com.library.Daemon;
import uuxia.utils.Logc;


/**
 * Created by uuxia-mac on 16/1/13.
 */
public class DaemonService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(this, DaemonService.class, 5, 26677, 18866);
        new Thread(new Runnable() {
            @Override
            public void run() {
           while (true){
               Logc.i("uulog==============================="+System.currentTimeMillis());
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Daemon.launchAlerm(this,10);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		/* do something here */
        return super.onStartCommand(intent, flags, startId);
    }
}
