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

import com.db4o.ObjectSet;
import com.db4o.browser.model.Database;
import com.db4o.ext.StoredClass;

/**
 * Class ClassNode.
 * 
 * @author djo
 */
public class ClassNode implements IModelNode {

	private final StoredClass _contents;
    private final Database _database;

	/**
	 * @param class1
	 */
	public ClassNode(StoredClass contents, Database database) {
		_contents = contents;
        _database = database;
    }

    public boolean mayHaveChildren() {
        return true;
    }
    
    /**
     * @return a list of nodes
     */
    public IModelNode[] children() {
        ObjectSet objects = _database.instances(_contents.getName());
        IModelNode[] result = new IModelNode[objects.size()];
        int i=0;
        while (objects.hasNext()) {
            Object object = objects.next();
            result[i] = new InstanceNode(object);
            ++i;
        }
        return result;
    }

	/**
	 * @return
	 */
	public String getText() {
		return _contents.getName();
	}
}
