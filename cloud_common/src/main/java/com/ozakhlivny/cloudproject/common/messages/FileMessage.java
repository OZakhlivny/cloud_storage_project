package com.ozakhlivny.cloudproject.common.messages;

import com.ozakhlivny.cloudproject.common.messages.AbstractMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage {
    private String fileName;
    private byte[] data;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}