package com.ksyun.campus.metaserver.services;

import com.ksyun.DataServerInfo;
import com.ksyun.FileType;
import com.ksyun.ReplicaData;
import com.ksyun.StatInfo;
import com.ksyun.campus.metaserver.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MetaService {
    private ZkClient zk = ZkUtil.getZkUtil().getZkClient();
    public List<DataServerInfo>  pickDataServer(){
        List<DataServerInfo> dataServerList = ZkUtil.getZkUtil().getDataServerList();
        if(dataServerList.size()<3) return null;
        return dataServerList.subList(0,3);
    }

    //写文件
    public boolean createFile(String path, byte[] data,int offset, int length){
        RestTemplate restTemplate = new RestTemplate();
        //如果存在，则更新dataServer的文件，以及更新StatInfo
        if(zk.exists(path)){
            StatInfo info = zk.readData(path);
            List<ReplicaData> replicaData = info.getReplicaData();
            for(ReplicaData r : replicaData){
                restTemplate.postForObject("http://"+r.getDsNode()+"/write?path="+r.getPath()+"&offset="+offset+"&length="+length,data,String.class);
            }
            info.setSize(restTemplate.getForObject("http://"+replicaData.get(0).getDsNode()+"/size?path="+replicaData.get(0).getPath(),Long.class));
            info.setMtime(System.currentTimeMillis());
            zk.writeData(path,info);
            System.out.println((StatInfo)zk.readData(path));
            return true;
        }
        String[]temp = path.split("/");
        String dirPath="";
        String fileName="";
        for(int i=1;i<temp.length-1;i++){
            dirPath =dirPath+"/"+temp[i];
        }
        fileName = temp[temp.length-1];

        if(!zk.exists(dirPath)) mkdir(dirPath);

        //创建新的文件对象，将数据写入到dataserver后提交到zk中
        StatInfo fileInfo = new StatInfo();
        fileInfo.setMtime(System.currentTimeMillis());
        fileInfo.setSize(data.length);
        fileInfo.setPath(path);
        fileInfo.setType(FileType.File);

        //生成存储到本地的唯一文件名，UUID+"_"+fileName
        String localName = UUID.randomUUID().toString()+"_"+fileName;

        //挑选3台dataServer作为数据服务器
        List<DataServerInfo> dataServer = pickDataServer();
        if(dataServer==null) return false;

        //向3台服务器写入数据,并设置文件的replicaData
        List<ReplicaData> replicaData = new ArrayList<>();
        for(DataServerInfo d : dataServer){
            ReplicaData r = new ReplicaData(localName,d.getHost()+":"+d.getPort(),"./"+d.getPort()+"/"+localName);
            replicaData.add(r);
            restTemplate.postForObject("http://"+r.getDsNode()+"/write?path="+r.getPath()+"&offset="+offset+"&length="+length,data,String.class);
        }
        fileInfo.setReplicaData(replicaData);

        //向zk中提交文件信息
        if(!zk.exists(path)) zk.create(path,null,CreateMode.PERSISTENT);
        zk.writeData(path,fileInfo);
        System.out.println((StatInfo)zk.readData(path));
        return true;
    }
    public String mkdir(String path){
        //1、判断path是否符合要求
        if(!isPathValid(path)){return new String("路径的格式不正确！");}
        //2、判断文件夹是否已存在
        if(zk.exists(path)){return new String("文件夹已存在！");}

        //3、递归创建文件夹,将文件夹统一创建在/default中
        try{
            String[] temp;
            temp = path.split("/");
            String recordPath="/";
            for(int i=1;i<temp.length;i++){
                recordPath += temp[i];
                if(!zk.exists(recordPath)) zk.create(recordPath,new StatInfo(recordPath,0,System.currentTimeMillis(),FileType.Directory,null), CreateMode.PERSISTENT);
                recordPath += "/";
            }
            return new String("创建成功！");
        }catch (Exception e){
            System.out.println(e);
            return new String("创建失败！");
        }
    }

    public boolean delete(String path){
        if(!zk.exists(path)) return false;
        if(zk.getChildren(path).size()!=0) return false;
        StatInfo info = zk.readData(path);
        if(info.getType()==FileType.Directory) return zk.delete(path);
        //删除dataServer中的文件，删除不成功，5秒dataServer自动检测删除
        RestTemplate restTemplate = new RestTemplate();
        List<ReplicaData> replicaData = info.getReplicaData();
        try {
            for (ReplicaData r : replicaData) {
                restTemplate.getForObject("http://" + r.getDsNode() + "?path=" + r.getPath(), String.class);
            }
        }catch (Exception e){}
        //删除zk中的元数据
        return zk.delete(path);
    }

    public byte[] read(String path,int offset, int length){
        if(!zk.exists(path)) return null;
        StatInfo info = zk.readData(path);
        List<ReplicaData> replicaData = info.getReplicaData();
        RestTemplate restTemplate = new RestTemplate();
        for(ReplicaData r : replicaData){
            try{
                byte[] res = restTemplate.getForObject("http://"+r.getDsNode()+"/read?path="+r.getPath()+"&offset="+offset+"&length="+length,byte[].class);
                return res;
            }catch (Exception e){}
        }
        return null;
    }

    public boolean isPathValid(String path) {
        String pattern = "^/\\w+(?:/\\w+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(path);
        return matcher.matches();
    }

    public StatInfo getFileStat(String path){
        if(zk.exists(path)) return zk.readData(path);
        return null;
    }

    public List<StatInfo> listStats(String path){
        if(!zk.exists(path)) return null;
        List<StatInfo> res = new ArrayList<>();
        List<String> child = zk.getChildren(path);
        for (String c : child) {
            StatInfo info = zk.readData(path + "/" + c);
            res.add(info);
        }
        return res;
    }
}
