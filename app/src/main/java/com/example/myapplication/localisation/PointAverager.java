package com.example.myapplication.localisation;

public class PointAverager implements Averager<Float>{

    private float[] points;
    private int currentIndex = 0;
    public PointAverager(int numOfPoints){
        points = new float[numOfPoints];
    }

    public void addPoint(Float point){
        points[currentIndex++] = point;
    }

    public Float getAverage() throws Exception{
        if (currentIndex != points.length - 1) {
            throw new Exception("The index of PointAverager is not the same as points length!");
        }
        return getAverage(points);
    }


    private static float getAverage(float[] array){
        float tmp = 0;
        for (float f: array) {
            tmp += f;
        }

        return tmp / array.length;
    }


}
