package com.misakimei.accelerate.geom;

/**
 * Created by 18754 on 2016/8/7.
 */
public class Circle extends Geom {
    public Point center;
    public float radius;


    public Circle(Point c, float radius) {
        this.center = c;
        this.radius = radius;
    }
}
