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
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.browser.model.nodes.InstanceNode;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class MapFieldNode.
 * 
 * @author djo
 */
public class MapFieldNode extends FieldNode {

    /**
     * @param _instance
     * @param database TODO
     * @param fieldType
     * @return
     */
    public static IModelNode tryToCreate(Field field, Object _instance, Database database) {
        MapFieldNode result;
        
        Class fieldType = field.getType();
        Method m = null;
        try {
            m = fieldType.getMethod("keySet", new Class[] {});
        } catch (Exception e) { return null; };
        
        try {
            result = new MapFieldNode(field, _instance, m, database);
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
    

	public MapFieldNode(Field field, Object instance, Method keySetMethod, Database database) {
        super(field, instance, database);
        _keySetMethod = keySetMethod;
	}
    
	public boolean hasChildren() {
		return iterator().hasNext();
	}

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

	public String getText() {
		return _field.getName() + ": " + _field.getType();
	}

}
