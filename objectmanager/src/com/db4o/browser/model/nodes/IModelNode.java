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


/**
 * Class ITreeNode. An interface for tree nodes in the containership tree.
 * 
 * @author djo
 */
public interface IModelNode {
	public boolean mayHaveChildren();

	/**
	 * @return an array of child nodes (no generics now please)
	 */
	public IModelNode[] children();

	/**
	 * @return the text string to display
	 */
	public String getText();
	
	/**
	 * Return the field name
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the field value as a string
	 * 
	 * @return
	 */
	public String getValueString();
}

