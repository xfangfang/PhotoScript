package tools;

import customView.LineBox;

import java.io.Serializable;


/**
 * 这个类的作用是在保存工程的时候记录下一个线的所有属性
 */
public class SerLine implements Serializable{
    double x1;
    double x2;
    double y1;
    double y2;
    double x;
    double y;
    public SerLine(double x1, double y1, double x2, double y2, double x, double y){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x = x;
        this.y = y;
    }
    public LineBox constuctLine(){
        return new LineBox(x1, y1, x2, y2, x, y);
    }
}
