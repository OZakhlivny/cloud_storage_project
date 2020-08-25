package com.ozakhlivny.cloudproject.common.command;

public class GetCloudListCommand extends Command{
    private String userName;

    public GetCloudListCommand(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
