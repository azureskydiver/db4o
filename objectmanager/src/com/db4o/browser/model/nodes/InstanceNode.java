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
        Field[] fields = _instance.getClass().getDeclaredFields();
        IModelNode[] results = new IModelNode[fields.length];
        for (int i = 0; i < fields.length; i++) {
            results[i] = FieldNodeFactory.construct(fields[i], _instance);
		}
		return results;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		return _instance.toString();
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
        // FIXME: Maybe we need to do reflection here and return an accurate result...
		return true;
	}
}
