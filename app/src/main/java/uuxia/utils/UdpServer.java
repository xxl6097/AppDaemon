package uuxia.utils;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class UdpServer extends BaseThread {
    protected DatagramPacket datagramPacket;
    protected byte[] buffer = new byte[8192];

    public UdpServer(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        setName("UdpServer");
    }

    @Override
    public void run() {
        super.run();
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        while (runnable) {
            try {
                datagramSocket.receive(datagramPacket);
                PacketBuffer packet = new PacketBuffer();
                packet.setData(datagramPacket.getData());
                packet.setLength(datagramPacket.getLength());
                packet.setPort(datagramPacket.getPort());
                packet.setIp(datagramPacket.getAddress().getHostAddress().toString());
//               Logc.i("uulog.jni接收队列大小->" + inQueue.size() + " " + Arrays.toString(packet.getData()));//+""+ ByteUtils.toHexString(datagramPacket.getData()));
                byte[] remo = new byte[datagramPacket.getLength()];
                System.arraycopy(datagramPacket.getData(), 0, remo, 0, datagramPacket.getLength());
                packet.setData(remo);
                boolean b = inQueue.offer(packet);
                if (!b) {
                    Logc.e("this packet is loss:" + packet.toString());
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Logc.i("UDPReceiver.start.run_SocketTimeoutException" + e.getMessage());
            } catch (IOException e) {
                Logc.i("UDPReceiver.start.run_IOException" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
