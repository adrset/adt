package com.example.myapplication.localisation;

public class Square {

    public float length;
    public Point[] points;

    public Square(float len) {
        this.length = len;
        points = new Point[4];
        computePoints();
    }

    public float getLength(){
        return length;
    }


    private void computePoints() {
        points[0] = new Point(0, 0);
        points[1] = new Point(length, 0);
        points[2] = new Point(0, length);
        points[3] = new Point(length, length);
    }

}
