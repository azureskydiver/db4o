/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericReflector implements Reflector, DeepClone {

    private Reflector _delegate;
    private GenericArray _array;
    
    // TODO: _classByName is our fastest access to the classes
    //      consider to improve access speed by special 
    //      hashcode, maybe on names
    
    private final Hashtable4 _classByName = new Hashtable4(1);
    private final Collection4 _classes = new Collection4();
    private final Hashtable4 _classByID = new Hashtable4(1);
    
	private Transaction _trans;
	private YapStream _stream;
	
	public GenericReflector(Transaction trans, Reflector delegateReflector){
		setTransaction(trans);
		_delegate = delegateReflector;
        if(_delegate != null){
            _delegate.setParent(this);
        }
	}
	
	public Object deepClone(Object obj) throws CloneNotSupportedException {
        GenericReflector myClone = new GenericReflector(null, (Reflector)_delegate.deepClone(this));
//        Iterator4 i = _classes.iterator();
//        while(i.hasNext()){
//            GenericClass clazz = (GenericClass)i.next();
//            
//        }
		return myClone;
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
        if(_array == null){
            _array = new GenericArray(this);
        }
        return _array;
    }

    public int collectionUpdateDepth(ReflectClass claxx) {
        return _delegate.collectionUpdateDepth(claxx);
        
        //TODO: will need knowledge for .NET collections here
    }

    public boolean constructorCallsSupported() {
        return _delegate.constructorCallsSupported();
    }

    GenericClass ensureDelegate(ReflectClass clazz){
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
        if(clazz == null){
            return null;
        }
        ReflectClass claxx = forName(clazz.getName());
        if(claxx != null){
            return claxx;
        }
        claxx = _delegate.forClass(clazz);
        return ensureDelegate(claxx);
    }
    

    public ReflectClass forName(String className) {
        ReflectClass clazz = (ReflectClass)_classByName.get(className);
        if(clazz != null){
            return clazz;
        }
        clazz = _delegate.forName(className);
        if(clazz != null){
            return ensureDelegate(clazz);
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
            return ensureDelegate(clazz);
        }
        return null;
    }
    
    public Reflector getDelegate(){
        return _delegate;
    }

    public boolean isCollection(ReflectClass claxx) {
        return _delegate.isCollection(claxx);
        
        //TODO: will need knowledge for .NET collections here
        // possibility: call registercollection with strings
    }

    public void registerCollection(Class clazz) {
        _delegate.registerCollection(clazz);
    }

    public void registerCollectionUpdateDepth(Class clazz, int depth) {
        _delegate.registerCollectionUpdateDepth(clazz, depth);
    }
    
    public void register(GenericClass clazz) {
    	String name = clazz.getName();
    	if(_classByName.get(name) == null){
    		_classByName.put(name, clazz);
    		_classes.add(clazz);
    	}
    }
    
	public ReflectClass[] knownClasses(){
		readAll();
        
        Collection4 classes = new Collection4();
		
		Iterator4 i = _classes.iterator();
		while(i.hasNext()){
            GenericClass clazz = (GenericClass)i.next();
            if(! _stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(clazz)){
                if(! clazz.isSecondClass()){
                	classes.add(clazz);
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
			_classByID.put(id, ret);
			return ret;
		}
		
		classreader.incrementOffset(YapConst.YAPINT_LENGTH); // skip empty unused int slot
        
		int ancestorid=classreader.readInt();
		
		ReflectClass nativeClass = _delegate.forName(classname);
		ret = new GenericClass(this, nativeClass,classname, ensureClassAvailability(ancestorid));
		
		// step 1 only add to _classByID, keep the class out of _classByName and _classes
        _classByID.put(id, ret);
		
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
		
		// Having the class in the _classByName Map for now indicates
		// that the class is fully read. This is breakable if we start
		// returning GenericClass'es in other methods like forName
		// even if a native class has not been found
		if(_classByName.get(classname) != null){
			return;
		}
		
        // step 2 add the class to _classByName and _classes to denote reading is completed
        _classByName.put(classname, clazz);
		_classes.add(clazz);
		
		// skip empty unused int slot, ancestor, index
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
    

	public void registerPrimitiveClass(int id, String name) {
        GenericClass existing = (GenericClass)_classByID.get(id);
		if( existing != null){
            existing.setSecondClass();
			return;
		}
		ReflectClass clazz = _delegate.forName(name);
        GenericClass claxx = ensureDelegate(clazz);
        claxx.setSecondClass();
        _classByID.put(id, claxx);
	}

    public void setParent(Reflector reflector) {
        // do nothing, the generic reflector does not have a parant
    }

}
