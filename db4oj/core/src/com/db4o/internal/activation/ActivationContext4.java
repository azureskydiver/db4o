/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.activation;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class ActivationContext4 {
    
    private final Transaction _transaction;
    
    private final Object _targetObject;
    
    private final ActivationDepth _depth;
    
    public ActivationContext4(Transaction transaction, Object obj, ActivationDepth depth){
        _transaction = transaction;
        _targetObject = obj;
        _depth = depth;
    }

    public void cascadeActivationToTarget(ClassMetadata classMetadata, boolean doDescend) {
        ActivationDepth depth = doDescend ? _depth.descend(classMetadata) : _depth; 
        cascadeActivation(classMetadata, targetObject(), depth);
    }
    
    public void cascadeActivationToChild(Object obj) {
        if(obj == null){
            return;
        }
        ClassMetadata classMetadata = container().classMetadataForObject(obj);
        if(classMetadata == null || classMetadata.isPrimitive()){
            return;
        }
        ActivationDepth depth = _depth.descend(classMetadata);
        cascadeActivation(classMetadata, obj, depth);
    }
    
    private void cascadeActivation(ClassMetadata classMetadata, Object obj, ActivationDepth depth) {
        if (! depth.requiresActivation()) {
            return;
        }
        if (depth.mode().isDeactivate()) {
            container().stillToDeactivate(_transaction, obj, depth, false);
        } else {
            // FIXME: [TA] do we need to check for isValueType here?
            if(classMetadata.isValueType()){
                classMetadata.activateFields(_transaction, obj, depth);
            }else{
                container().stillToActivate(_transaction, obj, depth);
            }
        }
    }

    public ObjectContainerBase container(){
        return _transaction.container();
    }

    public Object targetObject() {
        return _targetObject;
    }

}
