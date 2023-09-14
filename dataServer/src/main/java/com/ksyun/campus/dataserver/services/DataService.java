package com.ksyun.campus.dataserver.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class
DataService {

    /**
     * 实现随机写，offset偏移量，length写入的长度
     * @return boolean
     */
    public boolean write(String path, byte[] data, int offset, int length) {
        //避免写入长度超过byte[]长度
        if(length>data.length) length = data.length;
        try(RandomAccessFile file = new RandomAccessFile(path,"rw")){
            //避免offset越界
            if(offset>file.length()) offset = (int)file.length();
            file.seek(offset);
            file.write(Arrays.copyOfRange(data, 0, length));
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public void delete(String path){
        File file = new File(path);
        if(file.exists()) file.delete();
    }

    public Long size(String path){
        File file = new File(path);
        if(file.exists()) return file.length();
        return 0L;
    }

    /**
     * 随机读，offset文件偏移量，length长度
     */
    public byte[] read(String path, int offset, int length) {
        byte[] res =null;
        try (RandomAccessFile file = new RandomAccessFile(path,"rw")){
            if(length==-1 || length>file.length()) { length = (int)file.length();}
            if(offset>file.length()) offset = (int)file.length();
            res = new byte[length];
            file.seek(offset);
            file.read(res,0,length);
        }catch (Exception e){
            System.out.println(e);
        }
        return res;
    }
    public boolean exist(String path){
        File file = new File(path);
        if(file.exists()) return true;
        return false;
    }
}
