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
import java.util.ArrayList;
import java.util.List;

import com.db4o.browser.model.nodes.field.FieldNodeFactory;


/**
 * Class InstanceNode.
 * 
 * @author djo
 */
public class InstanceNode implements IModelNode {
    
    private Object _instance;

	/**
	 * @param object
	 */
	public InstanceNode(Object instance) {
		_instance = instance;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		List results=new ArrayList();
		Class curclazz=_instance.getClass();
		while(curclazz!=null) {
			Field[] fields = curclazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				results.add(FieldNodeFactory.construct(fields[i], _instance));
			}
			curclazz=curclazz.getSuperclass();
		}
		return (IModelNode[])results.toArray(new IModelNode[results.size()]);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		return _instance.toString();
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
		return _instance.equals(((InstanceNode)obj)._instance);
	}
	
	public int hashCode() {
		return _instance.hashCode();
	}
}
