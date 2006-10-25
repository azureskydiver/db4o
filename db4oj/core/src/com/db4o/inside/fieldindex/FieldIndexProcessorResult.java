/**
 * 
 */
package com.db4o.inside.fieldindex;

import com.db4o.*;
import com.db4o.foundation.*;

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
		return _indexedNode.toTreeInt();
	}
	
	
}