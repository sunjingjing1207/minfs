package com.ksyun.campus.dataserver.rigister;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FolderInit implements ApplicationRunner {
    @Value("${server.port}")
    private Integer port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String folderPath = "./"+port;
        File folder = new File(folderPath);
        if(folder.exists()){
            System.out.println("文件夹已存在！");
            return;
        }
        boolean created = folder.mkdirs();
        if(created) System.out.println("创建文件夹成功！");
        else System.out.println("创建文件夹失败！");
    }
}
