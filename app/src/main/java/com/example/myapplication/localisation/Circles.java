package com.example.myapplication.localisation;

public class Circles {
    private float minCollisionDistance;
    public Circles(float minCollisionDistance){
        this.minCollisionDistance = minCollisionDistance;
    }

    public float[] scaleLengths(float[] lengths, Square square){
        float[] tmp = new float[lengths.length];
        int ii = 0;
        float sum = 0;
        for (float t: lengths) {
            sum += t;
        }
        float factor = sum / square.getLength() / 4;
        for (float t: lengths) {
            tmp[ii++] = t / factor;
        }

        return tmp;
    }

    public int checkIfCirclesCollideInOnePoint(Point[] points, float[] lengths, Square square){
        return 0;
    }
}
