/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
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
        
        //TODO: will need knowledge for .NET collections here
        
    }

    public boolean constructorCallsSupported() {
        return false;
    }

    private GenericClass ensureDelegate(ReflectClass clazz){
        if(clazz == null){
        	return null;
        }
        GenericClass claxx = (GenericClass)_classByName.get(clazz.getName());
        if(claxx == null){
            //  We don't have to worry about the superclass, it can be null
            //  because handling is delegated anyway
            String name = clazz.getName();
            claxx = new GenericClass(this,clazz, name,null);
            _classes.add(claxx);
            _classByName.put(name, claxx);
        }
        return claxx;
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
        if (obj instanceof GenericObject){
            return ((GenericObject)obj).genericClass();
        }
        ReflectClass clazz = _delegate.forObject(obj);
        if(clazz != null){
            ensureDelegate(clazz);
            return clazz;
        }
        return null;
    }

    public boolean isCollection(ReflectClass claxx) {
        return _delegate.isCollection(claxx);
        
        //TODO: will need knowledge for .NET collections here
    }

    public void registerCollection(Class clazz) {
        _delegate.registerCollection(clazz);
    }

    public void registerCollectionUpdateDepth(Class clazz, int depth) {
        _delegate.registerCollectionUpdateDepth(clazz, depth);
    }

    /**
     * only used for testing for now, rename
     */
    public void registerDataClass(GenericClass dataClass) {
    	String name = dataClass.getName();
    	if(_classByName.get(name) == null){
    		_classByName.put(name, dataClass);
    		_classes.add(dataClass);
    	}
    }
    
	public ReflectClass[] knownClasses(){
		readAll();
        
        Collection4 classes = new Collection4();
		
		Iterator4 i = _classes.iterator();
		while(i.hasNext()){
            GenericClass clazz = (GenericClass)i.next();
            if(! _stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(clazz._delegate)){
                if(! clazz.isPrimitive()){
                    if(clazz.getID() > _stream.i_handlers.maxTypeID()){
                        classes.add(clazz);
                    }
                }
            }
		}
        
        ReflectClass[] ret = new ReflectClass[classes.size()];
        int j = 0;
        i = classes.iterator();
        while(i.hasNext()){
            ret[j++] = (ReflectClass)i.next();
        }
        return ret;
	}
	
	private void readAll(){
		YapWriter headerreader = _stream.getWriter(_trans, 0, YapStream.HEADER_LENGTH);
		headerreader.read();
		headerreader.incrementOffset(2 + 2 * YapConst.YAPINT_LENGTH);
		int classcollid = headerreader.readInt();
		YapWriter classcollreader = _stream.readWriterByID(_trans, classcollid);
		// FIXME: read debug ClassCollection header
        
        if (Deploy.debug) {
            classcollreader.readBegin(classcollid, YapConst.YAPCLASSCOLLECTION);
        }
        
		int numclasses = classcollreader.readInt();
		int[] classIDs = new int[numclasses];
		
		for (int classidx = 0; classidx < numclasses; classidx++) {
			classIDs[classidx] = classcollreader.readInt(); 
			ensureClassAvailability(classIDs[classidx]);
		}

		for (int classidx = 0; classidx < numclasses; classidx++) {
			ensureClassRead(classIDs[classidx]);
		}
	}
	
	private GenericClass ensureClassAvailability (int id) {

        if(id == 0){
            return null;
        }
		
        GenericClass ret = (GenericClass)_classByID.get(id);
		if(ret != null){
			return ret;
		}
        
		YapWriter classreader=_stream.readWriterByID(_trans,id);
        if (Deploy.debug) {
            classreader.readBegin(id, YapConst.YAPCLASS);
        }
		int namelength= classreader.readInt();
		String classname= _stream.stringIO().read(classreader,namelength);
		
		ret = (GenericClass)_classByName.get(classname);
		if(ret != null){
            referenceById(ret, id);
			return ret;
		}
		
		classreader.incrementOffset(YapConst.YAPINT_LENGTH); // skip empty unused int slot
        
		int ancestorid=classreader.readInt();
		
		ReflectClass nativeClass = _delegate.forName(classname);
		ret = new GenericClass(this, nativeClass,classname, ensureClassAvailability(ancestorid));
		
		// step 1 only add to _classByID, keep the class out of _classByName and _classes
        referenceById(ret, id);
		
		return ret;
	}
	
	private void ensureClassRead(int id) {

		GenericClass clazz = (GenericClass)_classByID.get(id);
		
		YapWriter classreader=_stream.readWriterByID(_trans,id);
        if (Deploy.debug) {
            classreader.readBegin(id, YapConst.YAPCLASS);
        }
		int namelength= classreader.readInt();
		String classname= _stream.stringIO().read(classreader,namelength);
		
		if(_classByName.get(classname) != null){
			return;
		}
		
        // step 2 add the class to _classByName and _classes to denote reading is completed
        _classByName.put(classname, clazz);
		_classes.add(clazz);
		
		//		 skip empty unused int slot, ancestor, index
		classreader.incrementOffset(YapConst.YAPINT_LENGTH * 3);
		
		int numfields=classreader.readInt();
		
		
		GenericField[] fields=new GenericField[numfields];
		for (int i = 0; i < numfields; i++) {
			String fieldname=null;
			int fieldnamelength= classreader.readInt();
			fieldname = _stream.stringIO().read(classreader,fieldnamelength);
			
		    int handlerid=classreader.readInt();
		    ReflectClass fieldClass = (ReflectClass)_classByID.get(handlerid);
		    
		    YapBit attribs=new YapBit(classreader.readByte());
		    boolean isprimitive=attribs.get();
		    boolean isarray = attribs.get();
		    boolean ismultidimensional=attribs.get();
		    
			fields[i]=new GenericField(classname,fieldClass, isprimitive, isarray, ismultidimensional );
		}
	}
    
    private void referenceById(GenericClass clazz, int id){
        clazz.setId(id);
        _classByID.put(id, clazz);
    }

	public void registerPrimitiveClass(int id, String name) {
		if(_classByID.get(id) != null){
			return;
		}
		ReflectClass clazz = _delegate.forName(name);
        GenericClass claxx = ensureDelegate(clazz);
        referenceById(claxx, id);
	}

}
