/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes.field;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectArray;
import com.db4o.reflect.ReflectField;

/**
 * StoredArrayFieldNode.
 *
 * @author djo
 */
public class ArrayFieldNode extends FieldNode implements IModelNode {
    
    private Object array;
    private int length;
    private ReflectArray arrayReflector;

	public ArrayFieldNode(ReflectField field, Object instance, Database database) {
		super(field, instance, database);
        array = FieldNode.field(_field, _instance);
        arrayReflector = _database.reflector().array();
        length = arrayReflector.getLength(array);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#children()
	 */
	public IModelNode[] children() {
        
        IModelNode[] result = new IModelNode[length];
        
        for (int i=0; i < length; ++i) {
            Object item = arrayReflector.get(array, i);
            result[i] = new ArrayItemNode(i, item, _database);
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
        return _field.getName() + ": " + _database.reflector().forObject(value).getName();
	}

}
