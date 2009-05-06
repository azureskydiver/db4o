/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class ReferenceSystemRegistry {
    
    private final Collection4 _referenceSystems = new Collection4();
    
    public void removeId(final int id){
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return referenceSystem.referenceForId(id);
			}
    	});
    }

    public void removeObject(final Object obj){
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return referenceSystem.referenceForObject(obj);
			}
    	});
    }
    
    public void removeReference(final ObjectReference reference) {
    	removeReference(new ReferenceSource() {
			public ObjectReference referenceFrom(ReferenceSystem referenceSystem) {
				return reference;
			}
    	});
    }

    private void removeReference(ReferenceSource referenceSource) {
        Iterator4 i = _referenceSystems.iterator();
        while(i.moveNext()){
            ReferenceSystem referenceSystem = (ReferenceSystem) i.current();
            ObjectReference reference = referenceSource.referenceFrom(referenceSystem);
            if(reference != null){
                referenceSystem.removeReference(reference);
            }
        }
    }

    public void addReferenceSystem(ReferenceSystem referenceSystem) {
        _referenceSystems.add(referenceSystem);
    }

    public void removeReferenceSystem(ReferenceSystem referenceSystem) {
        _referenceSystems.remove(referenceSystem);
    }

    private static interface ReferenceSource {
    	ObjectReference referenceFrom(ReferenceSystem referenceSystem);
    }
}
