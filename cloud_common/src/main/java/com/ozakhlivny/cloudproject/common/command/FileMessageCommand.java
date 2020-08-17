package com.ozakhlivny.cloudproject.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessageCommand extends Command {
    private String fileName;
    private byte[] data;

    public FileMessageCommand(Path path) throws IOException {
        fileName = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
