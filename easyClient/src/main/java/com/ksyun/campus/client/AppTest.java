package com.ksyun.campus.client;

import com.ksyun.MetaServerInfo;
import com.ksyun.campus.client.util.ZkUtil;
import org.I0Itec.zkclient.ZkClient;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppTest {
    public static void main(String[] args) throws Exception {
        EFileSystem eFileSystem = new EFileSystem();
        eFileSystem.mkdir("/");
        FSOutputStream fso = eFileSystem.create("/test/b.txt");
        fso.write("abc".getBytes());
        System.out.println(eFileSystem.listFileStats("/test"));
        System.out.println(eFileSystem.getClusterInfo());
    }
}
