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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.ReflectMethod;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class MapFieldNode.
 * 
 * @author djo
 */
public class MapFieldNode extends FieldNode {

    public static IModelNode tryToCreate(ReflectField field, Object instance, Database database) {
        MapFieldNode result;
        
        Object fieldContents = FieldNode.field(field, instance);
        
        // See if we can get ReflectMethods corresponding to keySet() and get()
        ReflectClass fieldType = database.reflector().forObject(fieldContents);
        ReflectMethod keySet = null;
		ReflectMethod get = null;
        ReflectClass object = database.reflector().forName("java.lang.Object");
        keySet = fieldType.getMethod("keySet", new ReflectClass[] {});
        get = fieldType.getMethod("get", new ReflectClass[] {object});
        
        if (keySet == null || get == null) {
            return null;
        }
        
        try {
            result = new MapFieldNode(field, instance, keySet, get, database);
            result.iterator();
        } catch (IllegalStateException e) {
            Logger.log().error(e, "Unable to invoke 'iterator()'");
            return null;
        }
        return result;
    }

	private ReflectMethod _keySetMethod;
	private ReflectMethod _getMethod;

	private Iterator iterator() {
        Set set;
        try {
            set = (Set) _keySetMethod.invoke(value, new Object[] {});
        } catch (Exception e) {
            Logger.log().error(e, "Unable to invoke 'keySet'");
            throw new IllegalStateException();
        }
        return set.iterator();
    }
	
	private Object get(Object key) {
		Object result;
		try {
			result = _getMethod.invoke(value, new Object[] {key});
		} catch (Exception e) {
			Logger.log().error(e, "Unable ot invoke 'get'");
			throw new IllegalStateException();
		}
		return result;
	}

    public MapFieldNode(ReflectField field, Object instance, ReflectMethod keySetMethod, ReflectMethod getMethod, Database database) {
        super(field, instance, database);
        _keySetMethod = keySetMethod;
		_getMethod = getMethod;
	}
    
	public boolean hasChildren() {
		return iterator().hasNext();
	}
	
	public IModelNode[] children() {
        LinkedList results = new LinkedList();
        Iterator i = iterator();
        while (i.hasNext()) {
			Object key = i.next();
			results.addLast(new MapItemNode(key, get(key), _database));
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
        return _field.getName() + ": " + _database.reflector().forObject(value).getName();
	}

}
