/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.persist;

public class Car {
	 private String model;
	 private int temperature;
     
     public Car(){
     }
     
     public Car(String model){
         this.model = model;
     }
     
     public String getModel() {
         return model;
     }
     
     public void setModel(String model) {
         this.model = model;
     }
     
     public int getTemperature() {
         return temperature;
     }
     
     public void setTemperature(int temperature) {
         this.temperature = temperature;
     }
     
     public String toString() {
         return model+"-"+temperature+" C";
     }
}
