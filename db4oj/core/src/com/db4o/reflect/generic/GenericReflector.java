/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.internal.io.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericReflector implements Reflector, DeepClone {

    private Reflector _delegate;
    
    private final Hashtable4 _classByName = new Hashtable4(1);
    private final Collection4 _classes = new Collection4();
    private final Hashtable4 _classByID = new Hashtable4(1);
    
	private Transaction _trans;
	private YapStream _stream;
	
	public GenericReflector(Transaction trans, Reflector reflector){
		setTransaction(trans);
		_delegate = reflector;
	}
	
	public Object deepClone(Object obj) throws CloneNotSupportedException {
		return new GenericReflector(null, _delegate);
	}

	public boolean hasTransaction(){
		return _trans != null;
	}
	
	public void setTransaction(Transaction trans){
		if(trans != null){
			_trans = trans;
			_stream = trans.i_stream;
		}
	}

    public ReflectArray array() {
        return _delegate.array();
    }

    public int collectionUpdateDepth(ReflectClass claxx) {
        return _delegate.collectionUpdateDepth(claxx);
    }

    public boolean constructorCallsSupported() {
        return false;
    }

    private void ensureDelegate(ReflectClass clazz){
        if(clazz != null){
            ReflectClass claxx = (ReflectClass)_classByName.get(clazz.getName());
            if(claxx == null){
                //  We don't have to worry about the superclass, it can be null
                //  because handling is delegated anyway
                String name = clazz.getName();
                claxx = new GenericClass(this,clazz, name,null);
                _classes.add(claxx);
                _classByName.put(name, claxx);
            }
        }
    }
    
    public ReflectClass forClass(Class clazz) {
        ReflectClass claxx = _delegate.forClass(clazz);
        ensureDelegate(claxx);
        return claxx;
    }
    

    public ReflectClass forName(String className) {
        ReflectClass clazz = (ReflectClass)_classByName.get(className);
        if(clazz != null){
            return clazz;
        }
        clazz = _delegate.forName(className);
        if(clazz != null){
            ensureDelegate(clazz);
            return clazz;
        }
        // TODO: do we always want to create a generic class here anyway
        // maybe with no fields for a start?
        return null;
    }

    public ReflectClass forObject(Object obj) {
        ReflectClass clazz = _delegate.forObject(obj);
        if(clazz != null){
            ensureDelegate(clazz);
            return clazz;
        }
    	if (obj instanceof GenericObject){
            return ((GenericObject)obj).genericClass();
        }
        return null;
    }

    public boolean isCollection(ReflectClass claxx) {
        return _delegate.isCollection(claxx);
    }

    public void registerCollection(Class clazz) {
        _delegate.registerCollection(clazz);
    }

    public void registerCollectionUpdateDepth(Class clazz, int depth) {
        _delegate.registerCollectionUpdateDepth(clazz, depth);
    }

    public void registerDataClass(GenericClass dataClass) {
        _classByName.put(dataClass.getName(), dataClass);
    }
    
	public ReflectClass[] knownClasses(){
		readAll();
        return null;
	}
	
	private void readAll(){
		
		
		
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
