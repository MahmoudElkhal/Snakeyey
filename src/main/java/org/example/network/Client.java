package org.example.network;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Object serverSnakeToReceive;
    public Object clientSnakeToSend;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void start() {
        // Initialize input and output streams

        // Start threads for reading and writing
//        new Thread(new Reader()).start();
        new Thread(new Writer()).start();

    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            try {
                while ((serverSnakeToReceive = in.readObject()) != null) {
                    System.out.println("Client received: " + serverSnakeToReceive);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Writer implements Runnable {
        @Override
        public void run() {
            while(clientSnakeToSend != null) {

                try {
                        Object o = new Object();
                        out.writeObject(clientSnakeToSend);
                        out.flush();
                        out.reset();
                        System.out.println("Client sent: " + ((SerializedClientSnake)(clientSnakeToSend)).x + ((SerializedClientSnake)(clientSnakeToSend)).y);
                        Thread.sleep(100);

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}
