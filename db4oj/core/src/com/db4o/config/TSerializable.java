/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.ObjectContainer;
import java.io.*;

/**
 * @exclude
 */
public class TSerializable implements ObjectConstructor
{
	public Object onStore(ObjectContainer con, Object object){
		try{
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			new ObjectOutputStream(byteStream).writeObject(object);
			return byteStream.toByteArray();
		}catch (Exception e){}
		return null;
	}

	public void onActivate(ObjectContainer con, Object object, Object members){
		// do nothing
	}

	public Object onInstantiate(ObjectContainer con, Object storedObject){
		try{
		    Object in = new ObjectInputStream(
			  new ByteArrayInputStream((byte[])storedObject)).readObject();
			return in;
		}catch(Exception e){}
		return null;
	}

	public Class storedClass(){
		return byte[].class;
	}
}
