/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.blobs;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;



public class BlobExample {
	public final static String YAPFILENAME="formula1.yap";
	public static void main(String[] args) {
		storeCars();
	    retrieveCars();
	}
	// end main
		
	  public static void storeCars() {
		  new File(YAPFILENAME).delete();
		  ObjectContainer db=Db4o.openFile(YAPFILENAME);
		   try {
			    Car car1=new Car("Ferrari");
			    db.set(car1);
			    storeImage(car1);
			    Car car2=new Car("BMW");
			    db.set(car2);
			    storeImage(car2);
		   }  finally {
		      db.close();
		    } 
	  }
	  // end storeCars
	  
	  public static void storeImage(Car car) {
		CarImage img = car.getImage();
		try {
			img.readFile();
		} catch (java.io.IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	// end storeImage
	  
	  public static void retrieveCars() {
		  ObjectContainer db=Db4o.openFile(YAPFILENAME);
		   try {
			   Query query = db.query();
			   query.constrain(Car.class);
			   ObjectSet result = query.execute();
			   getImages(result);
		   }  finally {
		      db.close();
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
