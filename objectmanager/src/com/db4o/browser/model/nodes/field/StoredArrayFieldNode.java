/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model.nodes.field;

import com.db4o.browser.model.Database;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.ext.StoredField;

/**
 * StoredArrayFieldNode.
 *
 * @author djo
 */
public class StoredArrayFieldNode extends StoredFieldNode implements IModelNode {

	/**
	 * @param field
	 * @param instance
	 * @param database
	 */
	public StoredArrayFieldNode(StoredField field, Object instance, Database database) {
		super(field, instance, database);
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#children()
	 */
	public IModelNode[] children() {
		return super.children();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#getText()
	 */
	public String getText() {
		// TODO Auto-generated method stub
		return super.getText();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#getValueString()
	 */
	public String getValueString() {
		// TODO Auto-generated method stub
		return super.getValueString();
	}

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.nodes.field.StoredFieldNode#hasChildren()
	 */
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return super.hasChildren();
	}
	
	

}
