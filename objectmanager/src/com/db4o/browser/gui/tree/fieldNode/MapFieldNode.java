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
package com.db4o.browser.gui.tree.fieldNode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.db4o.browser.gui.tree.ITreeNode;
import com.db4o.browser.gui.tree.InstanceNode;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class IterableFieldNode.
 * 
 * @author djo
 */
public class MapFieldNode extends FieldNode {

    /**
     * @param fieldType
     * @param _instance
     * @return
     */
    public static ITreeNode tryToCreate(Field field, Object _instance) {
        MapFieldNode result;
        
        Class fieldType = field.getType();
        Method m = null;
        try {
            m = fieldType.getMethod("keySet", new Class[] {});
        } catch (Exception e) { return null; };
        
        try {
            result = new MapFieldNode(field, _instance, m);
            result.iterator();
        } catch (IllegalStateException e) {
            Logger.log().error(e, "Unable to invoke 'iterator()'");
            return null;
        }
        return result;
    }

	private Method _keySetMethod;

	private Iterator iterator() {
        Set set;
        try {
            set = (Set) _keySetMethod.invoke(field(), new Object[] {});
        } catch (Exception e) {
            Logger.log().error(e, "Unable to invoke 'keySet'");
            throw new IllegalStateException();
        }
        return set.iterator();
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
    

	public MapFieldNode(Field field, Object instance, Method keySetMethod) {
        super(field, instance);
        _keySetMethod = keySetMethod;
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
