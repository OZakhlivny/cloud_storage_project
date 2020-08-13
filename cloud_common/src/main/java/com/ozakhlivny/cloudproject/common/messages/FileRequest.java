package com.ozakhlivny.cloudproject.common.messages;

import com.ozakhlivny.cloudproject.common.messages.AbstractMessage;

public class FileRequest extends AbstractMessage {
    private String fileName;

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
