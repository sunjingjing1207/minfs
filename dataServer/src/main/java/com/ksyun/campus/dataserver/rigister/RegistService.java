package com.ksyun.campus.dataserver.rigister;

import com.ksyun.*;
import com.ksyun.campus.dataserver.services.DataService;
import com.ksyun.campus.dataserver.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class RegistService implements ApplicationRunner {
    @Value("${server.port}")
    private Integer port;
    private ZkClient zk = ZkUtil.getZkUtil().getZkClient();

    public void registToCenter() {
        String path = "/dataServerList/" + port;
        if (!zk.exists("/dataServerList")) zk.create("/dataServerList", null, CreateMode.PERSISTENT);
        if (!zk.exists(path)) zk.create(path, null, CreateMode.PERSISTENT);
        zk.writeData(path, new DataServerInfo("127.0.0.1", port, 0, 1024 * 1024 * 1024, 0, System.currentTimeMillis()));
    }


    @Scheduled(initialDelay = 500, fixedRate = 1000)
    public void sendHeart() {
        String path = "/dataServerList/" + port;
        if (!zk.exists("/dataServerList")) zk.create("/dataServerList", null, CreateMode.PERSISTENT);
        if (!zk.exists(path))
            zk.create(path, new DataServerInfo("127.0.0.1", port, 0, 1024 * 1024 * 1024, 0, System.currentTimeMillis()), CreateMode.PERSISTENT);
        //更新DataServer的信息
        DataServerInfo res = (DataServerInfo) zk.readData(path);
        //更新时间
        res.setMTime(System.currentTimeMillis());
        //更新文件数与已使用容量
        File dir = new File("./" + port + "/");
        File[] files = dir.listFiles();
        int fileSize = 0, fileNum = 0;
        for (File f : files) {
            fileSize += f.length();
            fileNum++;
        }
        res.setUseCapacity(fileSize);
        res.setFileTotal(fileNum);
        zk.writeData(path, res);
    }

    /**
     * 删除冗余的文件,5秒一次
     */
    @Scheduled(initialDelay = 5000, fixedRate = 5000)
    public void updateFile() {
        //递归遍历zk树，删除冗余文件
        if(!zk.exists("/default")){
            zk.create("/default",new StatInfo("/default",0,System.currentTimeMillis(),FileType.Directory,null),CreateMode.PERSISTENT);
        }
        List<String> aviFileList = dfs("/default");
        String dirPath = "./" + port + "/";
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        for (File f : files) {
            if (!aviFileList.contains(f.getName())) f.delete();
        }
    }

    public List<String> dfs(String node) {
        List<String> res = new ArrayList<>();
        List<String> child = zk.getChildren(node);
        for (String c : child) {
            StatInfo info = zk.readData(node + "/" + c);
            if (info.getType() != FileType.File) {
                res.addAll(dfs(info.getPath()));
                continue;
            }
            List<ReplicaData> replicaData = info.getReplicaData();
            for (ReplicaData r : replicaData) {
                if (r.getDsNode().equals("127.0.0.1:" + port)) res.add(r.getId());
            }
        }
        return res;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        registToCenter();
    }
}
