package com.ksyun.campus.metaserver.controller;

import com.ksyun.campus.metaserver.services.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class MetaController {
    @Autowired
    MetaService metaService;
    @RequestMapping("stats")
    public ResponseEntity stats(@RequestHeader(required = false) String fileSystem,@RequestParam String path){
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping("create")
    public ResponseEntity createFile(@RequestHeader(required = false) String fileSystem, @RequestParam String path, @RequestParam(defaultValue ="0") int offset, @RequestParam(defaultValue ="-1") int length, @RequestBody(required = false) byte[] data){
        boolean res = metaService.createFile("/default"+path,data,offset,length);
        if(res) return ResponseEntity.ok("success");
        else return ResponseEntity.ok("fail");
    }
    @RequestMapping("mkdir")
    public ResponseEntity mkdir(@RequestHeader(required = false) String fileSystem, @RequestParam String path){
        if(path.equals("/")) path="";
        String res = metaService.mkdir("/default"+path);
        return ResponseEntity.ok(res);
    }
    @RequestMapping("listdir")
    public ResponseEntity listdir(@RequestHeader String fileSystem,@RequestParam String path){
        return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping("delete")
    public ResponseEntity delete(@RequestHeader(required = false) String fileSystem, @RequestParam String path){
        boolean res =  metaService.delete("/default"+path);
        return ResponseEntity.ok(res);
    }

    @RequestMapping("read")
    public ResponseEntity read(@RequestHeader(required = false) String fileSystem, @RequestParam String path, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "-1") int length){
        return ResponseEntity.ok(metaService.read("/default"+path,offset,length));
    }

    @RequestMapping("getstat")
    public ResponseEntity getStat(@RequestHeader(required = false) String fileSystem, @RequestParam String path){
        if(path.equals("/")) path="";
        return ResponseEntity.ok(metaService.getFileStat("/default"+path));
    }

    @RequestMapping("liststats")
    public ResponseEntity listStats(@RequestHeader(required = false) String fileSystem, @RequestParam String path) {
        if(path.equals("/")) path="";
        return ResponseEntity.ok(metaService.listStats("/default"+path));
    }
    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }

}
