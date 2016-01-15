package uuxia.het.com.appdaemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import uuxia.het.com.library.Daemon;


/**
 * Created by uuxia-mac on 16/1/13.
 */
public class DaemonService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
//        Daemon.run(this, DaemonService.class,5,26677,18866);
        String cmd = "-p uuxia.het.com.sample -s uuxia.het.com.sample.DaemonService11 -t 5 -z uuxia.het.com.sample:daemon11 -y 26677 -x 18866";
        Daemon.run(this, cmd);
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
