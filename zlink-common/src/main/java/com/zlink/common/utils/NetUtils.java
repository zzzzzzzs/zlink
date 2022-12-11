package com.zlink.common.utils;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

/**
 * @author zs
 * @date 2022/12/11
 */
public class NetUtils {
    public static boolean isLoclePortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    public static boolean isPortUsing(String host, int port) {
        boolean flag = false;
        try {
            InetAddress theAddress = InetAddress.getByName(host);
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (Exception e) {

        }
        return flag;
    }

    // 随机生成端口
    public static int getAvailablePort() {
        int max = 65535;
        int min = 50000;
        Random random = new Random();
        int port = random.nextInt(max) % (max - min + 1) + min;
        boolean using = NetUtils.isLoclePortUsing(port);
        if (using) {
            return getAvailablePort();
        } else {
            return port;
        }
    }

    public static void main(String[] args) {
        int port = getAvailablePort();
        System.out.println(port);
    }

}
