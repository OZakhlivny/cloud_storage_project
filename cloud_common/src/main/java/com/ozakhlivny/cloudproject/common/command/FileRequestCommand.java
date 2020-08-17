package com.ozakhlivny.cloudproject.common.command;

public class FileRequestCommand extends Command {
    private String fileName;

    public FileRequestCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
