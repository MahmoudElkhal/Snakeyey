package org.example.snake;
import javafx.event.Event;
import org.example.network.SerializedServerSnake;
import org.example.network.SerializedSnake;
import org.example.network.SocketClient;
import org.example.network.SocketServer;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.network.SocketServer.in;

public class SnakeFrame extends JFrame implements KeyListener, ActionListener {
    public static ObjectInputStream inn=null;
    public static ObjectOutputStream outt=null;
    int x_food=100;
    int y_food=100;
    int sqUnit=20;
    Direction direction=Direction.UP;
    ArrayList<Integer>xx=new ArrayList<>();
    ArrayList<Integer>yy=new ArrayList<>();
    public static List<Integer> xx2=new ArrayList<Integer>();
    public static List<Integer>yy2=new ArrayList<Integer>();
    Timer t = new Timer(400, (ActionListener) this);
    Random r = new Random();
    boolean lost=false;

    SnakeFrame(){
            xx.add(0,100);
            xx.add(1,120);
            xx.add(2,140);
            yy.add(0,300);
            yy.add(1,300);
            yy.add(2,300);
//            xx2.add(0,160);
//            xx2.add(1,140);
//            xx2.add(2,120);
//            yy2.add(0,200);
//            yy2.add(1,200);
//            yy2.add(2,200);
        setTitle("Snake Game");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        t.start();
    }
    public void paint(Graphics g) {
//        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.GREEN);
        for(int i=1;i<xx.size();i++){
            g.fillRect(xx.get(i), yy.get(i), sqUnit, sqUnit);
        }
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(xx.get(0),yy.get(0),sqUnit,sqUnit);
        if(xx2.size()>0 && yy2.size()>0){
            g.setColor(Color.PINK);
            for(int i=1;i<xx2.size();i++){
                g.fillRect(xx2.get(i), yy2.get(i), sqUnit, sqUnit);
            }
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(xx2.get(0),yy2.get(0),sqUnit,sqUnit);

        }

        g.setColor(Color.RED);
        g.fillRect(x_food,y_food,sqUnit,sqUnit);
        if(lost){
            t.stop();
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,30));
            g.drawString("Game Over",(this.getWidth()-g.getFontMetrics().stringWidth("Game Over"))/2,this.getHeight()/2);
        }
        // Draw player

    }

    public static void main(String[] args){

        System.out.println("hi form server main");
        Thread serverThread = new Thread(() -> {
            try {
                SocketServer.main(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        SwingUtilities.invokeLater(()->{
            Frame frame = new SnakeFrame();
            frame.setVisible(true);
        });
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int keyCode=keyEvent.getKeyCode();
        switch(keyCode){
            case KeyEvent.VK_DOWN:
                direction=Direction.DOWN;break;
            case KeyEvent.VK_UP:
                direction=Direction.UP;break;
            case KeyEvent.VK_LEFT:
                direction=Direction.LEFT;break;
            case KeyEvent.VK_RIGHT:
                direction=Direction.RIGHT;break;
            default:
                break;
        }

    }



    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        SerializedSnake snake;
        //Receiving snake from client
        if(inn!=null){
            System.out.println("inn in server is not null ");
            try {
                Object o=inn.readObject();
                System.out.println(o);
                if(o!=null){
                    snake = (SerializedSnake)(o);
                    xx2=snake.x;
                    yy2=snake.y;

                    System.out.println(snake);
                    System.out.println(xx2);
                    System.out.println(yy2);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        if(outt!=null){
            SerializedServerSnake serverSnake = new SerializedServerSnake(xx,yy,x_food,y_food);
            try {
                outt.writeObject(serverSnake);
                System.out.println("server is writing"+serverSnake.x+" "+serverSnake.y);
                outt.reset();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        int x_tail=xx.get(xx.size()-1);
        int y_tail=yy.get(yy.size()-1);
        for(int i=xx.size()-1;i>0;i--){
            xx.set(i,xx.get(i-1));
        }
        for(int i=yy.size()-1;i>0;i--){
            yy.set(i,yy.get(i-1));
        }
        switch (direction){
            case UP:
                yy.set(0,yy.get(0)-sqUnit);
                break;
            case DOWN:
                yy.set(0,yy.get(0)+sqUnit);
                break;
            case LEFT:
                xx.set(0,xx.get(0)-sqUnit);
                break;
            case RIGHT:
                xx.set(0,xx.get(0)+sqUnit);
            default:
                break;
        }
        if(xx.get(0)==x_food && yy.get(0)==y_food || ( xx2.size()>0 && xx2.get(0)==x_food && yy2.get(0)==y_food)){
            xx.add(x_tail);
            yy.add(y_tail);
            x_food=r.nextInt((int)(this.getWidth()/sqUnit))*sqUnit;
            y_food=r.nextInt((int)(this.getHeight()/sqUnit))*sqUnit;
        }if(xx.get(0)<0 || xx.get(0)>this.getWidth() || yy.get(0)>this.getHeight() || yy.get(0)<0){
//                lost=true;
        }
        System.out.println("repainting");
        repaint();
    }
}
