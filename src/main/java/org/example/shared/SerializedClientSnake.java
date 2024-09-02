package org.example.shared;

import java.io.Serializable;
import java.util.List;

public class SerializedClientSnake implements Serializable {
    public List<Integer> x;
    public List<Integer> y;
    public SerializedClientSnake(List<Integer> x, List<Integer> y){
        this.x=x;
        this.y=y;
    }
}
