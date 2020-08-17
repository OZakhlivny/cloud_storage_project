import com.ozakhlivny.cloudproject.common.command.AuthorizationCommand;
import com.ozakhlivny.cloudproject.common.command.FileMessageCommand;
import com.ozakhlivny.cloudproject.common.command.FileRequestCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static String SERVER_ROOT_PATH = "/Users/oleg/Desktop/GeekBrains/Cloud";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (message instanceof AuthorizationCommand) {
            authorizationCommand(ctx, (AuthorizationCommand) message);
        }
        else if(message instanceof FileRequestCommand){
            downloadFileCommand(ctx, (FileRequestCommand) message);
        }
        else System.out.println("Unknown request from client");

    }

    private void authorizationCommand(ChannelHandlerContext ctx, AuthorizationCommand message) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        e.printStackTrace();
        ctx.close();
    }

    private void downloadFileCommand(ChannelHandlerContext ctx, FileRequestCommand message){
        try {
            String fileName = message.getFileName();
            if (Files.exists(Paths.get(SERVER_ROOT_PATH + "/" + fileName))) {
                FileMessageCommand fileMessageCommand = new FileMessageCommand(Paths.get(SERVER_ROOT_PATH + "/" + fileName));
                ctx.writeAndFlush(fileMessageCommand);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
