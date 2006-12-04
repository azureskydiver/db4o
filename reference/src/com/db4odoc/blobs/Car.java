/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4odoc.blobs;


public class Car {
    private String model;
    private CarImage img;
    
   
    public Car(String model) {
        this.model=model;
        img=new CarImage();
        img.setFile(model+".jpg");
    }
  
    public CarImage getImage() {
        return img;
    }
    
    public String toString() {
        return model +"(" + img.getFile() + ")" ;
    }
}
