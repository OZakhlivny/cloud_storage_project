package com.ozakhlivny.cloudproject.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileMessageCommand extends Command {

    private String userDir;
    private String fileName;
    private byte[] data;
    private int counter, parts;


    public FileMessageCommand(String fileName) {
        this.fileName = fileName;
    }

    public FileMessageCommand(String fileName, String userDir, byte[] data) {
        this.userDir = userDir;
        this.fileName = fileName;
        this.data = data;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public String getFilename() {
        return fileName;
    }

    public String getUserDir() {
        return userDir;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void normalizeData(int size){
        if(data.length > size) data = Arrays.copyOfRange(data, 0, size);
    }
}
