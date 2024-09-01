package org.example;

import org.example.snake.ClientSnakeFrame;
import org.example.snake.ServerSnakeFrame;


public class App
{
    public static void main( String[] args ) throws InterruptedException {

        Thread serverThread = new Thread(() -> {
            try {
                ServerSnakeFrame.main(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        Thread.sleep(100);
        // Create and start a separate thread for the client
         Thread clientThread = new Thread(() -> {
             try {
                 ClientSnakeFrame.main(null);
             } catch (Exception e) {
                 throw new RuntimeException(e);
             }
         });
         clientThread.start();
        System.out.println("application started");

    }
}
