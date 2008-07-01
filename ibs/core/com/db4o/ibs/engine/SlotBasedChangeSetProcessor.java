/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.ibs.engine;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.ibs.*;
import com.db4o.internal.*;


public class SlotBasedChangeSetProcessor implements ChangeSetProcessor{
    
    private final ExtObjectContainer _container;
    
    public SlotBasedChangeSetProcessor(ObjectContainer container) {
        _container = container.ext();
    }

    public void apply(ChangeSet changes) {
        SlotBasedChangeSet slotBasedChangeSet = (SlotBasedChangeSet)changes;
        for(SlotBasedChange change : slotBasedChangeSet.changes()){
            apply(change);
        }
    }
    
    private void apply(SlotBasedChange change){
        if(change instanceof NewObjectChange){
            apply((NewObjectChange)change);
        } else if(change instanceof UpdateChange){
            apply((UpdateChange)change);
        } else if(change instanceof DeleteChange){
            apply((DeleteChange)change);
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public void apply(UpdateChange change){
        Object obj = activatedObjectByUUID(change);
        for(FieldChange fieldChange : change.fieldChanges()){
            mappedFieldMetadata(fieldChange.field()).set(obj, fieldChange.currentValue());
        }
        _container.store(obj);
    }

    private Object activatedObjectByUUID(SlotBasedChange change) {
        Object obj = _container.getByUUID(change.uuid());
        if(obj == null){
            
            // FIXME: What do we do here? 
            // For a backup this is a disaster case.
            // For replication this may well be a possible case to do nothing.
            // Best solution: Have a listener that can be notified.
            throw new NotImplementedException();
            
        }
        if(! _container.isActive(obj)){
            _container.activate(obj);
        }
        return obj;
    }
    
    public void apply(DeleteChange change){
        Object obj = activatedObjectByUUID(change);
        _container.delete(obj);
    }
    
    public void apply(NewObjectChange change){
        _container.store(change.object());
    }
    
    private FieldMetadata mappedFieldMetadata(FieldMetadata fieldMetadata){
        StoredClass storedClass = _container.storedClass(fieldMetadata.containingClass().getName());
        StoredField storedField = storedClass.storedField(fieldMetadata.getName(), null);
        return ((StoredFieldImpl)storedField).fieldMetadata();
    }
    
}
