package com.ksyun.campus.client;

import com.ksyun.MetaServerInfo;
import com.ksyun.campus.client.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FSInputStream extends InputStream {

    private String path;
    private RestTemplate restTemplate = new RestTemplate();
    private int readOff=0;

    public FSInputStream(String path){
        this.path = path;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int res = this.read(b,readOff,1);
        readOff++;
        if(res==-1) return res;
        else return (int)b[0];
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b,0,b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return -1;
        byte[] res = restTemplate.getForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/read?path="+path+"&offset=0"+"&length="+len,byte[].class);
        if(res.length==1 && (int)res[0]==0) return -1;
        len = Math.min(res.length,b.length-off);
        System.arraycopy(res, 0, b, off,len);
        return Math.min(res.length,len);
    }

    public MetaServerInfo getMetaServer(){
        List<MetaServerInfo> metaServer = ZkUtil.getZkUtil().getAvailList();
        if(metaServer.size()>0) return metaServer.get(0);
        return null;
    }

    @Override
    public void close() throws IOException {
        path = null;
        restTemplate = null;
    }
}
