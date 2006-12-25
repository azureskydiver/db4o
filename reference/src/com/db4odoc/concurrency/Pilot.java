package com.db4odoc.concurrency;


public class Pilot {
	private String name;
	private int points;
	
    public Pilot(String name, int points) {
        this.name=name;
        this.points =points;
    }
    
    public String getName() {
        return name;
    }
    
    public void addPoints(int points){
    	this.points += points;
    }

    public String toString() {
        return name + "/" + points;
    }
}
