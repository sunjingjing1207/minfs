package com.ksyun.campus.dataserver.util;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLOutput;


public class ZkUtil {
    private String zooIP = System.getProperty("zookeeper.addr");
    private ZkClient zkClient;
    private volatile static ZkUtil zkUtil;
    private ZkUtil (){
        //初始化zkClient
        if(zooIP==null) zooIP  ="10.0.0.201:2181";
        System.out.println("zookeeper地址："+zooIP);
        zkClient = new ZkClient(zooIP, 60000 * 30, 60000);
    }
    public static ZkUtil getZkUtil() {
        if (zkUtil == null) {
            synchronized (ZkUtil.class) {
                if (zkUtil == null) {
                    zkUtil = new ZkUtil();
                }
            }
        }
        return zkUtil;
    }
    public ZkClient getZkClient() {
        return zkClient;
    }

}
