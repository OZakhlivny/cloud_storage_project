package com.ozakhlivny.cloudproject.client.network;

import com.ozakhlivny.cloudproject.client.ClientProperties;
import com.ozakhlivny.cloudproject.common.command.Command;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class NetworkService {
    public ClientProperties properties;

    {
        try {
            properties = new ClientProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static final int MAX_OBJECT_SIZE = 20 * 1024 * 1024;
    public static final int MAX_BUFFER_SIZE = 1024; //10 * 1024 * 1024;
    public static int DEFAULT_SERVER_PORT = 8189;
    public static String DEFAULT_SERVER_HOST = "localhost";*/

    private  Socket socket;
    private  ObjectDecoderInputStream in;
    private  ObjectEncoderOutputStream out;

    public  void connect(){
        try {
            socket = new Socket(properties.getDefaultServerHost(), properties.getDefaultServerPort());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), properties.getMaxObjectSize());
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public  void close() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Command readCommand() throws ClassNotFoundException, IOException {
        return (Command)in.readObject();
    }

    public boolean sendCommand(Command command) {
        try {
            out.writeObject(command);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
