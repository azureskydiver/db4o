/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class QueryPrototypeInstance {
    private final ReflectClass clazz;
    private QueryBuilderModel model;

    private Map fields = new TreeMap(new FieldComparator());
    
    public QueryPrototypeInstance(ReflectClass clazz, QueryBuilderModel model) {
        this.clazz = clazz;
        this.model = model;
        populateFields();
    }

    private void populateFields() {
        ReflectClass curClazz = clazz;
        while (curClazz != null) {
            ReflectField[] curFields = curClazz.getDeclaredFields();
            for (int i = 0; i < curFields.length; i++) {
                ReflectClass fieldClass = curFields[i].getFieldType();
                
                if (curFields[i].isTransient() || fieldClass.isCollection() || fieldClass.isArray()) {
                    continue;
                }
                
                fields.put(curFields[i], new FieldConstraint(curFields[i], model));
            }
            curClazz = curClazz.getSuperclass();
        }
    }

    public void addUserConstraints(Query query) {
        if (query == null || clazz == null) {
            return;
        }
        query.constrain(clazz);
        for (Iterator fieldIter = fields.values().iterator(); fieldIter.hasNext();) {
            FieldConstraint constraint = (FieldConstraint) fieldIter.next();
            if (constraint.value != null) {
                constraint.apply(query);
            }
        }
    }
    
    public ReflectField[] getFields() {
        return (ReflectField[]) fields.keySet().toArray(new ReflectField[fields.size()]);
    }
    
    public FieldConstraint getConstraint(ReflectField field) {
        return (FieldConstraint) fields.get(field);
    }

    public ReflectClass getType() {
        return clazz;
    }
}
