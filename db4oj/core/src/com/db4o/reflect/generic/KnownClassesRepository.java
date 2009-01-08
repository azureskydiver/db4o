/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class KnownClassesRepository {
	
	// FIXME very java-centric, what about .NET?
	private final static Hashtable4 PRIMITIVES;
	
	static {
		PRIMITIVES=new Hashtable4();
		registerPrimitive(Boolean.class,boolean.class);
		registerPrimitive(Byte.class,byte.class);
		registerPrimitive(Short.class,short.class);
		registerPrimitive(Character.class,char.class);
		registerPrimitive(Integer.class,int.class);
		registerPrimitive(Long.class,long.class);
		registerPrimitive(Float.class,float.class);
		registerPrimitive(Double.class,double.class);
	}

	private static void registerPrimitive(Class wrapper,Class primitive) {
		PRIMITIVES.put(wrapper.getName(),primitive);
	}
	
	private ObjectContainerBase _stream;
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
			_stream = trans.container();
		}
	}
    
    public void register(ReflectClass clazz) {
    	register(clazz.getName(), clazz);
    }

    public ReflectClass forID(int id) {
    	synchronized(_stream.lock()) {
	    	if(_stream.handlers().isSystemHandler(id)) {
	    		return _stream.handlers().classForID(id);
	    	}
			ensureClassAvailability(id);
	        return lookupByID(id);        
    	}
    }
    
    public ReflectClass forName(String className) {
        final ReflectClass clazz = lookupByName(className);
        if(clazz != null){
            return clazz;
        }
        
        if(_stream == null) {
        	return null;
        }
        
		
    	synchronized(_stream.lock()) {
	        if(_stream.classCollection() == null){
	        	return null;
	        }
	        
	        int classID = _stream.classMetadataIdForName(className);
	        if(classID <= 0){
	        	return null;
	        }
	        
	        return initializeClass(classID, className);
    	}
    }

	private ReflectClass initializeClass(int classID, String className) {
		ReflectClass newClazz = ensureClassInitialised(classID);
        _classByName.put(className, newClazz);
        return newClazz;
	}

	private void readAll(){
		forEachClassId(new Procedure4<Integer>() { public void apply(Integer id) {
			ensureClassAvailability(id);
		}});
		forEachClassId(new Procedure4<Integer>() { public void apply(Integer id) {
			ensureClassRead(id);
		}});
	}
	
	private void forEachClassId(Procedure4<Integer> procedure) {
		for(Iterator4 ids=_stream.classCollection().ids();ids.moveNext();) {
			procedure.apply((Integer)ids.current());
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
        
		ByteArrayBuffer classreader=_stream.readWriterByID(_trans,id);

		ClassMarshaller marshaller = marshallerFamily()._class;
		RawClassSpec spec=marshaller.readSpec(_trans, classreader);

		String className = spec.name();
		ret = lookupByName(className);
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
		
		ByteArrayBuffer classreader=_stream.readWriterByID(_trans,id);

		ClassMarshaller classMarshaller = marshallerFamily()._class;
		RawClassSpec classInfo=classMarshaller.readSpec(_trans, classreader);
		String className=classInfo.name();
		
		// Having the class in the _classByName Map for now indicates
		// that the class is fully read. This is breakable if we start
		// returning GenericClass'es in other methods like forName
		// even if a native class has not been found
		if(lookupByName(className) != null){
			return;
		}
		
        // step 2 add the class to _classByName and _classes to denote reading is completed
        register(className, clazz);
		
		int numFields=classInfo.numFields();
		ReflectField[] fields=_builder.fieldArray(numFields);
		FieldMarshaller fieldMarshaller=marshallerFamily()._field;
		
		for (int i = 0; i < numFields; i++) {
			
			RawFieldSpec fieldInfo=fieldMarshaller.readSpec(_stream, classreader);
			String fieldName=fieldInfo.name();
            ReflectClass fieldClass = reflectClassForFieldSpec(fieldInfo, _stream.reflector());
			fields[i]=_builder.createField(clazz, fieldName, fieldClass, fieldInfo.isVirtual(), fieldInfo.isPrimitive(), fieldInfo.isArray(), fieldInfo.isNArray());
		}
		_builder.initFields(clazz, fields);
	}

	private void register(String className, ReflectClass clazz) {
		if (lookupByName(className) != null)
			throw new IllegalArgumentException();
	    _classByName.put(className, clazz);
		_classes.add(clazz);
    }

	private ReflectClass reflectClassForFieldSpec(RawFieldSpec fieldInfo, Reflector reflector) {
		
		if (fieldInfo.isVirtual()) {
			 return virtualFieldByName(fieldInfo.name()).classReflector(reflector);
		}
		
		final int handlerID = fieldInfo.handlerID();
		
		// need to take care of special handlers here
		switch (handlerID){
		    case Handlers4.UNTYPED_ID:
		        return objectClass();
		    case Handlers4.ANY_ARRAY_ID:
		        return arrayClass(objectClass());
		    default:
		    	ReflectClass fieldClass=forID(handlerID);
		    	if (null != fieldClass) {
		    		return normalizeFieldClass(fieldInfo, fieldClass);
		    	}
		    	break;
		}
		return null;
	}

	private ReflectClass normalizeFieldClass(RawFieldSpec fieldInfo,
			ReflectClass fieldClass) {
		// TODO: why the following line is necessary?
		ReflectClass theClass=_stream.reflector().forName(fieldClass.getName());		    	
		if(fieldInfo.isPrimitive()) {
			theClass = primitiveClass(theClass);
		}
		if(fieldInfo.isArray()) {
			theClass = arrayClass(theClass);
		}
		return theClass;
	}

	private ReflectClass objectClass() {
		return _stream.reflector().forClass(Object.class);
	}

	private VirtualFieldMetadata virtualFieldByName(final String fieldName) {
		return _stream.handlers().virtualFieldByName(fieldName);
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
    
	private ReflectClass arrayClass(ReflectClass clazz) {
		Object proto=clazz.reflector().array().newInstance(clazz,0);
		return clazz.reflector().forObject(proto);
	}

	private ReflectClass primitiveClass(ReflectClass baseClass) {
		Class primitive=(Class) PRIMITIVES.get(baseClass.getName());
		if(primitive!=null) {
			return baseClass.reflector().forClass(primitive);
		}
		return baseClass;
	}
}
