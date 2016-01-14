package uuxia.het.com.appdaemon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;

import uuxia.het.com.library.Daemon;
import uuxia.utils.IRecevie;
import uuxia.utils.IpUtils;
import uuxia.utils.PacketBuffer;
import uuxia.utils.UdpManager;

public class MainActivity extends Activity implements IRecevie{
    private UdpManager udpManager;
    private TextView tv;
    private String ip;
    private int port = 27766;
    private EditText num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(Daemon.getIp(this));
//        try {
//            Process process = Runtime.getRuntime().exec("su");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        num = (EditText) findViewById(R.id.num);
        ip = IpUtils.getLocalIP(this);
        tv = (TextView) findViewById(R.id.log);
        try {
            udpManager = new UdpManager(ip,port);
            udpManager.setCallback(this);
            udpManager.setLocalIp(IpUtils.getLocalIP(this));
            udpManager.setBroadCasetIp(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void onSend(View view){
        String str = "uuxia";
        if (num.getText().toString() != null){
            str = num.getText().toString();
        }
        udpManager.send(str.getBytes(),ip,26677);
    }

    public void onInit(View view){
        startService(new Intent(this, DaemonService.class));
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PacketBuffer packetBuffer = (PacketBuffer) msg.obj;
            if (packetBuffer != null){
                byte[] data = packetBuffer.getData();
                tv.setText(Arrays.toString(data));
            }
        }
    };

    @Override
    public void onRecevie(Object obj) {
        Message msg = Message.obtain();
        msg.obj = obj;
        handler.sendMessage(msg);
    }
}
