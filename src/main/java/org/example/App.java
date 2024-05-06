package org.example;

import org.example.network.SocketClient;
import org.example.network.SocketServer;
import org.example.snake.SnakeFrame;
import org.example.snake.SnakeFrame2;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Thread serverThread = new Thread(() -> {
            try {
                SnakeFrame.main(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        // Create and start a separate thread for the client
        // Thread clientThread = new Thread(() -> {
        //     try {
        //         SocketClient.main(null);
        //     } catch (Exception e) {
        //         throw new RuntimeException(e);
        //     }
        // });
        // clientThread.start();


    }
}
