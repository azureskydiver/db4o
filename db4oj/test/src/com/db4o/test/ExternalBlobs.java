/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.types.*;

public class ExternalBlobs {
	
	static final String BLOB_FILE_IN = Test.BLOB_PATH + "/regressionBlobIn.txt"; 
	static final String BLOB_FILE_OUT = Test.BLOB_PATH + "/regressionBlobOut.txt"; 
	
	Blob blob;
	
	void configure(){
		try{
			Db4o.configure().setBlobPath(Test.BLOB_PATH);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	void storeOne(){
	}
	
	public void testOne(){
		
		if(new File(Test.BLOB_PATH).exists()){
			try{
				char[] chout = new char[]{'H', 'i', ' ', 'f','o', 'l', 'k','s'};
				new File(BLOB_FILE_IN).delete();
				new File(BLOB_FILE_OUT).delete();
				FileWriter fw = new FileWriter(BLOB_FILE_IN);
				fw.write(chout);
				fw.flush();
				fw.close();
				blob.readFrom(new File(BLOB_FILE_IN));
				double status = blob.getStatus();
				while(status > Status.COMPLETED){
					Thread.sleep(50);
					status = blob.getStatus();
				}
				
				blob.writeTo(new File(BLOB_FILE_OUT));
				status = blob.getStatus();
				while(status > Status.COMPLETED){
					Thread.sleep(50);
					status = blob.getStatus();
				}
				File resultingFile = new File(BLOB_FILE_OUT);
				Test.ensure(resultingFile.exists());
				if(resultingFile.exists()){
					FileReader fr = new FileReader(resultingFile);
					char[] chin = new char[chout.length];
					fr.read(chin);
					for (int i = 0; i < chin.length; i++) {
						Test.ensure(chout[i] == chin[i]);
                    }
                    fr.close();
				}
			}catch(Exception e){
				Test.ensure(false);
				e.printStackTrace();
			}
		}
		
	}

}
