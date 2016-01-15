package uuxia.het.com.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import uuxia.het.com.daemon001.Daemon;

public class DaemonService11 extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
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
