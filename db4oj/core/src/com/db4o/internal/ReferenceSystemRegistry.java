/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class ReferenceSystemRegistry {
    
    private final Collection4 _referenceSystems = new Collection4();
    
    public void removeId(int id){
        Iterator4 i = _referenceSystems.iterator();
        while(i.moveNext()){
            ReferenceSystem referenceSystem = (ReferenceSystem) i.current();
            ObjectReference reference = referenceSystem.referenceForId(id);
            if(reference != null){
                referenceSystem.removeReference(reference);
            }
        }
    }
    
    public void removeObject(Object obj){
        Iterator4 i = _referenceSystems.iterator();
        while(i.moveNext()){
            ReferenceSystem referenceSystem = (ReferenceSystem) i.current();
            ObjectReference reference = referenceSystem.referenceForObject(obj);
            if(reference != null){
                referenceSystem.removeReference(reference);
            }
        }
    }
    
    public void removeReference(ObjectReference reference) {
        Iterator4 i = _referenceSystems.iterator();
        while(i.moveNext()){
            ReferenceSystem referenceSystem = (ReferenceSystem) i.current();
            referenceSystem.removeReference(reference);
        }
    }

    public void addReferenceSystem(ReferenceSystem referenceSystem) {
        _referenceSystems.add(referenceSystem);
    }

    public void removeReferenceSystem(ReferenceSystem referenceSystem) {
        _referenceSystems.remove(referenceSystem);
    }

}
