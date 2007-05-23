package com.db4o.drs.test.regression;

public class NewPilot {
    String name;
    int points;
    int[] arr;
    
    public NewPilot() {
        
    }
    
    public NewPilot(String name, int points, int[] arr) {
        this.name = name;
        this.points = points;
        this.arr = arr;
    }

    public int[] getArr() {
        return arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public String toString() {
        return name + "/" + points;
    }
}
