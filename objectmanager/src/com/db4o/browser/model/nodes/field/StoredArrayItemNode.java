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
package com.db4o.browser.model.nodes.field;

import java.util.ArrayList;
import java.util.List;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;


/**
 * Class InstanceNode.
 * 
 * @author djo
 */
public class StoredArrayItemNode implements IModelNode {
    
	private int _key;
    private Object _instance;
	private Database _database;

	public StoredArrayItemNode(int key, Object instance, Database database) {
		_key = key;
		_instance = instance;
		_database = database;
		database.activate(instance);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		List results=new ArrayList();
		ReflectClass curclazz= _database.reflector().forObject(_instance);
		while(curclazz!=null) {
			ReflectField[] fields = curclazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if(!fields[i].isTransient()) {
					results.add(FieldNodeFactory.construct(fields[i], _instance, _database));
				}
			}
			curclazz=curclazz.getSuperclass();
		}
		return (IModelNode[])results.toArray(new IModelNode[results.size()]);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		long id = _database.getId(_instance);
		if (id > 0) {
			return getName() + _instance.toString() + " (" + _database.getId(_instance) + ")";
		} else {
			return getName() +_instance.toString();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getValueString()
	 */
	public String getValueString() {
		long id = _database.getId(_instance);
		if (id > 0) {
			return getName() + _instance.toString() + " (" + _database.getId(_instance) + ")";
		} else {
			return getName() + _instance.toString();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getName()
	 */
	public String getName() {
		return "[" + _key + "] = ";
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
        return _database.reflector().forObject(_instance).getDeclaredFields().length > 0;
	}
	
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		return _instance.equals(((StoredArrayItemNode)obj)._instance);
	}
	
	public int hashCode() {
		return _instance.hashCode();
	}
}
