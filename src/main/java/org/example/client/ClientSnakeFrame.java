package org.example.client;
import org.example.shared.Direction;
import org.example.shared.SerializedServerSnake;
import org.example.shared.SerializedClientSnake;
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

public class ClientSnakeFrame extends JFrame implements KeyListener, ActionListener {
    String host = "localhost";  //default host
    int port = 8888;    //default port
    Client client;

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

    ClientSnakeFrame(String host,int port) throws IOException {
        client = new Client(host, port);
        xx.add(0,100);
        xx.add(1,120);
        xx.add(2,140);
        yy.add(0,300);
        yy.add(1,300);
        yy.add(2,300);

        SerializedClientSnake clientSnake = new SerializedClientSnake(xx,yy);
        client.clientSnakeToSend=clientSnake;
        System.out.println("injected "+ clientSnake + "from clientFrame to client network");

        setTitle("Snake Game Client");
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
        Object serverObject = client.serverSnakeToReceive;
        if(serverObject!=null){
            SerializedServerSnake serverSnake = (SerializedServerSnake)(serverObject);
            xx2=serverSnake.x;
            yy2=serverSnake.y;
            x_food=serverSnake.x_food;
            y_food=serverSnake.y_food;
        }

        SerializedClientSnake clientSnake = new SerializedClientSnake(xx,yy);
        client.clientSnakeToSend=clientSnake;

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
//        if(xx2.size()>0 && ( xx2.get(0)<0 || xx2.get(0)>this.getWidth() || yy2.get(0)>this.getHeight() || yy2.get(0)<0 )){
//            serverLost=true;
//        }
        repaint();
    }
    void startClient() throws IOException {
        new Thread(() -> {
            client.start();
        }).start();
    }

    public static void main(String[] args) throws IOException {
        final String host = (args.length==2) ? args[0] : "localhost";
        final int port = (args.length==2) ? Integer.parseInt(args[1]) : 8888;
        SwingUtilities.invokeLater(()->{
            try {
            ClientSnakeFrame frame = new ClientSnakeFrame(host,port);
                frame.setVisible(true);
                frame.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
