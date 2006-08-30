package com.db4o.inside.fieldindex;


public class OrIndexedLeaf extends JoinedLeaf {
	
	public OrIndexedLeaf(IndexedLeaf leaf1, IndexedLeaf leaf2) {
		super(leaf1, leaf1.getRange().union(leaf2.getRange()));
	}
	
}
