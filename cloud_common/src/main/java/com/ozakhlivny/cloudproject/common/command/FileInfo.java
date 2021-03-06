package com.ozakhlivny.cloudproject.common.command;

import com.ozakhlivny.cloudproject.common.command.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileInfo extends Command {
    private byte isFile;
    private String filename;
    private long size;

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte getIsFile() {
        return isFile;
    }

    public void setIsFile(byte isFile) {
        this.isFile = isFile;
    }

    public FileInfo(Path path) {
        try {
            filename = path.getFileName().toString();
            if(Files.isDirectory(path)){
                size = -1L;
                isFile = 0;
            }
            else {
                size = Files.size(path);
                isFile = 1;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    public FileInfo(){}
}
