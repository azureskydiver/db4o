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

import com.db4o.browser.gui.tree.fieldNode.FieldNode;


/**
 * Class PrimitiveFieldNode. Represents primitive types like Integer, int,
 * Float, float, etc.
 * 
 * @author djo
 */
public class PrimitiveFieldNode extends FieldNode {

	/**
	 * @param field
	 * @param instance
	 */
	public PrimitiveFieldNode(Field field, Object instance) {
		super(field, instance);
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.gui.FieldNode#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return false;
	}
}
