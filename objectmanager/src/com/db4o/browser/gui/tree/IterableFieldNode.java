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
package com.db4o.browser.gui.tree;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class IterableFieldNode.
 * 
 * @author djo
 */
public class IterableFieldNode implements ITreeNode {

    /**
     * @param fieldType
     * @param _instance
     * @return
     */
    public static ITreeNode tryToCreate(Field field, Object _instance) {
        IterableFieldNode result;
        
        Class fieldType = field.getType();
        Method m = null;
        try {
            m = fieldType.getMethod("iterator", new Class[] {});
        } catch (Exception e) { return null; };
        
        try {
            result = new IterableFieldNode(field, _instance, m);
            result.iterator();
        } catch (IllegalStateException e) {
            Logger.log().error(e, "Unable to invoke 'iterator()'");
            return null;
        }
        return result;
    }

    private Object _instance;
	private Method _iteratorMethod;
	private Field _field;

	private Iterator iterator() {
        try {
            return (Iterator) _iteratorMethod.invoke(field(), new Object[] {});
        } catch (Exception e) {
            Logger.log().error(e, "Unable to invoke 'iterator'");
            throw new IllegalStateException();
        }
    }
    
    private Object field() {
        try {
            if (!_field.isAccessible()) 
                _field.setAccessible(true);
            
			return _field.get(_instance);
		} catch (Exception e) {
            Logger.log().error(e, "Unable to get the field contents");
            throw new IllegalStateException();
		}
    }
    
	/**
	 * @param field TODO
	 * @param instance TODO
	 * @param field
	 * @param iterator
	 */
	public IterableFieldNode(Field field, Object instance, Method iteratorMethod) {
        _field = field;
        _instance = instance;
        _iteratorMethod = iteratorMethod;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return iterator().hasNext();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public List children() {
        LinkedList results = new LinkedList();
        Iterator i = iterator();
        while (i.hasNext()) {
            results.addLast(new InstanceNode(i.next()));
        }
		return results;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		return _field.getName() + ": " + _field.getType();
	}

}
