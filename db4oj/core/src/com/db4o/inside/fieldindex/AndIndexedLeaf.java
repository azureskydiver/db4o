package com.db4o.inside.fieldindex;


public class AndIndexedLeaf extends JoinedLeaf {

	public AndIndexedLeaf(IndexedLeaf leaf1, IndexedLeaf leaf2) {
		super(leaf1, leaf1.getRange().intersect(leaf2.getRange()));
	}
}
