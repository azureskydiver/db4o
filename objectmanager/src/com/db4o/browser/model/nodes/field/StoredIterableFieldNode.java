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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.browser.model.nodes.InstanceNode;
import com.db4o.ext.StoredField;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class IterableFieldNode.  Defines a FieldNode for objects that define
 * an iterator() or listIterator() method.
 * <p>
 * Note that this is useless if we cannot get the actual class object (ie:
 * if we are strictly using the meta-reflection framework).  In that case,
 * we will have to look for the internal structure of java.util.List
 * and register FieldNode classes with the FieldNodeFactory that can
 * handle various classes according to their internal structure.
 * 
 * @author djo
 */
public class StoredIterableFieldNode extends StoredFieldNode {

    /**
     * @param _instance
     * @param database TODO
     * @param fieldType
     * @return
     */
    public static IModelNode tryToCreate(StoredField field, Object _instance, Database database) {
        StoredIterableFieldNode result;
        
        Class fieldType = field.get(_instance).getClass();
        Method method = null;
        try {
            method = fieldType.getMethod("iterator", new Class[] {});
        } catch (Exception e) { return null; };
        
        try {
            result = new StoredIterableFieldNode(field, _instance, method, database);
            result.iterator();
        } catch (IllegalStateException e) {
            Logger.log().error(e, "Unable to invoke 'iterator()'");
            return null;
        }
        return result;
    }

	private Method _iteratorMethod;
	private Object _fieldValue;

	private Iterator iterator() {
        try {
            return (Iterator) _iteratorMethod.invoke(value, new Object[] {});
        } catch (Exception e) {
            Logger.log().error(e, "Unable to invoke 'iterator'");
            throw new IllegalStateException();
        }
    }
    
	/**
	 * @param field TODO
	 * @param field
	 * @param instance TODO
	 * @param database TODO
	 * @param iterator
	 */
	public StoredIterableFieldNode(StoredField field, Object instance, Method iteratorMethod, Database database) {
        super(field, instance, database);
        _iteratorMethod = iteratorMethod;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
		return iterator().hasNext();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
        LinkedList results = new LinkedList();
        Iterator i = iterator();
        while (i.hasNext()) {
            results.addLast(new InstanceNode(i.next(), _database));
        }
        IModelNode[] finalResults = new IModelNode[results.size()];
        int elementNum=0;
        for (i = results.iterator(); i.hasNext();) {
			IModelNode element = (IModelNode) i.next();
			finalResults[elementNum] = element;
            ++elementNum;
		}
		return finalResults;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
	public String getText() {
		return _field.getName() + ": " + value.getClass().getName();
	}

}
