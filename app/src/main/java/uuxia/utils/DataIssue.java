package uuxia.utils;


import java.nio.ByteBuffer;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class DataIssue extends BaseThread {

    final static byte HEAD_F2 = (byte) 0xF2;
    final static byte HEAD_5A = 0x5A;
    //接收线程
    private static final byte HEAD = (byte) 0xF2;
    ByteBuffer hfBuffer = ByteBuffer.allocate(200);
    // 缓冲BUFF
    private byte[] cashBuffer = new byte[1024 * 1024];
    // 当前BUFFER 的总数byte,（位置指引）
    private int currentSizeNew = 0;
    // 缓冲BUFF
    private byte[] cashBufferNew = new byte[4096];
    private String localIp;
    private IRecevie callback;

    public DataIssue() {
        setName("DataIssue");
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    @Override
    public void run() {
        super.run();
        while (runnable) {
            try {
                //从队列中取数据，如果队列为空，则阻塞在此处
                PacketBuffer packet = inQueue.take();
//              Logc.e("出->" + inQueue.size());
                recv(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void recv(PacketBuffer packet) throws Exception {
        if (packet == null)
            throw new Exception("packet is null...");
        byte[] data = packet.getData();
        if (data == null || data.length == 0)
            throw new Exception("data is null or length is zero...");
        int len = packet.getLength();
        if (data == null || data.length == 0)
            throw new Exception("data length is Invalid...");
        String ip = packet.getIp();
        if (localIp != null && localIp.equals(ip))
            return;
        byte[] recv = data;//checkData(data, len, ip);
        if (callback == null) {
            throw new Exception("please set callback method...");
        }
        if (recv != null) {
            packet.setData(recv);
            callback.onRecevie(packet);
        }
    }

    public void setCallback(IRecevie callback) {
        this.callback = callback;
    }
}
