package com.ozakhlivny.cloudproject.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CloudFileListCommand extends Command{
    private List<FileInfo> fileList;
    private String userName;

    public void fillFileList(String cloudRoot) throws IOException {
        if(userName != null) {
            Path path = Paths.get(cloudRoot, userName);
            if(Files.exists(path)) {
                fileList = Files.list(path).map(FileInfo::new)
                        .filter(p -> !p.getFilename().equals(".DS_Store"))
                        .collect(Collectors.toList());
            }
        }
    }

    public List<FileInfo> getFileList(){
        return fileList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
