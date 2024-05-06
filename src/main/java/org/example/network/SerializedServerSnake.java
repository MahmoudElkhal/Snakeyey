package org.example.network;

import java.io.Serializable;
import java.util.List;

public class SerializedServerSnake implements Serializable {
    public List<Integer> x;
    public List<Integer> y;
    public int x_food;
    public int y_food;

    public SerializedServerSnake(List<Integer> x,List<Integer> y,int x_food,int y_food){
        this.x_food=x_food;
        this.y_food=y_food;
        this.x=x;
        this.y=y;
    }
}

