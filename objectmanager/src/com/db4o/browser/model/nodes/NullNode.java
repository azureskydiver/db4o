package com.db4o.browser.model.nodes;

public class NullNode implements IModelNode {
	public final static NullNode INSTANCE=new NullNode();
	
	private NullNode() {
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

}
