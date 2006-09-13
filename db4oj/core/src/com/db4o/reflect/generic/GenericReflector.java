/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericReflector implements Reflector, DeepClone {
	
    private Reflector _delegate;
    private GenericArrayReflector _array;
    
    private final Hashtable4 _classByName = new Hashtable4();
    private final Hashtable4 _classByClass = new Hashtable4();
    private final Collection4 _classes = new Collection4();
    private final Hashtable4 _classByID = new Hashtable4();
    
    private Collection4 _collectionPredicates = new Collection4();
    private Collection4 _collectionUpdateDepths = new Collection4();
	private Collection4 _pendingClasses = new Collection4();
    
	private Transaction _trans;
	private YapStream _stream;
	
	public GenericReflector(Transaction trans, Reflector delegateReflector){
		setTransaction(trans);
		_delegate = delegateReflector;
        if(_delegate != null){
            _delegate.setParent(this);
        }
	}
	
	public Object deepClone(Object obj)  {
        GenericReflector myClone = new GenericReflector(null, (Reflector)_delegate.deepClone(this));
        myClone._collectionPredicates = (Collection4)_collectionPredicates.deepClone(myClone);
        myClone._collectionUpdateDepths = (Collection4)_collectionUpdateDepths.deepClone(myClone);
        
        
        // Interesting, adding the following messes things up.
        // Keep the code, since it may make sense to carry the
        // global reflectors into a running db4o session.
        
        
//        Iterator4 i = _classes.iterator();
//        while(i.hasNext()){
//            GenericClass clazz = (GenericClass)i.next();
//            clazz = (GenericClass)clazz.deepClone(myClone);
//            myClone._classByName.put(clazz.getName(), clazz);
//            myClone._classes.add(clazz);
//        }
        
		return myClone;
	}
	
	YapStream getStream(){
		return _stream;
	}

	public boolean hasTransaction(){
		return _trans != null;
	}
	
	public void setTransaction(Transaction trans){
		if(trans != null){
			_trans = trans;
			_stream = trans.stream();
		}
	}

    public ReflectArray array() {
        if(_array == null){
            _array = new GenericArrayReflector(this);
        }
        return _array;
    }

    public int collectionUpdateDepth(ReflectClass candidate) {
        Iterator4 i = _collectionUpdateDepths.iterator();
        while(i.moveNext()){
        	CollectionUpdateDepthEntry entry = (CollectionUpdateDepthEntry) i.current();
        	if (entry._predicate.match(candidate)) {
        		return entry._depth;
        	}
        }
        return 2;
        
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
        ReflectClass claxx = (ReflectClass) _classByClass.get(clazz);
        if(claxx != null){
            return claxx;
        }
        claxx = forName(clazz.getName());
        if(claxx != null){
            _classByClass.put(clazz, claxx);
            return claxx;
        }
        claxx = _delegate.forClass(clazz);
        if(claxx == null){
            return null;
        }
        claxx = ensureDelegate(claxx);
        _classByClass.put(clazz, claxx);
        return claxx;
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
        
        if(_stream == null) {
        	return null;
        }
        
        if(_stream.classCollection() != null){
            int classID = _stream.classCollection().getYapClassID(className);
            if(classID > 0){
                clazz = ensureClassInitialised(classID);
                _classByName.put(className, clazz);
                return clazz; 
            }
        }
        
        return null;
    }

    public ReflectClass forObject(Object obj) {
        if (obj instanceof GenericObject){
            return ((GenericObject)obj)._class;
        }
        return _delegate.forObject(obj);
    }
    
    public Reflector getDelegate(){
        return _delegate;
    }

    public boolean isCollection(ReflectClass candidate) {
        //candidate = candidate.getDelegate(); 
        Iterator4 i = _collectionPredicates.iterator();
        while(i.moveNext()){
            if (((ReflectClassPredicate)i.current()).match(candidate)) {
            	return true;
            }
        }
        return _delegate.isCollection(candidate.getDelegate());
        
        //TODO: will need knowledge for .NET collections here
        // possibility: call registercollection with strings
    }

    public void registerCollection(Class clazz) {
		registerCollection(classPredicate(clazz));
    }

	public void registerCollection(ReflectClassPredicate predicate) {
		_collectionPredicates.add(predicate);
	}

	private ReflectClassPredicate classPredicate(Class clazz) {
		final ReflectClass collectionClass = forClass(clazz);
		ReflectClassPredicate predicate = new ReflectClassPredicate() {
			public boolean match(ReflectClass candidate) {
	            return collectionClass.isAssignableFrom(candidate);
			}
		};
		return predicate;
	}

    public void registerCollectionUpdateDepth(Class clazz, int depth) {
		registerCollectionUpdateDepth(classPredicate(clazz), depth);
    }

	public void registerCollectionUpdateDepth(ReflectClassPredicate predicate, int depth) {
        _collectionUpdateDepths.add(new CollectionUpdateDepthEntry(predicate, depth));
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
		while(i.moveNext()){
            GenericClass clazz = (GenericClass)i.current();
            if(! _stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(clazz)){
                if(! clazz.isSecondClass()){
					if(! clazz.isArray()){
						classes.add(clazz);
					}
                }
            }
		}
        
        ReflectClass[] ret = new ReflectClass[classes.size()];
        int j = 0;
        i = classes.iterator();
        while(i.moveNext()){
            ret[j++] = (ReflectClass)i.current();
        }
        return ret;
	}
	
	private void readAll(){
		for(Iterator4 idIter=_stream.classCollection().ids();idIter.moveNext();) {
			ensureClassAvailability(((Integer)idIter.current()).intValue());
		}
		for(Iterator4 idIter=_stream.classCollection().ids();idIter.moveNext();) {
			ensureClassRead(((Integer)idIter.current()).intValue());
		}
	}
	
	private GenericClass ensureClassInitialised (int id) {
		GenericClass ret = ensureClassAvailability(id);
		while(_pendingClasses.size() > 0) {
			Collection4 pending = _pendingClasses;
			_pendingClasses = new Collection4();
			Iterator4 i = pending.iterator();
			while(i.moveNext()) {
				ensureClassRead(((Integer)i.current()).intValue());
			}
		}
		return ret;
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

		ClassMarshaller marshaller = MarshallerFamily.current()._class;
		RawClassSpec basicInfo=marshaller.readBasicInfo(_trans, classreader);

		String className = basicInfo.name();
		ret = (GenericClass)_classByName.get(className);
		if(ret != null){
			_classByID.put(id, ret);
			_pendingClasses.add(new Integer(id));
			return ret;
		}
		
		ReflectClass nativeClass = _delegate.forName(className);
		ret = new GenericClass(this, nativeClass,className, ensureClassAvailability(basicInfo.superClassID()));
		ret.setDeclaredFieldCount(basicInfo.numFields());
		
		// step 1 only add to _classByID, keep the class out of _classByName and _classes
        _classByID.put(id, ret);
		_pendingClasses.add(new Integer(id));
		
		return ret;
	}
	
	private void ensureClassRead(int id) {

		GenericClass clazz = (GenericClass)_classByID.get(id);
		
		YapWriter classreader=_stream.readWriterByID(_trans,id);

		ClassMarshaller classMarshaller = MarshallerFamily.current()._class;
		RawClassSpec classInfo=classMarshaller.readBasicInfo(_trans, classreader);
		String className=classInfo.name();
		
		// Having the class in the _classByName Map for now indicates
		// that the class is fully read. This is breakable if we start
		// returning GenericClass'es in other methods like forName
		// even if a native class has not been found
		if(_classByName.get(className) != null){
			return;
		}
		
        // step 2 add the class to _classByName and _classes to denote reading is completed
        _classByName.put(className, clazz);
		_classes.add(clazz);
		
		int numFields=classInfo.numFields();
		GenericField[] fields=new GenericField[numFields];
		FieldMarshaller fieldMarshaller=MarshallerFamily.current()._field;
		
		for (int i = 0; i < numFields; i++) {
			
			RawFieldSpec fieldInfo=fieldMarshaller.readBasicInfo(_stream, classreader);
			String fieldName=fieldInfo.name();
            int handlerID=fieldInfo.handlerID();
			
            if (fieldInfo.isVirtual()) {
                fields[i] = new GenericVirtualField(fieldName);
                continue;
            }   
            GenericClass fieldClass = null;

            // need to take care of special handlers here
            switch (handlerID){
                case YapHandlers.ANY_ID:
                    fieldClass = (GenericClass)forClass(Object.class);
                    break;
                case YapHandlers.ANY_ARRAY_ID:
                    fieldClass = ((GenericClass)forClass(Object.class)).arrayClass();
                    break;
                default:
					ensureClassAvailability(handlerID);
                    fieldClass = (GenericClass)_classByID.get(handlerID);        
            }
		    		    
			fields[i]=new GenericField(fieldName,fieldClass, fieldInfo.isPrimitive(), fieldInfo.isArray(), fieldInfo.isNArray());
		}
		
        clazz.initFields(fields);
	}
    

	public void registerPrimitiveClass(int id, String name, GenericConverter converter) {
        GenericClass existing = (GenericClass)_classByID.get(id);
		if (existing != null) {
			if (null != converter) {
				existing.setSecondClass();
			} else {
				existing.setConverter(null);
			}
			return;
		}
		ReflectClass clazz = _delegate.forName(name);
		
		GenericClass claxx = null;
		if(clazz != null) {
	        claxx = ensureDelegate(clazz);
		}else {
	        claxx = new GenericClass(this, null, name, null);
	        _classByName.put(name, claxx);
		    claxx.initFields(new GenericField[] {new GenericField(null, null, true, false, false)});
		    claxx.setConverter(converter);
	        _classes.add(claxx);
		}
	    claxx.setSecondClass();
	    claxx.setPrimitive();
	    _classByID.put(id, claxx);
	}

    public void setParent(Reflector reflector) {
        // do nothing, the generic reflector does not have a parant
    }

}
