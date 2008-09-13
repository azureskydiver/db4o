/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;


/**
 * @exclude
 */
public class StoredClassImpl implements StoredClass {
    
    private final Transaction _transaction;
    
    private final ClassMetadata _classMetadata;
    
    public StoredClassImpl(Transaction transaction, ClassMetadata classMetadata){
        if(classMetadata == null){
            throw new IllegalArgumentException();
        }
        _transaction = transaction;
        _classMetadata = classMetadata;
    }

    public long[] getIDs() {
        return _classMetadata.getIDs(_transaction);
    }

    public String getName() {
        return _classMetadata.getName();
    }

    public StoredClass getParentStoredClass() {
        ClassMetadata parentClassMetadata = _classMetadata.getAncestor();
        if(parentClassMetadata == null){
            return null;
        }
        return new StoredClassImpl(_transaction, parentClassMetadata);
    }

    public StoredField[] getStoredFields() {
        StoredField[] fieldMetadata = _classMetadata.getStoredFields();
        StoredField[] storedFields = new StoredField[fieldMetadata.length];
        for (int i = 0; i < fieldMetadata.length; i++) {
            storedFields[i] = new StoredFieldImpl(_transaction, (FieldMetadata)fieldMetadata[i]);
        }
        return storedFields;
    }
    
    public boolean hasClassIndex() {
        return _classMetadata.hasClassIndex();
    }

    // TODO: Write test case.
    public void rename(String newName) {
        _classMetadata.rename(newName);
    }

    public StoredField storedField(String name, Object type) {
        FieldMetadata fieldMetadata = (FieldMetadata) _classMetadata.storedField(name, type);
        if(fieldMetadata == null){
            return null;
        }
        return new StoredFieldImpl(_transaction, fieldMetadata);
    }
    
    public int hashCode() {
        return _classMetadata.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return _classMetadata.equals(((StoredClassImpl) obj)._classMetadata);
    }

	public int instanceCount() {
		return _classMetadata.instanceCount(_transaction);
	}

}
