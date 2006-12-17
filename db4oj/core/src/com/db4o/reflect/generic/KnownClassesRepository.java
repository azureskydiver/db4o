/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;
import com.db4o.reflect.*;

public class KnownClassesRepository {
	
	private YapStream _stream;
	private Transaction _trans;

    private ReflectClassBuilder _builder;

    private final Hashtable4 _classByName = new Hashtable4();
    private final Hashtable4 _classByID = new Hashtable4();
	private Collection4 _pendingClasses = new Collection4();
    private final Collection4 _classes = new Collection4();

    public KnownClassesRepository(ReflectClassBuilder builder) {
    	_builder=builder;
    }
    
	public void setTransaction(Transaction trans){
		if(trans != null){
			_trans = trans;
			_stream = trans.stream();
		}
	}
    
    public void register(ReflectClass clazz) {
    	_classByName.put(clazz.getName(), clazz);
		_classes.add(clazz);
    }

    public ReflectClass forID(int id) {
		ensureClassAvailability(id);
        return lookupByID(id);        
    }
    
    public ReflectClass forName(String className) {
        ReflectClass clazz = (ReflectClass)_classByName.get(className);
        if(clazz != null){
            return clazz;
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

	private void readAll(){
		for(Iterator4 idIter=_stream.classCollection().ids();idIter.moveNext();) {
			ensureClassAvailability(((Integer)idIter.current()).intValue());
		}
		for(Iterator4 idIter=_stream.classCollection().ids();idIter.moveNext();) {
			ensureClassRead(((Integer)idIter.current()).intValue());
		}
	}

	private ReflectClass ensureClassAvailability (int id) {

        if(id == 0){
            return null;
        }
		
        ReflectClass ret = (ReflectClass)_classByID.get(id);
		if(ret != null){
			return ret;
		}
        
		YapReader classreader=_stream.readWriterByID(_trans,id);

		ClassMarshaller marshaller = marshallerFamily()._class;
		RawClassSpec spec=marshaller.readSpec(_trans, classreader);

		String className = spec.name();
		ret = (ReflectClass)_classByName.get(className);
		if(ret != null){
			_classByID.put(id, ret);
			_pendingClasses.add(new Integer(id));
			return ret;
		}
		
		ret = _builder.createClass(className, ensureClassAvailability(spec.superClassID()),spec.numFields());
		
		// step 1 only add to _classByID, keep the class out of _classByName and _classes
        _classByID.put(id, ret);
		_pendingClasses.add(new Integer(id));
		
		return ret;
	}

	private void ensureClassRead(int id) {

		ReflectClass clazz = lookupByID(id);
		
		YapReader classreader=_stream.readWriterByID(_trans,id);

		ClassMarshaller classMarshaller = marshallerFamily()._class;
		RawClassSpec classInfo=classMarshaller.readSpec(_trans, classreader);
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
		ReflectField[] fields=_builder.fieldArray(numFields);
		FieldMarshaller fieldMarshaller=marshallerFamily()._field;
		
		for (int i = 0; i < numFields; i++) {
			
			RawFieldSpec fieldInfo=fieldMarshaller.readSpec(_stream, classreader);
			String fieldName=fieldInfo.name();
            int handlerID=fieldInfo.handlerID();
            ReflectClass fieldClass = null;

            // need to take care of special handlers here
            switch (handlerID){
                case YapHandlers.ANY_ID:
                    fieldClass = _stream.reflector().forName(Object.class.getName());
                    break;
                case YapHandlers.ANY_ARRAY_ID:
                    fieldClass = _builder.arrayClass(_stream.reflector().forName(Object.class.getName()));
                    break;
                default:
                	fieldClass=forID(handlerID);
                	fieldClass=_stream.reflector().forName(fieldClass.getName());
            }
    	    		    
			fields[i]=_builder.createField(fieldName, fieldClass, fieldInfo.isVirtual(), fieldInfo.isPrimitive(), fieldInfo.isArray(), fieldInfo.isNArray());
		}
		_builder.initFields(clazz, fields);
	}

	private MarshallerFamily marshallerFamily() {
		return MarshallerFamily.forConverterVersion(_stream.converterVersion());
	}

	private ReflectClass ensureClassInitialised (int id) {
		ReflectClass ret = ensureClassAvailability(id);
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
	
    public Iterator4 classes() {
    	readAll();
    	return _classes.iterator();
    }

    public void register(int id,ReflectClass clazz) {
    	_classByID.put(id, clazz);
    }
    
    public ReflectClass lookupByID(int id) {
    	return (ReflectClass)_classByID.get(id);
    }

    public ReflectClass lookupByName(String name) {
    	return (ReflectClass)_classByName.get(name);
    }
}
