/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.model.nodes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.field.FieldNodeFactory;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;


/**
 * Class InstanceNode.
 * 
 * @author djo
 */
public class StoredInstanceNode implements IModelNode {
	private StoredClass _clazz;
	// TODO: refactor to use id and instantiate on demand
    private Object _instance;
	private Database _database;

	/**
	 * @param database TODO
	 * @param object
	 */
	public StoredInstanceNode(Object instance, StoredClass clazz,Database database) {
		_instance = instance;
		_clazz=clazz;
		_database = database;
		database.activate(instance);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		StoredField[] fields=_clazz.getStoredFields();
		IModelNode[] children=new IModelNode[fields.length];
		for (int idx = 0; idx < fields.length; idx++) {
			children[idx]=FieldNodeFactory.construct(fields[idx],_instance,_database);
		}
		return children;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		long id = _database.getId(_instance);
		if (id > 0) {
			return _instance.toString() + " (" + _database.getId(_instance) + ")";
		} else {
			return _instance.toString();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getValueString()
	 */
	public String getValueString() {
		return _instance.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getName()
	 */
	public String getName() {
		// This is only called if this is a top-level query result or an item in a container
		return "";
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
		return _instance.getClass().getDeclaredFields().length > 0;
	}
	
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		return _instance.equals(((StoredInstanceNode)obj)._instance);
	}
	
	public int hashCode() {
		return _instance.hashCode();
	}
}
