package com.ozakhlivny.cloudproject.common.command;

import com.ozakhlivny.cloudproject.common.files.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CloudFileListCommand extends Command{
    List<FileInfo> fileList;

    public void fillFileList(String userDir) throws IOException {
        Path path = Paths.get(userDir);
        fileList.addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
    }

    public List<FileInfo> getFileList(){
        return fileList;
    }
}
