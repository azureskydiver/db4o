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

import java.lang.reflect.Field;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.*;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.browser.model.nodes.InstanceNode;


/**
 * Class FieldNode.  The patriarch of the FieldNode hierarchy.  Implements
 * common functionality.
 * 
 * @author djo
 */
public class FieldNode implements IModelNode {

    protected Field _field;
	protected Object value = null;
    protected Object _instance;
	protected IModelNode delegate;
	protected Database _database;
    
	/**
	 * @param field
	 * @param database TODO
	 * @param _instance
	 */
	public FieldNode(Field field, Object instance, Database database) {
		_field = field;
        _instance = instance;
		_database = database;

		if (!_field.isAccessible()) {
            _field.setAccessible(true);
        }
		try {
			value = _field.get(_instance);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get field value", e);
		}
		if(value==null) {
			delegate=NullNode.INSTANCE;
			return;
		}
		delegate = new InstanceNode(value, database);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
		return delegate.hasChildren();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		return delegate.children();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		return _field.getName() + ": " + delegate.getText();
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getName()
	 */
	public String getName() {
		return _field.getName();
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.IModelNode#getValueString()
	 */
	public String getValueString() {
		return (value==null ? "null" : value.toString());
	}

	public boolean equals(Object obj) {
		if(obj==this) {
			return true;
		}
		if(obj==null||getClass()!=obj.getClass()) {
			return false;
		}
		FieldNode node=(FieldNode)obj;
		return _instance.equals(node._instance)&&_field.equals(node._field);
	}
	
	public int hashCode() {
		return _instance.hashCode()*29+_field.hashCode();
	}

}
