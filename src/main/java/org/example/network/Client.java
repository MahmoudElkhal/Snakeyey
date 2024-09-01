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
        new Thread(new Reader()).start();
        new Thread(new Writer()).start();

    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println("client reading");
                while (true) {
                    try {
                        serverSnakeToReceive = in.readObject();
                        System.out.println("Client received: " + ((SerializedServerSnake)serverSnakeToReceive).x + ((SerializedServerSnake)serverSnakeToReceive).y);
//                            Thread.sleep(100);
                    } catch (EOFException eof) {
                        // Handle EOFException but do not quit the loop
                        System.out.println("EOFException encountered, waiting for more data...");
                        try {
                            Thread.sleep(100); // Add a small delay before retrying
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt(); // Restore interrupted status
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class Writer implements Runnable {
        @Override
        public void run() {
            while(clientSnakeToSend != null) {

                try {
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
