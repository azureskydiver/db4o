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
package com.db4o.browser.gui;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Class FieldNode.
 * 
 * @author djo
 */
public class FieldNode implements ITreeNode {

    private Field _field;
    private Object _instance;
    
	/**
	 * @param field
	 * @param _instance
	 */
	public FieldNode(Field field, Object instance) {
		_field = field;
        _instance = instance;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public List children() {
		return new LinkedList();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
        if (!_field.isAccessible()) {
            _field.setAccessible(true);
        }
		try {
			return _field.getName() + ": " + _field.get(_instance);
		} catch (Exception e) {
            // FIXME: Log the error here...
		}
        return _field.getName() + ": (unable to get value)";
	}

}
