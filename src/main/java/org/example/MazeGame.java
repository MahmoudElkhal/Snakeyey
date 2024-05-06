package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MazeGame extends JFrame implements KeyListener {

    private int playerX = 50;
    private int playerY = 50;

    public MazeGame() {
        setTitle("Maze Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addKeyListener(this);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawRect(50, 50, 300, 300);

        // Draw player
        g.setColor(Color.RED);
        g.fillOval(playerX, playerY, 20, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (playerY > 50) {
                    playerY -= 10;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (playerY < 330) {
                    playerY += 10;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (playerX > 50) {
                    playerX -= 10;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (playerX < 330) {
                    playerX += 10;
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeGame mazeGame = new MazeGame();
            mazeGame.setVisible(true);
        });
    }
}
