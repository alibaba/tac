package com.alibaba.tac.sdk.utils;

import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by changkun.hck on 2016/12/29.
 */
public class TacIPUtils {

    private  static String staticIp;

    private  static String staticHost;

    static {
        // ip init
        try {
            if(staticIp == null) {
                staticIp = Inet4Address.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
        // host init
        try {
            if(staticHost == null) {
                staticHost = Inet4Address.getLocalHost().getHostName();
            }
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }
    }

    public static String getLocalHostName() {
        return staticHost;
    }

    public static String getLocalIp() {
        return staticIp;
    }


    @Deprecated
    private static Collection<InetAddress> getAllHostAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Collection<InetAddress> addresses = new ArrayList<InetAddress>();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addresses.add(inetAddress);
                }
            }

            return addresses;
        } catch (SocketException e) {
        }
        return Collections.emptyList();
    }

    @Deprecated
    public static Collection<String> getAllIpv4NoLoopbackAddresses() {
        Collection<String> noLoopbackAddresses = new ArrayList<String>();
        Collection<InetAddress> allInetAddresses = getAllHostAddress();
        for (InetAddress address : allInetAddresses) {
            if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                noLoopbackAddresses.add(address.getHostAddress());
            }
        }
        return noLoopbackAddresses;
    }
    @Deprecated
    public static String getFirstNoLoopbackAddress() {
        Collection<String> allNoLoopbackAddresses = getAllIpv4NoLoopbackAddresses();
        return allNoLoopbackAddresses.iterator().next();
    }

}
