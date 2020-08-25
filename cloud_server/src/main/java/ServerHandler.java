import com.ozakhlivny.cloudproject.common.command.*;
import com.ozakhlivny.cloudproject.common.db.CloudUsersDB;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    ServerProperties properties;
    {
        try {
            properties = new ServerProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("Client (%s) is connected\n", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("Client (%s) was disconnected\n", ctx.channel().remoteAddress());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object command) throws Exception {
        if (command instanceof AuthorizationCommand) {
            authorizationCommand(ctx, (AuthorizationCommand) command);
        }
        else if(command instanceof GetCloudListCommand){
            cloudFileListCommand(ctx, (GetCloudListCommand)command);
        }
        else if(command instanceof FileRequestCommand){
            downloadFileCommand(ctx, (FileRequestCommand) command);
        }
        else if(command instanceof FileMessageCommand){
            uploadFileCommand(ctx, (FileMessageCommand) command);
        }
        else System.out.println("Unknown request from client");

    }

    private void uploadFileCommand(ChannelHandlerContext ctx, FileMessageCommand command) {
        try {
            boolean appendToFile = command.getCounter() > 1 ? true : false;
            FileOutputStream out = new FileOutputStream(properties.getServerRootPath() + File.separator + command.getUserDir() + File.separator + command.getFilename(), appendToFile);
            out.write(command.getData());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cloudFileListCommand(ChannelHandlerContext ctx, GetCloudListCommand command) {
        CloudFileListCommand outCommand = new CloudFileListCommand();
        outCommand.setUserName(command.getUserName());
        try {
            outCommand.fillFileList(properties.getServerRootPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush(outCommand);

    }

    private void authorizationCommand(ChannelHandlerContext ctx, AuthorizationCommand command) {

        try {
            CloudUsersDB.connectToDB();
            command.setAuthorized(CloudUsersDB.isRegisteredUser(command.getLogin(), command.getPassword()));
            CloudUsersDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush(command);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        e.printStackTrace();
        ctx.close();
    }

    private void downloadFileCommand(ChannelHandlerContext ctx, FileRequestCommand command){
        Path path = Paths.get(properties.getServerRootPath() + File.separator + command.getUserDir() + File.separator + command.getFileName());
        if (!Files.exists(path)) return;
        try {
            long fileSize = Files.size(path);
            int fileParts = (int) (fileSize / properties.getMaxBufferSize());
            fileParts += fileSize % properties.getMaxBufferSize() > 0 ? 1 : 0;
            FileMessageCommand fmCommand = new FileMessageCommand(command.getFileName(), "", new byte[properties.getMaxBufferSize()]);
            fmCommand.setParts(fileParts);
            FileInputStream inputStream = new FileInputStream(path.toFile());
            for(int i = 0; i < fileParts; i++){
                int readBytes = inputStream.read(fmCommand.getData());
                fmCommand.normalizeData(readBytes);
                fmCommand.setCounter(i + 1);
                ctx.writeAndFlush(fmCommand);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
