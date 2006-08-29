package com.db4o.objectmanager.api.impl;

import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.reflect.ReflectClass;
import com.db4o.query.Query;
import com.db4o.objectmanager.api.DatabaseInspector;
import com.db4o.objectmanager.api.helpers.ReflectHelper;

import java.util.List;

/**
 * User: treeder
 * Date: Aug 9, 2006
 * Time: 11:49:54 AM
 */
public class DatabaseInspectorImpl implements DatabaseInspector {
    private ObjectContainer oc;

    public DatabaseInspectorImpl(ObjectContainer oc) {
        this.oc = oc;
    }

    public int getNumberOfClasses() {
        return getClassesStored().size();
    }

    public List getClassesStored() {
        return ReflectHelper.getUserStoredClasses(oc);
    }

    public int getNumberOfObjectsForClass(Class aClass) {
        List results = oc.query(aClass);
        return results.size();
    }

    public int getNumberOfObjectsForClass(String aClass) {
        ReflectClass reflectClass = oc.ext().reflector().forName(aClass);
        Query q = oc.query();
        q.constrain(reflectClass);
        List results = q.execute();
        return results.size();
    }

    public long getSpaceUsed() {
        return 0;
    }

    public long getSpaceUsedByIndexes() {
        return 0;
    }

    public long getSpaceUsedByClassMetaData() {
        return 0;
    }

    public long getSpaceUsedByStoredObjects() {
        return 0;
    }

    public long getSpaceFree() {
        return 0;
    }

    public long getSpaceUnallocated() {
        return 0;
    }

    public int getNumberOfIndexes() {
        int counter = 0;
        List classes = getClassesStored();
        for (int i = 0; i < classes.size(); i++) {
            StoredClass storedClass = (StoredClass) classes.get(i);
            StoredField[] storedFields = storedClass.getStoredFields();
            for (int j = 0; j < storedFields.length; j++) {
                StoredField storedField = storedFields[j];
                if(storedField.hasIndex()){
                    counter++;
                }
            }
        }
        return counter;
    }

    public List getIndexStats() {
        // Waiting for YapField.getIndex() to have an Index interface so it doesn't return BTree class
        return null;
    }

    public List getReplicationRecords() {
        return null;
    }

    public long getSize() {
        return 0;
    }
}
