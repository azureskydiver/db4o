/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.blobs;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.types.*;

public class CarImage {
	Blob blob;
	private String fileName = null;
	private String inFolder = "blobs\\in\\";	
	private String outFolder = "blobs\\out\\";
	
	public CarImage() {
		
	}

	public void setFile(String fileName){
			this.fileName = fileName;
	}
	
	public String getFile(){
		return fileName;
	}
	
	public boolean readFile() throws java.io.IOException {
		blob.readFrom(new File(inFolder + fileName));
		double status = blob.getStatus();
		while(status >  Status.COMPLETED){
			try {
				Thread.sleep(50);
				status = blob.getStatus();
			} catch (InterruptedException ex){
				System.out.println(ex.getMessage());
			}
		}
        return (status == Status.COMPLETED);
	}
	
	public boolean writeFile() throws java.io.IOException {
		blob.writeTo(new File(outFolder + fileName));
		double status = blob.getStatus();
		while(status > Status.COMPLETED){
			try {
				Thread.sleep(50);
				status = blob.getStatus();
			} catch (InterruptedException ex){
				System.out.println(ex.getMessage());
			}
		}
        return (status == Status.COMPLETED);
	}
}
