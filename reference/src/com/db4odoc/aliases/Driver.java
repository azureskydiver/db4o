package com.db4odoc.aliases;


public class Driver {
	private String name;
	private int points;
    
    public Driver(String name, int points) {
        this.name=name;
        this.points =points;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + "/" + points;
    }
}
