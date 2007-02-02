/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.query.processor.*;

public class FieldIndexProcessorResult {
	
	public static final FieldIndexProcessorResult NO_INDEX_FOUND = new FieldIndexProcessorResult(null);

	public static final FieldIndexProcessorResult FOUND_INDEX_BUT_NO_MATCH = new FieldIndexProcessorResult(null);
	
	private final IndexedNode _indexedNode;
	
	public FieldIndexProcessorResult(IndexedNode indexedNode) {
		_indexedNode = indexedNode;
	}
	
	public Tree toQCandidate(QCandidates candidates){
		return TreeInt.toQCandidate(toTreeInt(), candidates);
	}
	
	public TreeInt toTreeInt(){
		if(foundMatch()){
			return _indexedNode.toTreeInt();
		}
		return null;
	}
	
	public boolean foundMatch(){
		return foundIndex() && ! noMatch();
	}
	
	public boolean foundIndex(){
		return this != NO_INDEX_FOUND;
	}
	
	public boolean noMatch(){
		return this == FOUND_INDEX_BUT_NO_MATCH;
	}
	
	public Iterator4 iterateIDs(){
		return new MappingIterator(_indexedNode.iterator()) {
			protected Object map(Object current) {
			    FieldIndexKey composite = (FieldIndexKey)current;
				return new Integer(composite.parentID());
			}
		};
	}
	
}