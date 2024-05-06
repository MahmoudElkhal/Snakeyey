package org.example.network;

import org.example.snake.SnakeFrame;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocketServer {
    static SerializedSnake snakeReceived=null;
    public static ObjectInputStream in;
    public static ObjectOutputStream out;
            int x=0;
    public static List<Integer> snakex;
    public static List<Integer> snakey;
    public static synchronized List<Integer> getSnakex(){
        return snakex;
    }
    public static synchronized List<Integer> getSnakey(){
        return snakey;
    }
    public static synchronized void setSnakex(List<Integer> l){
         snakex=l;
    }
    public static synchronized void setSnakey(List<Integer> l){
         snakey=l;
    }
    public static void main(String[] args) throws Exception {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8888,50, InetAddress.getByName("0.0.0.0"));

            System.out.println("Server waiting for clients...");

            // Accept client connections
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);


            SnakeFrame.outt = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("erghh");
            SnakeFrame.outt.flush();
            SnakeFrame.inn = new ObjectInputStream(clientSocket.getInputStream());


//

    }
}

