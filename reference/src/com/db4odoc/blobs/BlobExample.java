/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.blobs;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class BlobExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
	public static void main(String[] args) {
		storeCars();
	    retrieveCars();
	}
	// end main
		
	  private static void storeCars() {
		  new File(DB4O_FILE_NAME).delete();
		  ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		   try {
			    Car car1=new Car("Ferrari");
			    container.set(car1);
			    storeImage(car1);
			    Car car2=new Car("BMW");
			    container.set(car2);
			    storeImage(car2);
		   }  finally {
		      container.close();
		    } 
	  }
	  // end storeCars
	  
	  private static void storeImage(Car car) {
		CarImage img = car.getImage();
		try {
			img.readFile();
		} catch (java.io.IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	// end storeImage
	  
	  private static void retrieveCars() {
		  ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		   try {
			   Query query = container.query();
			   query.constrain(Car.class);
			   ObjectSet result = query.execute();
			   getImages(result);
		   }  finally {
		      container.close();
		    } 
	  }
	  // end retrieveCars
	 
	  private static  void getImages(ObjectSet result){
		  while(result.hasNext()) {
	            Car car = (Car)(result.next());
	            System.out.println(car);
	            CarImage img = car.getImage();
	            try {
	            	img.writeFile();
	            } catch (java.io.IOException ex){
	            	System.out.print(ex.getMessage());
	            }
	        }
	  }
	  // end getImages
}
