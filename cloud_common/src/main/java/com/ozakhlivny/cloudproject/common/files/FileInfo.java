package com.ozakhlivny.cloudproject.common.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileInfo {
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

    public FileInfo(Path path) {
        try {
            filename = path.getFileName().toString();
            if(Files.isDirectory(path)) size = -1L;
            else size = Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }
}
