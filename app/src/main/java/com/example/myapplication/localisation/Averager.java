package com.example.myapplication.localisation;

public interface Averager <T extends Number>{
    public T getAverage() throws Exception;
    public void addPoint(T point);
}
