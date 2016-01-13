package uuxia.utils;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class UdpClient extends BaseThread {
    public UdpClient(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        setName("UdpClient");
    }

    @Override
    public void run() {
        super.run();
        while (runnable) {
            try {
                //若队列为空，则线程阻塞在此处
                PacketBuffer data = outQueue.take();
                send(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(PacketBuffer packetBuffer) throws IOException {
        if (datagramSocket == null) {
            throw new IOException("datagramSocket is null");
        }
        if (packetBuffer == null) {
            throw new IOException("packetBuffer is null");
        }
        byte[] bytes = packetBuffer.getData();
        if (bytes == null || bytes.length == 0) {
            throw new IOException("bytes is null or size is zero");
        }
        String ip = packetBuffer.getIp();
        if (ip == null) {
            throw new IOException("ip is null");
        }
        int port = packetBuffer.getPort();
        if (port == 0) {
            throw new IOException("port is zero");
        }
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ip), port);
        datagramSocket.send(dp);
    }

    public static void main(String[] args) {
        String ip = "192.168.10.113";
        String ip1 = "192.168.10.255";
        String[] aa = ip.split("\\.");
        int len = aa[3].length();
        if (len == 1) {
            ip += "  ";
        } else if (len == 2) {
            ip += " ";
        }
        System.out.println(ip + "aaaa\r\n" + ip1);
    }

    public void putData(PacketBuffer packet) {
        //若队列已经满，则等待有空间再继续。
        boolean b = outQueue.offer(packet);
        if (!b) {
            Logc.e("this packet offer faile:" + packet.toString());
        }

    }
}
