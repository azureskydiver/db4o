/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.io;

import com.db4o.*;
import com.db4o.reflect.*;


public class ReadAllTheClasses {
	
	private final Transaction _trans;
	private final YapStream _stream;
	
	public ReadAllTheClasses(Transaction trans){
		_trans = trans;
		_stream = trans.i_stream;
	}
	
	
	
	public ClassReader[] leanStoredClasses() {
		YapWriter headerreader = _stream.getWriter(_trans, 0, YapStream.HEADER_LENGTH);
		headerreader.read();
		headerreader.incrementOffset(2 + 2 * YapConst.INTEGER_BYTES);
		int classcollid = headerreader.readInt();
		YapWriter classcollreader = _stream.readWriterByID(_trans, classcollid);
		classcollreader.read();
		int numclasses = classcollreader.readInt();
		ClassReader[] classes = new ClassReader[numclasses];
		for (int classidx = 0; classidx < numclasses; classidx++) {
			int classid = classcollreader.readInt();
			classes[classidx] = leanStoredClassByID(classid);
//			System.out.println(classes[classidx]);
		}
		return classes;
	}

	public ClassReader leanStoredClassByName(String name) {
		ClassReader[] classes=leanStoredClasses();
		for (int idx = 0; idx < classes.length; idx++) {
			if(name.equals(classes[idx].name())) {
				return classes[idx];
			}
		}
		return null;
	}
	
	public ClassReader leanStoredClassByID(int id) {
		YapWriter classreader=_stream.readWriterByID(_trans,id);
		classreader.read();
		int namelength= classreader.readInt();
		String classname= _stream.stringIO().read(classreader,namelength);
		classreader.incrementOffset(YapConst.INTEGER_BYTES); // what's this?
		int ancestorid=classreader.readInt();
		int indexid=classreader.readInt();
		int numfields=classreader.readInt();
		FieldReader[] fields=new FieldReader[numfields];
		for (int i = 0; i < numfields; i++) {
			String fieldname=null;
			int fieldnamelength= classreader.readInt();
			fieldname = _stream.stringIO().read(classreader,fieldnamelength);
		    int handlerid=classreader.readInt();
		    YapBit attribs=new YapBit(classreader.readByte());
		    boolean isprimitive=attribs.get();
		    boolean isarray = attribs.get();
		    boolean ismultidimensional=attribs.get();
			fields[i]=new FieldReader(fieldname,handlerid,isprimitive);
		}
		ClassReader clazz=new ClassReader(id,classname,ancestorid,fields);
		return clazz;
	}

	
	

}
