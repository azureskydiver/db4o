/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.blobs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class BlobExample {
	private final static String DB4O_FILE_NAME="reference.db4o";
	public static void main(String[] args) throws IOException{
		storeCars();
	    retrieveCars();
	}
	// end main
		
	  private static void storeCars() throws IOException{
		  new File(DB4O_FILE_NAME).delete();
		  Configuration config = Db4o.newConfiguration();
		  config.setBlobPath("c:\\blobs");
		  ObjectServer server = Db4o.openServer(DB4O_FILE_NAME, 0xdb40);
			server.grantAccess("y", "y");
			//ObjectContainer container = server.openClient();
			ObjectContainer container = Db4o.openClient(config, "localhost", 0xdb40,"y", "y");
			//ObjectContainer container=Db4o.openFile(configuration , DB4O_FILE_NAME);
			
		  //ObjectContainer container=Db4o.openFile(DB4O_FILE_NAME);
		   try {
			    Car car1=new Car("Ferrari");
			    container.store(car1);
			    storeImage(car1);
			    Car car2=new Car("BMW");
			    container.store(car2);
			    storeImage(car2);
		   }  finally {
		      container.close();
		      server.close();
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
