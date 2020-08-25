package com.ozakhlivny.cloudproject.common.command;

public class FileRequestCommand extends Command {
    private String fileName;
    private String userDir;

    public FileRequestCommand(String fileName, String userDir) {
        this.fileName = fileName;
        this.userDir = userDir;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUserDir(){ return userDir;}
}
