package com.misakimei.accelerate.geom;


/**
 * Created by 18754 on 2016/8/6.
 * 四边形
 */
public class Quad extends Geom {

    public float area = 0;
    public float perimeter = 0;
    private Point o, a, b, c;

    public Quad(Point o, Point a, Point b, Point c) {
        this.o = o;
        this.a = a;
        this.b = b;
        this.c = c;
        area = (GeomTool.cross(o, a, b) + GeomTool.cross(o, b, c)) / 2;
        perimeter = GeomTool.dis(o, a) + GeomTool.dis(a, b) + GeomTool.dis(b, c) + GeomTool.dis(c, o);
    }

}
