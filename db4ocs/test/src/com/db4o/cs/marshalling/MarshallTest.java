package com.db4o.cs.marshalling;

/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.YapStreamBase;

public class MarshallTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException {
		ObjectContainer oc = Db4o.openFile("marshalltest.yap");
		YapStreamBase stream = (YapStreamBase) oc;
	
		FileInputStream fis = new FileInputStream("atom.mar");
		int atomID = readInt(fis);
		int atomdLength = readInt(fis);
		System.out.println("atom id = "+atomID+", length = "+atomdLength);
		byte [] atomBytes = new byte[atomdLength];
		fis.read(atomBytes);
		fis.close();
		Object o = stream.unmarshall(atomBytes, atomID);
		System.out.println(o);
	
		
		oc.set(o);
		ObjectSet os = oc.get(null);
		show(oc, os);
		oc.close();
		
		ObjectContainer oc2 = Db4o.openFile("marshalltest.yap");
		ObjectSet os2 = oc2.get(null);
		show(oc2, os2);
		oc2.close();
	}
	
	static void show(ObjectContainer oc, ObjectSet os) {
		System.out.println("ObjectSet size = "+os.size());
		while(os.hasNext()) {
			Object o = os.next();
			long id = oc.ext().getID(o);
			System.out.println("id = "+id+ ", object = "+o);
			
		}
	}

	static void writeInt(OutputStream os, int value) throws IOException {
		os.write(value >> 24);
		os.write(value >> 16);
		os.write(value >> 8);
		os.write(value);
	}
	
	static int readInt(InputStream is) throws IOException {
		int i1 = is.read(); 
		int i2 = is.read();
		int i3 = is.read(); 
		int i4 = is.read();
		return (i1 << 24) + (i2 << 16) + (i3 << 8) + i4;
	}

}
