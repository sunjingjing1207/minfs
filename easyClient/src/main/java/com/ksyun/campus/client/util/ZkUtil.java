package com.ksyun.campus.client.util;

import com.ksyun.DataServerInfo;
import com.ksyun.MetaServerInfo;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 单例模式实现ZkUtil
 */
public class ZkUtil {
    private String zooIP = System.getProperty("zookeeper.addr");
    private ZkClient zkClient;
    private volatile static ZkUtil zkUtil;
    private ZkUtil (){
        if(zooIP==null) zooIP  ="10.0.0.201:2181";
        System.out.println("zookeeper地址："+zooIP);
        //初始化zkClient
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
    public List<MetaServerInfo> getAvailList(){
        List<String> metaServerName;
        List<MetaServerInfo> metaServerList = new ArrayList<>();
        List<MetaServerInfo> availMetaServerList = new ArrayList<>();
        try {
            metaServerName = zkClient.getChildren("/metaServerList");
            for(String m : metaServerName){
                MetaServerInfo tem = (MetaServerInfo)zkClient.readData("/metaServerList/"+m);
                metaServerList.add(tem);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        long nowTime = System.currentTimeMillis();
        for(MetaServerInfo m : metaServerList){
            //超时5秒心跳失败
            if(nowTime-m.getMTime()<1000*5) availMetaServerList.add(m);
        }
        Collections.sort(availMetaServerList);
        return availMetaServerList;
    }

    public List<DataServerInfo> getDataServerList(){
        List<String> dataServerName;
        List<DataServerInfo> availDataServerList = new ArrayList<>();
        List<DataServerInfo> dataServerList = new ArrayList<>();

        try {
            dataServerName = zkClient.getChildren("/dataServerList");
            for(String m : dataServerName){
                DataServerInfo tem = (DataServerInfo)zkClient.readData("/dataServerList/"+m);
                dataServerList.add(tem);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        long nowTime = System.currentTimeMillis();
        for(DataServerInfo m : dataServerList){
            //超时5秒心跳失败
            if(nowTime-m.getMTime()<1000*5) availDataServerList.add(m);
        }
        Collections.sort(availDataServerList);
        return availDataServerList;
    }

}
