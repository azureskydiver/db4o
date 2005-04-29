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

import com.db4o.browser.model.*;
import com.db4o.browser.model.nodes.*;
import com.db4o.browser.model.nodes.partition.PartitionFieldNodeFactory;
import com.db4o.reflect.*;

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
public class CollectionFieldNode extends FieldNode {

	public CollectionFieldNode(String fieldName, Object instance, IDatabase database) {
        super(fieldName, instance, database);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#mayHaveChildren()
	 */
	public boolean hasChildren() {
		ReflectClass clazz = _database.reflector().forObject(value);
		return clazz.toArray(value).length > 0;
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#children()
	 */
	public IModelNode[] children() {
		ReflectClass clazz = _database.reflector().forObject(value);
		Object[] contents = clazz.toArray(value);
		IModelNode[] results = new IModelNode[contents.length];
		
		for (int i = 0; i < results.length; i++) {
			ReflectClass itemClass = _database.reflector().forObject(contents[i]);
            results[i] = FieldNodeFactory.construct("", contents[i], _database);
		}
        return PartitionFieldNodeFactory.create(results,0,results.length,_database);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.gui.ITreeNode#getText()
	 */
    public String getText() {
        final String className = _database.reflector().forObject(value).getName() + " (id=" + _database.getId(value) + ")";
        return _fieldName.equals("") ? className : _fieldName + ": " + className;
    }

}
