package org.example.server;
import org.example.shared.SerializedServerSnake;
import org.example.shared.SerializedClientSnake;
import org.example.shared.Direction;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerSnakeFrame extends JFrame implements KeyListener, ActionListener {
    int port=8888;
    public Server server;
    int x_food = 100;
    int y_food = 100;
    int sqUnit = 20;
    Direction direction=Direction.UP;
    List<Integer> xx = new ArrayList<>();
    List<Integer> yy = new ArrayList<>();
    List<Integer> xx2 = new ArrayList<>();
    List<Integer> yy2 = new ArrayList<>();
    Timer t = new Timer(200, (ActionListener) this);
    Random r = new Random();
    boolean lost = false;
    boolean clientLost = false;

    ServerSnakeFrame(int inputPort){
            port=inputPort;
            server=new Server(inputPort);
            xx.add(0,100);
            xx.add(1,120);
            xx.add(2,140);
            yy.add(0,300);
            yy.add(1,300);
            yy.add(2,300);
            SerializedServerSnake serverSnake = new SerializedServerSnake(xx,yy,x_food,y_food);
            server.serverSnakeToSend=serverSnake;
            System.out.println("injected "+ serverSnake + "from serverFrame to server network");

        setTitle("Snake Game Server");
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

        g.setColor(Color.DARK_GRAY);    // filling the background
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.MAGENTA);    // constructing the snake
        for(int i=1;i<xx.size();i++){
            g.fillRect(xx.get(i), yy.get(i), sqUnit, sqUnit);
        }
        g.setColor(Color.LIGHT_GRAY);   // snake head
        g.fillRect(xx.get(0),yy.get(0),sqUnit,sqUnit);

        if(xx2.size()>0 && yy2.size()>0){   // print client snake if not null
            g.setColor(Color.RED);
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
    public void keyReleased(KeyEvent keyEvent) {}

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object clientObject = server.clientSnakeToReceive;
        if(clientObject!=null){
                SerializedClientSnake clientSnake = (SerializedClientSnake)(clientObject);
                xx2=clientSnake.x;
                yy2=clientSnake.y;

        }

            SerializedServerSnake serverSnake = new SerializedServerSnake(xx,yy,x_food,y_food);
            server.serverSnakeToSend=serverSnake;

        int x_tail=xx.get(xx.size()-1);
        int y_tail=yy.get(yy.size()-1);
        for(int i=xx.size()-1;i>0;i--){
            xx.set(i,xx.get(i-1));
        }
        for(int i=yy.size()-1;i>0;i--){
            yy.set(i,yy.get(i-1));
        }
        switch (direction){     //
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
        if(xx.get(0)==x_food && yy.get(0)==y_food ){    // server snake ate the food
            xx.add(x_tail);
            yy.add(y_tail);
            x_food=r.nextInt((int)(this.getWidth()/sqUnit))*sqUnit;
            y_food=r.nextInt((int)(this.getHeight()/sqUnit))*sqUnit;
        }
        if(xx2.size()>0 && xx2.get(0)==x_food && yy2.get(0)==y_food){   // client snake ate the food
            xx2.add(x_tail);
            yy2.add(y_tail);
            x_food=r.nextInt((int)(this.getWidth()/sqUnit))*sqUnit;
            y_food=r.nextInt((int)(this.getHeight()/sqUnit))*sqUnit;
        }
        if(xx.get(0)<0 || xx.get(0)>this.getWidth() || yy.get(0)>this.getHeight() || yy.get(0)<0){
            lost=true;
        }
        if(xx2.size()>0 && ( xx2.get(0)<0 || xx2.get(0)>this.getWidth() || yy2.get(0)>this.getHeight() || yy2.get(0)<0 )){
            clientLost=true;
        }
        repaint();
    }
    void startServer() throws IOException {
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        final int inputPort= (args.length==1) ? Integer.parseInt(args[0]):8888 ;
        SwingUtilities.invokeLater(()->{
            ServerSnakeFrame frame = new ServerSnakeFrame(inputPort);
            try {
                frame.startServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            frame.setVisible(true);
        });
    }
}
