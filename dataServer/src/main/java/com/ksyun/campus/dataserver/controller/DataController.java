package com.ksyun.campus.dataserver.controller;

import com.ksyun.campus.dataserver.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class DataController {
    @Autowired
    DataService dataService;
    /**
     * 1、读取request content内容并保存在本地磁盘下的文件内
     * 2、同步调用其他ds服务的write，完成另外2副本的写入
     * 3、返回写成功的结果及三副本的位置
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @RequestMapping("write")
    public ResponseEntity writeFile(@RequestHeader(required = false) String fileSystem, @RequestParam String path, @RequestParam(defaultValue ="0") int offset, @RequestParam(defaultValue = "-1") int length, @RequestBody(required = false) byte[] requestBody){
        if(length==-1) length = requestBody.length;
        boolean isWrite = dataService.write(path,requestBody,offset,length);
        if(isWrite) return ResponseEntity.ok("success");
        else return ResponseEntity.ok("fail");
    }

    @RequestMapping("delete")
    public ResponseEntity deleteFile(@RequestParam String path){
        dataService.delete(path);
        return ResponseEntity.ok("success");
    }

    /**
     * 在指定本地磁盘路径下，读取指定大小的内容后返回
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @RequestMapping("read")
    public ResponseEntity readFile(@RequestHeader(required = false) String fileSystem, @RequestParam String path, @RequestParam(defaultValue ="0") int offset, @RequestParam(defaultValue ="-1") int length){
        byte [] res = dataService.read(path,offset,length);
        return ResponseEntity.ok(res);
    }

    @RequestMapping("size")
    public ResponseEntity size(@RequestParam String path){
        return ResponseEntity.ok(dataService.size(path));
    }

    @RequestMapping("exist")
    public ResponseEntity exist(@RequestParam String path){
        return ResponseEntity.ok(dataService.exist(path));
    }

    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }
}
