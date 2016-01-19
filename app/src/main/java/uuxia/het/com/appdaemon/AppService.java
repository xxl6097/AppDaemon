package uuxia.het.com.appdaemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import uuxia.het.com.library.Daemon;
import uuxia.utils.Logc;

public class AppService extends Service {
    public AppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(this, AppService.class, 5, 26677, 18899);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Logc.e("aaaaaaaaaaaaaaaaaaaaaaa="+System.currentTimeMillis());
                    try {
                        Thread.sleep(6000);
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
        Daemon.launchAlerm(this,null,5);
    }
}
