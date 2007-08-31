/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.reflect.*;


/**
 * @exclude
 */
class ObjectAnalyzer {
    
    private final PartialObjectContainer _container;
    
    private final Object _obj;
    
    private ClassMetadata _classMetadata;
    
    private ObjectReference _ref;
    
    private boolean _notStorable;
    
    ObjectAnalyzer(PartialObjectContainer container, Object obj){
        _container = container;
        _obj = obj;
    }
    
    void analyze(Transaction trans){
        _ref = trans.referenceForObject(_obj);
        if (_ref == null) {
            ReflectClass claxx = _container.reflector().forObject(_obj);
            if(claxx == null){
                notStorable(_obj, claxx);
                return;
            }
            if(!detectClassMetadata(trans, claxx)){
                return;
            }
        } else {
            _classMetadata = _ref.classMetadata();
        }
        
        if (isPlainObjectOrPrimitive(_classMetadata) ) {
            notStorable(_obj, _classMetadata.classReflector());
        }
        
    }

    private boolean detectClassMetadata(Transaction trans, ReflectClass claxx) {
        _classMetadata = _container.getActiveClassMetadata(claxx);
        if (_classMetadata == null) {
            _classMetadata = _container.produceClassMetadata(claxx);
            if ( _classMetadata == null){
                notStorable(_obj, claxx);
                return false;
            }
            
            // The following may return a reference if the object is held
            // in a static variable somewhere ( often: Enums) that gets
            // stored or associated on initialization of the ClassMetadata.
            
            _ref = trans.referenceForObject(_obj);
        }
        return true;
    }

    private void notStorable(Object obj, ReflectClass claxx) {
        _container.notStorable(claxx, obj);
        _notStorable = true;
    }
    
    boolean notStorable(){
        return _notStorable;
    }
    
    private final boolean isPlainObjectOrPrimitive(ClassMetadata classMetadata) {
        return classMetadata.getID() == HandlerRegistry.ANY_ID  || classMetadata.isPrimitive();
    }

    ObjectReference objectReference() {
        return _ref;
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }

}
