/**
 * 
 */
package com.db4o.inside.fieldindex;

import com.db4o.TreeInt;

public class FieldIndexProcessorResult {	
	public static final FieldIndexProcessorResult NO_INDEX_FOUND = new FieldIndexProcessorResult(null);

	public static final FieldIndexProcessorResult FOUND_INDEX_BUT_NO_MATCH = new FieldIndexProcessorResult(null);
	
	public final TreeInt found;
	
	public FieldIndexProcessorResult(TreeInt found_) {
		found = found_;
	}
}