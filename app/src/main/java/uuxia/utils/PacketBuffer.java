package uuxia.utils;


import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class PacketBuffer implements Serializable {
    private short command;
    private byte[] data;
    private int length;
    private String ip;
    private int port;
    private long timestamp;

    public PacketBuffer() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "PacketBuffer{" +
                "data=" + Arrays.toString(data) +
                ", length=" + length +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
