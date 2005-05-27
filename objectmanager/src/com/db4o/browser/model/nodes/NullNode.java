package com.db4o.browser.model.nodes;

import com.db4o.browser.model.IDatabase;

public class NullNode implements IModelNode {
	private IDatabase database;

    public NullNode(IDatabase database) {
        this.database = database;
	}
    
    public IDatabase getDatabase() {
        return database;
    }
	
	public boolean hasChildren() {
		return false;
	}

	public IModelNode[] children() {
		return new IModelNode[0];
	}

	public String getText() {
		return "null";
	}

	public String getName() {
		return "null";
	}

	public String getValueString() {
		return "null";
	}

    public void setShowType(boolean showType) {
        // Nothing needed here
    }

    public boolean isEditable() {
        return true;
    }

    public Object getEditValue() {
        return "";
    }

}
