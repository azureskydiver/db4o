/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes.field;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectArray;

/**
 * StoredArrayFieldNode.
 *
 * @author djo
 */
public class ArrayFieldNode extends FieldNode implements IModelNode {
    
    private int length;
    private ReflectArray arrayReflector;

	public ArrayFieldNode(String fieldName, Object instance, Database database) {
		super(fieldName, instance, database);

        arrayReflector = _database.reflector().array();
        length = arrayReflector.getLength(value);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#children()
	 */
	public IModelNode[] children() {
        
        IModelNode[] result = new IModelNode[length];
        
        for (int i=0; i < length; ++i) {
            Object item = arrayReflector.get(value, i);
            result[i] = FieldNodeFactory.construct("["+ i + "] ", item, _database);
        }
        
        return result;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#hasChildren()
	 */
	public boolean hasChildren() {
		return length > 0;
	}
	
	public String getText() {
        return _fieldName + " " + _database.reflector().forObject(value).getName();
	}

}
