package uuxia.het.com.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void onStartService(View view){
        startService(new Intent(this, DaemonService11.class));
    }

    public void onStartLinux(View view){
        String cmd = "/data/data/uuxia.het.com.appdaemon/app_bin/daemon -p uuxia.het.com.sample -s uuxia.het.com.sample.DaemonService11 -t 5 -z uuxia.het.com.sample:daemon11 -y 26677 -x 18866";
        //Daemon.run(this, "uuxia.het.com.appdaemon uuxia.het.com.appdaemon:daemon uuxia.het.com.appdaemon.DaemonService 1 1");
        try {
            int pid = Runtime.getRuntime().exec(cmd).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e("sfsefsefuulog.j", "start daemon error: " + e.getMessage());
        }
    }
}
