package com.ksyun.campus.client;

import com.ksyun.MetaServerInfo;
import com.ksyun.campus.client.util.ZkUtil;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FSOutputStream extends OutputStream {
    private String path;
    private RestTemplate restTemplate = new RestTemplate();

    public FSOutputStream(String path) {
        this.path = path;
    }

    @Override
    public void write(int b) throws IOException {
        byte[] c = new byte[1];
        c[0] = (byte)b;
        this.write(c,0,1);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b,0,b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        MetaServerInfo metaServer = getMetaServer();
        if(metaServer==null) return;
        byte[] c = new byte[len];
        System.arraycopy(b,off,c,0,len);
        String res = restTemplate.postForObject("http://"+metaServer.getIP()+":"+metaServer.getPort()+"/create?path="+path+"&offset=0"+"&length="+len,c,String.class);
        System.out.println(res);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
    public MetaServerInfo getMetaServer(){
        List<MetaServerInfo> metaServer = ZkUtil.getZkUtil().getAvailList();
        if(metaServer.size()>0) return metaServer.get(0);
        return null;
    }
}
