package com.ozakhlivny.cloudproject.client.controller;

import com.ozakhlivny.cloudproject.client.forms.MainWindowControllers;
import com.ozakhlivny.cloudproject.client.network.NetworkService;
import com.ozakhlivny.cloudproject.common.command.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientController {

    private final NetworkService networkService;

    private final MainWindowControllers mainWindow;
    private boolean isAuthorized;


    private String userName;

    public ClientController(MainWindowControllers mainWindow) {
        this.mainWindow = mainWindow;
        isAuthorized = false;
        networkService = new NetworkService();
        networkService.connect();
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void runAuthProcess(String login, String password){
        AuthorizationCommand authorizationCommand = new AuthorizationCommand();
        authorizationCommand.setLogin(login);
        authorizationCommand.setPassword(password);
        networkService.sendCommand(authorizationCommand);
        try {
            Command command = networkService.readCommand();
            if (command instanceof AuthorizationCommand) {
                isAuthorized = ((AuthorizationCommand) command).isAuthorized();
                userName = ((AuthorizationCommand) command).getLogin();
                mainWindow.clientIsAuthorized();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void getCloudList() {
        if(isAuthorized()){
            GetCloudListCommand cloudFileListCommand = new GetCloudListCommand(userName);
            networkService.sendCommand(cloudFileListCommand);
            try {
                Command command = networkService.readCommand();
                if (command instanceof CloudFileListCommand) {
                    CloudFileListCommand cfCommand = (CloudFileListCommand) command;
                    mainWindow.updateCloudDirectory(cfCommand.getFileList());
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadFile(String fileName, String pathFrom, String pathTo) {
        Path path = Paths.get(pathFrom + File.separator + fileName);
        if (!Files.exists(path)) return;
        try {
            long fileSize = Files.size(path);
            int fileParts = (int) (fileSize / networkService.properties.getMaxBufferSize());
            fileParts += fileSize % networkService.properties.getMaxBufferSize() > 0 ? 1 : 0;
            FileMessageCommand command = new FileMessageCommand(fileName, pathTo, new byte[networkService.properties.getMaxBufferSize()]);
            FileInputStream inputStream = new FileInputStream(path.toFile());
            for(int i = 0; i < fileParts; i++){
                int readBytes = inputStream.read(command.getData());
                command.normalizeData(readBytes);
                command.setCounter(i + 1);
                networkService.sendCommand(command);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCloudList();
    }

    public void downloadFile(String fileName, String pathFrom, String pathTo) {
        FileRequestCommand command = new FileRequestCommand(fileName, pathFrom);
        networkService.sendCommand(command);
        try {
            int readCounter = 0;
            while(true) {
                FileMessageCommand fmCommand = (FileMessageCommand) networkService.readCommand();
                readCounter++;
                boolean appendToFile = fmCommand.getCounter() > 1 ? true : false;
                FileOutputStream out = new FileOutputStream(pathTo + File.separator + fmCommand.getFilename(), appendToFile);
                out.write(fmCommand.getData());
                out.close();
                if(readCounter == fmCommand.getParts()) break;
            }
            mainWindow.updateLocalDirectory(Paths.get(pathTo));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getLocalUserDirectory(){
        return networkService.properties.getUserLocalDirectory();
    }
}
