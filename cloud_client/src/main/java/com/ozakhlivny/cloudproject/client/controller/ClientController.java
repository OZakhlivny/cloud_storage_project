package com.ozakhlivny.cloudproject.client.controller;

import com.ozakhlivny.cloudproject.client.forms.MainWindowControllers;
import com.ozakhlivny.cloudproject.client.network.NetworkService;
import com.ozakhlivny.cloudproject.common.command.Command;
import com.ozakhlivny.cloudproject.common.command.FileMessageCommand;
import com.ozakhlivny.cloudproject.common.db.CloudUsersDB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

public class ClientController {
    public static final String USER_LOCAL_DIRECTORY = "/Users/oleg/Desktop/GeekBrains";

    private MainWindowControllers mainWindow;

    public ClientController(MainWindowControllers mainWindow) {
        this.mainWindow = mainWindow;
    }

    public boolean runAuthProcess(String login, String password){
        boolean isAuthorized = false;
        try {
            CloudUsersDB.connectToDB();
            isAuthorized = CloudUsersDB.isRegisteredUser(login, password);
            CloudUsersDB.closeConnection();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return isAuthorized;
    }

    public void runReadThread() {
        NetworkService.connect();
        new Thread(() -> {
            while (true) {
                try {
                    Command command = NetworkService.readCommand();
                    if(command instanceof FileMessageCommand){
                        FileMessageCommand fm = (FileMessageCommand) command;
                        Files.write(Paths.get(USER_LOCAL_DIRECTORY + "/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        mainWindow.updateLocalDirectory(Paths.get(USER_LOCAL_DIRECTORY));
                    }

                } catch (IOException e) {
                    System.out.println("Input stream was interrupted!");
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    NetworkService.close();
                }
            }
        }).start();
    }
}
