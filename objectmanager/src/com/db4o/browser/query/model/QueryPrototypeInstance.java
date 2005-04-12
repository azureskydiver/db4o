/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class QueryPrototypeInstance {
    private final ReflectClass clazz;
    private QueryBuilderModel model;

    private Map fields = new HashMap();
    
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
                if (!curFields[i].isTransient()) {
                    fields.put(curFields[i].getName(), new FieldConstraint(curFields[i], model));
                }
            }
            curClazz = curClazz.getSuperclass();
        }
    }

    public void addUserConstraints(Query query) {
        query.constrain(clazz);
        for (Iterator fieldIter = fields.values().iterator(); fieldIter.hasNext();) {
            FieldConstraint constraint = (FieldConstraint) fieldIter.next();
            if (constraint.value != null) {
                constraint.apply(query);
            }
        }
    }
    
    public String[] getFieldNames() {
        return (String[]) fields.keySet().toArray(new String[fields.size()]);
    }
    
    public FieldConstraint getConstraint(String fieldName) {
        return (FieldConstraint) fields.get(fieldName);
    }
}
