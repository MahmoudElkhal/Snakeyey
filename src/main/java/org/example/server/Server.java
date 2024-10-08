package org.example.server;

import org.example.shared.SerializedClientSnake;
import org.example.shared.SerializedServerSnake;

import java.io.*;
import java.net.*;

public class Server {
    public Object serverSnakeToSend;
    public Object clientSnakeToReceive;
    public int port=8888;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                new ClientHandler(clientSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                // Initialize input and output streams
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                // Start threads for reading and writing
                new Thread(new Reader()).start();
                new Thread(new Writer()).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class Reader implements Runnable {
            @Override
            public void run() {
                try {
                    System.out.println("server reading");
                    while (true) {
                        try {
                            clientSnakeToReceive = in.readObject();
                            System.out.println("Server received: " + ((SerializedClientSnake)clientSnakeToReceive).x + ((SerializedClientSnake)(clientSnakeToReceive)).y);
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
                while(serverSnakeToSend != null) {

                    try {
                        out.writeObject(serverSnakeToSend);
                        out.flush();
                        out.reset();
                        System.out.println("Server sent: " + ((SerializedServerSnake)(serverSnakeToSend)).x + ((SerializedServerSnake)(serverSnakeToSend)).y);
                        Thread.sleep(100);

                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }

}
