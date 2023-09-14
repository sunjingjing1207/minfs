package com.ksyun.campus.client;

import com.ksyun.ClusterInfo;
import com.ksyun.DataServerInfo;
import com.ksyun.MetaServerInfo;
import com.ksyun.StatInfo;
import com.ksyun.campus.client.util.ZkUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class EFileSystem extends FileSystem{

    private RestTemplate restTemplate = new RestTemplate();
    private ZkClient zk = ZkUtil.getZkUtil().getZkClient();

    private String fileName="default";
    public EFileSystem() {
    }

    public EFileSystem(String fileName) {
        this.fileName = fileName;
    }

    public FSInputStream open(String path){
        FSInputStream fsInputStream = new FSInputStream(path);
        return fsInputStream;
    }
    public FSOutputStream create(String path){
        FSOutputStream fsOutputStream = new FSOutputStream(path);
        return fsOutputStream;
    }
    public boolean mkdir(String path){
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return false;
        String res = restTemplate.getForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/mkdir?path="+path,String.class);
        if(res.equals("创建成功！")) return true;
        else return false;
    }
    public boolean delete(String path){
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return false;
        Boolean res = restTemplate.getForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/delete?path="+path, Boolean.class);
        return res;
    }
    public StatInfo getFileStats(String path){
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return null;
        return restTemplate.getForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/getstat?path="+path,StatInfo.class);
    }

    public List<StatInfo> listFileStats(String path){
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return null;
        List<StatInfo> res = restTemplate.getForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/liststats?path="+path,List.class);
        return res;

    }
    public ClusterInfo getClusterInfo(){
        //1、获取元数据服务器metaServer
        List<MetaServerInfo> metaServerInfos = ZkUtil.getZkUtil().getAvailList();
        //2、获取数据服务器dataServer
        List<DataServerInfo> dataServerInfos = ZkUtil.getZkUtil().getDataServerList();
        //3、拼装信息返回,如果metaServer挂掉则赋值为null
        ClusterInfo res = new ClusterInfo();
        res.setMasterMetaServer(metaServerInfos);
        res.setSlaveMetaServer(metaServerInfos);
        res.setDataServer(dataServerInfos);
        return res;
    }

    public MetaServerInfo getMetaServer(){
        List<MetaServerInfo> metaServer = ZkUtil.getZkUtil().getAvailList();
        if(metaServer.size()>0) return metaServer.get(0);
        return null;
    }
}
