package org.example.network;

import java.io.Serializable;
import java.util.List;

public class SerializedSnake implements Serializable {
    public List<Integer> x;
    public List<Integer> y;
    public SerializedSnake(List<Integer> x,List<Integer> y){
        this.x=x;
        this.y=y;
    }
}
