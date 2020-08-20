package com.ozakhlivny.cloudproject.client.network;

import com.ozakhlivny.cloudproject.common.command.Command;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class NetworkService {
    public static final int MAX_OBJECT_SIZE = 10 * 1024 * 1024;
    public static int DEFAULT_SERVER_PORT = 8189;
    public static String DEFAULT_SERVER_HOST = "localhost";

    private static Socket socket;
    private static ObjectDecoderInputStream in;
    private static ObjectEncoderOutputStream out;

    public static void connect(){
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), MAX_OBJECT_SIZE);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Command readCommand() throws ClassNotFoundException, IOException {
        return (Command)in.readObject();
    }

    public static boolean sendCommand(Command command) {
        try {
            out.writeObject(command);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
