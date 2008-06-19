package com.db4o.ibs;

import com.db4o.ext.*;

/**
 * Accumulates changes into {@link ChangeSet} instances.
 */
public interface ChangeSetBuilder {
	
	/**
	 * Accumulates a 'New Object' change.
	 * @param object
	 */
	void added(ObjectInfo object);
	
	/**
	 * Accumulates a 'Delete Object' change.
	 * @param object
	 */
	void deleted(ObjectInfo object);
	
	/**
	 * Accumulates a 'Update Object' change.
	 * @param object
	 */
	void updated(ObjectInfo object);

	/**
	 * Returns all the accumulated changes as a {@link ChangeSet} object.
	 *  
	 * @return
	 */
	ChangeSet build();
}

	
