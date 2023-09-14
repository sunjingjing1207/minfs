package com.ksyun.campus.metaserver.register;

import com.ksyun.MetaServerInfo;
import com.ksyun.campus.metaserver.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RegistService implements ApplicationRunner {
    @Value("${zk.path}")
    private String path;
    @Value("${server.port}")
    private Integer port;
    private ZkClient zk = ZkUtil.getZkUtil().getZkClient();
    public void registToCenter(){
        if(!zk.exists("/metaServerList")) zk.create("/metaServerList",null, CreateMode.PERSISTENT);
        if(!zk.exists(path)) zk.create(path,null,CreateMode.PERSISTENT);
        zk.writeData(path,new MetaServerInfo("127.0.0.1",port,System.currentTimeMillis()));
    }

    @Scheduled(cron="0/1 * *  * * ? ")
    public void sendHeart(){
        if(!zk.exists("/metaServerList")) zk.create("/metaServerList",null, CreateMode.PERSISTENT);
        if(!zk.exists(path)) zk.create(path,null,CreateMode.PERSISTENT);
        zk.writeData(path,new MetaServerInfo("127.0.0.1",port,System.currentTimeMillis()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        registToCenter();
    }
}
