package org.example.snake;
import javafx.event.Event;
import org.example.network.SerializedServerSnake;
import org.example.network.SocketClient;
import org.example.network.SocketServer;
import org.example.network.SerializedSnake;

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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SnakeFrame2 extends JFrame implements KeyListener, ActionListener {
    static SocketClient client;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    int x_food=100;
    int y_food=100;
    int sqUnit=20;
    Direction direction=Direction.RIGHT;
    public static List<Integer>xx=new ArrayList<>();
    public static List<Integer>yy=new ArrayList<>();
    List<Integer> xx2=new ArrayList<Integer>();
    List<Integer>yy2=new ArrayList<Integer>();
    Timer t = new Timer(400, (ActionListener) this);
    Random r = new Random();
    boolean lost=false;

    SnakeFrame2() throws IOException {
        xx.add(0,180);
        xx.add(1,200);
        xx.add(2,220);
        yy.add(0,500);
        yy.add(1,500);
        yy.add(2,500);
//        xx2.add(0,160);
//        xx2.add(1,140);
//        xx2.add(2,120);
//        yy2.add(0,200);
//        yy2.add(1,200);
//        yy2.add(2,200);
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
        if(xx2.size()>0){
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

    public static void main(String[] args) throws Exception {

//        System.out.println("hi form main");
        // Thread clientThread = new Thread(() -> {
        //     try {
        //         SocketClient.main(null);
        //     } catch (Exception e) {
        //         throw new RuntimeException(e);
        //     }
        // });
        client = new SocketClient();
        // clientThread.start();
        SwingUtilities.invokeLater(()->{
            Frame frame = null;
            try {
                frame = new SnakeFrame2();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            frame.setVisible(true);
        });
        // Create and start a separate thread for the client

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
  System.out.println("beg of action performed");
        SerializedSnake snake;
        //Receiving snake from client
        if(in!=null) {

          System.out.println("in is not null");
        }
        //
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

        if(xx.get(0)<0 || xx.get(0)>this.getWidth() || yy.get(0)>this.getHeight() || yy.get(0)<0){
//            lost=true;
        }
        System.out.println("xx is actually "+xx);
        System.out.println("yy is actually "+yy);

            try {
              System.out.println("trying to read object");
                Object o = in.readObject();
                System.out.println(o);
                if (o != null) {
                    SerializedServerSnake serverSnake = (SerializedServerSnake) (o);
                    xx2 = serverSnake.x;
                    yy2 = serverSnake.y;
                    System.out.println(xx2+"from server");
                    System.out.println(yy2+"from server");
                    x_food=serverSnake.x_food;
                    y_food=serverSnake.y_food;


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        try {
            if(out!=null){
                out.writeObject(new SerializedSnake(xx,yy));
                System.out.println("client is writing"+xx+" "+yy);
                out.reset();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("repainting");
        repaint();
    }
}
