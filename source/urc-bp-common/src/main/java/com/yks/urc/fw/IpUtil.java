package com.yks.urc.fw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtil.class);
    /**
     * 获取当前服务器应用的IP地址
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String  getAddressIp(){
        String hostAddress = "";
        try {
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            hostAddress = address.getHostAddress();//192.168.0.121
//            System.out.println(hostAddress);
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);

            return "";
        }
        return hostAddress;
    }

    public static String getHostName() {
        String hostName = "";
        try {
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            hostName = address.getHostName();//192.168.0.121
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);

            return "";
        }
        return hostName;
    }

    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
            String hostAddress = address.getHostAddress();//192.168.0.121
            System.out.println(hostAddress);
            InetAddress address1 = InetAddress.getByName("www.wodexiangce.cn");//获取的是该网站的ip地址，比如我们所有的请求都通过nginx的，所以这里获取到的其实是nginx服务器的IP地
            String hostAddress1 = address1.getHostAddress();//124.237.121.122
            System.out.println(hostAddress1);
            InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");//根据主机名返回其可能的所有InetAddress对象
            for(InetAddress addr:addresses){
                System.out.println(addr);//www.baidu.com/14.215.177.38
                //www.baidu.com/14.215.177.37
            }
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
