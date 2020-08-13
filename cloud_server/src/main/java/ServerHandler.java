import com.ozakhlivny.cloudproject.common.messages.FileMessage;
import com.ozakhlivny.cloudproject.common.messages.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static String SERVER_ROOT_PATH = "/Users/oleg/Desktop/GeekBrains/Cloud";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (message instanceof FileRequest) {
            String fileName = ((FileRequest) message).getFileName();
            if (Files.exists(Paths.get(SERVER_ROOT_PATH + "/" + fileName))) {
                FileMessage fileMessage = new FileMessage(Paths.get(SERVER_ROOT_PATH + "/" + fileName));
                ctx.writeAndFlush(fileMessage);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        e.printStackTrace();
        ctx.close();
    }
}
