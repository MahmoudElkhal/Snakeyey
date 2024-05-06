package org.example.network;
import org.example.snake.SnakeFrame2;

import java.io.*;
import java.net.*;
import java.util.List;

public class SocketClient {
    public static ObjectInputStream in;
    public static ObjectOutputStream out;
    public static List<Integer> snakex ;
    public static List<Integer> snakey ;
    static SerializedSnake snake;
    public SocketClient() throws Exception {
        System.out.println("hello from client entry");


            // Connect to the server
            Socket socket = new Socket("localhost", 8888);

            // Create input and output streams for communication with the server
//            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        SnakeFrame2.out = new ObjectOutputStream((socket.getOutputStream()));;
        SnakeFrame2.out.flush();
        SnakeFrame2.in = new ObjectInputStream((socket.getInputStream()));
        System.out.println("the two streams in client are created");

    }
}

