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

import com.db4o.browser.model.IDatabase;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;


/** (non-api)
 * Class InstanceNode.
 * 
 * @author djo
 */
class InstanceNode implements IModelNode {
	private ReflectClass _clazz=null;
    private Object _instance=null;
	private IDatabase _database;

	public InstanceNode(Object instance, IDatabase database) {
        if (instance == null || database == null) {
            throw new IllegalArgumentException("InstanceNode: Null constructor argument");
        }
		_instance = instance;
		_clazz=database.reflector().forObject(instance);
		_database = database;
		database.activate(instance);
	}
    
    public InstanceNode(long id, IDatabase database) {
        this(database.byId(id),database);
    }

    public IModelNode[] children() {
		List results = new ArrayList();
		ReflectClass curclazz = _clazz;
		while (curclazz != null) {
			ReflectField[] fields = curclazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (!fields[i].isTransient()) {
                    Object field = FieldNode.field(fields[i], _instance);
                    results.add(FieldNodeFactory.construct(fields[i].getName(), field, _database));
				}
			}
			curclazz = curclazz.getSuperclass();
		}
		return (IModelNode[]) results.toArray(new IModelNode[results.size()]);
	}

    
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		long id = _database.getId(_instance);
		if (id > 0) {
			return _instance.toString() + " (id=" + _database.getId(_instance) + ")";
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
        return _database.reflector().forObject(_instance).getDeclaredFields().length > 0;
	}
	
	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		return _instance.equals(((InstanceNode)obj)._instance);
	}
	
	public int hashCode() {
		return _instance.hashCode();
	}
}
