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

import java.util.LinkedList;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.browser.gui.standalone.Model;
import com.db4o.ext.StoredClass;

/**
 * Class ClassNode.
 * 
 * @author djo
 */
public class ClassNode implements ITreeNode {

	private final StoredClass _contents;
    private final Model _model;

	/**
	 * @param class1
	 */
	public ClassNode(StoredClass contents, Model model) {
		_contents = contents;
        _model = model;
    }

    public boolean mayHaveChildren() {
        return true;
    }
    
    /**
     * @return a list of nodes
     */
    public List children() {
        LinkedList result = new LinkedList();
        ObjectSet objects = _model.instances(_contents.getName());
        while (objects.hasNext()) {
            Object object = objects.next();
            result.add(new InstanceNode(object));
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
