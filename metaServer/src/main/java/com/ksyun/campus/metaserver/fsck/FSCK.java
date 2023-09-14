package com.ksyun.campus.metaserver.fsck;

import com.ksyun.DataServerInfo;
import com.ksyun.FileType;
import com.ksyun.ReplicaData;
import com.ksyun.StatInfo;
import com.ksyun.campus.metaserver.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class FSCK{
    private ZkClient zk = ZkUtil.getZkUtil().getZkClient();
    private RestTemplate restTemplate = new RestTemplate();
    //遍历文件树，判断文件的副本服务器是否可用，如果不可用，则对文件进行复制迁移并修改StatInfo
    @Scheduled(cron="0/10 * *  * * ? ")
    public void fsck(){
        List<DataServerInfo> dataServerInfoList = ZkUtil.getZkUtil().getDataServerList();
        if(dataServerInfoList.size()<3) return;
        List<StatInfo> fileStatInfo =  dfs("/default");
        //获取可用dataServer列表
        List<String> dataServer = new ArrayList<>();
        for(DataServerInfo d : dataServerInfoList){
            dataServer.add(d.getHost()+":"+d.getPort());
        }

        for(StatInfo stat : fileStatInfo){
            List<ReplicaData> replicaData = stat.getReplicaData();
            List<ReplicaData> newReplicaData = new ArrayList<>();
            int[] isUseDataServer = new int[dataServer.size()];
            //检查是否有失效的DataServer
            for(ReplicaData r:replicaData){
                if(dataServer.contains(r.getDsNode())){
                    boolean isExist = restTemplate.getForObject("http://"+r.getDsNode()+"/exist?path="+r.getPath(),Boolean.class);
                    if(!isExist) continue;
                    isUseDataServer[dataServer.indexOf(r.getDsNode())]=1;
                    newReplicaData.add(r);
                }
            }
            //将副本移至新的DataServer
            while(newReplicaData.size()<3 && newReplicaData.size()>=1){
                ReplicaData origin = newReplicaData.get(0);
                for(int i=0;i<dataServer.size();i++){
                    if(isUseDataServer[i]==0){
                        ReplicaData r = new ReplicaData(origin.getId(),dataServer.get(i),"./"+dataServer.get(i).split(":")[1]+"/"+origin.getId());
                        newReplicaData.add(r);
                        System.out.println(r);
                        //读取文件
                        byte[] file = restTemplate.getForObject("http://"+origin.getDsNode()+"/read?path="+origin.getPath(),byte[].class);
                        //复制文件
                        String res = restTemplate.postForObject("http://"+r.getDsNode()+"/write?path="+r.getPath(),file,String.class);
                    }
                }
            }
            //更新ReplicaData
            stat.setReplicaData(newReplicaData);
            zk.writeData(stat.getPath(),stat);
        }
    }

    public List<StatInfo> dfs(String node){
        List<StatInfo> res = new ArrayList<>();
        List<String> child = zk.getChildren(node);
        for (String c : child) {
            StatInfo info = zk.readData(node + "/" + c);
            if (info.getType() != FileType.File) {
                res.addAll(dfs(info.getPath()));
                continue;
            }
            res.add(info);
        }
        return res;
    }

}
