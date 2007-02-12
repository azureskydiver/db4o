/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.generic;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericReflector implements Reflector, DeepClone {
	
	private KnownClassesRepository _repository;

	/* default delegate Reflector is the JdkReflector */
	private Reflector _delegate;
    private GenericArrayReflector _array;
    
    
    private Collection4 _collectionPredicates = new Collection4();
    private Collection4 _collectionUpdateDepths = new Collection4();

	// todo: Why have this when there is already the _repository by name? Redundant
	private final Hashtable4 _classByClass = new Hashtable4();

	private Transaction _trans;
	private ObjectContainerBase _stream;
	
	public GenericReflector(Transaction trans, Reflector delegateReflector){
		_repository=new KnownClassesRepository(new GenericClassBuilder(this,delegateReflector));
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
	
	ObjectContainerBase getStream(){
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
		_repository.setTransaction(trans);
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
        GenericClass claxx = (GenericClass)_repository.lookupByName(clazz.getName());
        if(claxx == null){
            //  We don't have to worry about the superclass, it can be null
            //  because handling is delegated anyway
			claxx = genericClass(clazz);
			_repository.register(claxx);
        }
        return claxx;
    }

	private GenericClass genericClass(ReflectClass clazz) {
		GenericClass ret;
		String name = clazz.getName();
		if(name.equals(GenericArray.class.getName())){ // special case, comparing name because can't compare class == class directly with ReflectClass
			ret = new GenericArrayClass(this, clazz, name, null);
		} else {
			ret = new GenericClass(this, clazz, name, null);
		}
		return ret;
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
        ReflectClass clazz = _repository.lookupByName(className);
        if(clazz != null){
            return clazz;
        }
        clazz = _delegate.forName(className);
        if(clazz != null){
            return ensureDelegate(clazz);
        }
    	return _repository.forName(className);
    }

    public ReflectClass forObject(Object obj) {
        if (obj instanceof GenericObject){
			return forGenericObject((GenericObject)obj);
        }
        return _delegate.forObject(obj);
    }

	private ReflectClass forGenericObject(final GenericObject genericObject) {
		GenericClass claxx = genericObject._class;
		if(claxx == null){
			throw new IllegalStateException(); 
		}
		String name = claxx.getName();
		if(name == null){
			throw new IllegalStateException();
		}
		GenericClass existingClass = (GenericClass) forName(name);
		if(existingClass == null){
			_repository.register(claxx);
			return claxx;
		}
		// TODO: Using .equals() here would be more consistent with 
		//       the equals() method in GenericClass.
		if(existingClass != claxx){
			
			throw new IllegalStateException();
		}
		
		return claxx;
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
    	if(_repository.lookupByName(name) == null){
    		_repository.register(clazz);
    	}
    }
    
	public ReflectClass[] knownClasses() {
        Collection4 classes = new Collection4();
		collectKnownClasses(classes);
		return (ReflectClass[])classes.toArray(new ReflectClass[classes.size()]);
	}

	private void collectKnownClasses(Collection4 classes) {
		Iterator4 i = _repository.classes();
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
	}
	
	public void registerPrimitiveClass(int id, String name, GenericConverter converter) {
        GenericClass existing = (GenericClass)_repository.lookupByID(id);
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
	        register(claxx);
		    claxx.initFields(new GenericField[] {new GenericField(null, null, true, false, false)});
		    claxx.setConverter(converter);
		}
	    claxx.setSecondClass();
	    claxx.setPrimitive();
	    _repository.register(id,claxx);
	}

    public void setParent(Reflector reflector) {
        // do nothing, the generic reflector does not have a parant
    }

}
