package com.db4o.ibs;

import com.db4o.internal.*;

/**
 * Accumulates changes into {@link ChangeSet} instances.
 */
public interface ChangeSetBuilder {
	
	/**
	 * Accumulates a 'New Object' change.
	 * 
	 * @param transaction
	 * @param object
	 */
	void create(Transaction transaction, Object object);

	/**
	 * Returns all the accumulated changes for the specific transaction as a {@link ChangeSet} object.
	 *
	 * Accumulated changes are forgotten.
	 * 
	 * @param transaction
	 * @return
	 */
	ChangeSet build(Transaction transaction);
}
