package uuxia.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 夏小力 on 2014-48-19.
 */
public class IpUtils {

    public static String getBroadcastAddress(Context ctx) {
        try {
            String broad = IpUtils.getBroadcast();
            Logc.i("获取广播地址：" + broad);
            return broad;
        } catch (SocketException e) {
            e.printStackTrace();
            Logc.e("广播地址获取失败 " + e.getMessage(), true);
        }
        return null;
    }

    public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
//                    System.out.println("MYACTIVITY "+ ni.getName() +" "+ interfaceAddress.getBroadcast());
                    if (interfaceAddress.getBroadcast() != null && (ni.getName().contains("wlan") || ni.getName().contains("eth1"))) {
                        String broad = interfaceAddress.getBroadcast().toString().substring(1);
                        Log.i("MYACTIVITY", ni.getName() + " 广播地址==" + broad);
                        return broad;
                    }
                }
            }
        }
        return null;
    }

    public static String getBroadcast1() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        String broad = interfaceAddress.getBroadcast().toString().substring(1);
                        Logc.i("广播地址==" + broad);
                        return broad;
                    }
                }
            }
        }
        return null;
    }

    public static String getBroadcastAddress1(Context ctx) {
        WifiManager cm = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo myDhcpInfo = cm.getDhcpInfo();
        if (myDhcpInfo == null) {
            return "255.255.255.255";
        }
        // int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
        // | ~myDhcpInfo.netmask;
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
            return InetAddress.getByAddress(quads).getHostAddress();
        } catch (Exception e) {
            return "255.255.255.255";
        }
    }

    /**
     * 根据IP最后一个字节拼凑IP(192.168.1.x + 112 ==> 192.168.1.112)
     *
     * @param ctx
     * @param lastBit
     * @return
     */
    public static String pieceIP(Context ctx, byte lastBit) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (di.ipAddress & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((di.ipAddress >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((di.ipAddress >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) (lastBit & 0xff)));
        String ipStr = sb.toString();
        return ipStr;
    }

    /**
     * 获取本机
     *
     * @param ctx
     * @return
     */
    public static String getLocalIP(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long ip = di.ipAddress;
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    /**
     * 获取IP最后一个字节(192.168.1.112 ==> 112)
     *
     * @param ctx
     * @return
     */
    public static byte getIpLastByte(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        byte lastIPByte = (byte) ((di.ipAddress >> 24) & 0xff);
        return lastIPByte;
    }

    public static byte getIpLastByte(String ip) {
        byte[] ips = ipv4Address2BinaryArray(ip);
        if (ips.length >= 4) {
            return ips[3];
        }
        return 0;
    }

    public static boolean isIpv4(String ipAddress) {
        if (ipAddress == null || ipAddress.equalsIgnoreCase("")) {
            return false;
        }

        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }

    /**
     * 将给定的字节数组转换成IPV4的十进制分段表示格式的ip地址字符串
     */
    public static String binaryArray2Ipv4Address(byte[] addr) {
        String ip = "";
        for (int i = 0; i < addr.length; i++) {
            ip += (addr[i] & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    /**
     * 将给定的用十进制分段格式表示的ipv4地址字符串转换成字节数组
     */
    public static byte[] ipv4Address2BinaryArray(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            binIP[i] = (byte) Integer.parseInt(strs[i]);
        }
        return binIP;
    }
}
