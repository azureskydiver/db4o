/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.defragment;

import java.io.*;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * The ID mapping used internally during a defragmentation run.
 * 
 * @see Defragment
 */
public interface IdMapping {

	/**
	 * Returns a previously registered mapping ID for the given ID if it exists.
	 * If lenient mode is set to true, will provide the mapping ID for the next
	 * smaller original ID a mapping exists for, plus the difference between the 
	 * found ID and the original ID. Otherwise it returns 0.
	 * 
	 * @param origID The original ID
	 * @param lenient If true, lenient mode will be used for lookup, strict mode otherwise.
	 * @return The mapping ID for the given original ID or 0, if none has been registered.
	 */
	int mappedId(int origId, boolean lenient);

	/**
	 * Registers a mapping for the given IDs.
	 * 
	 * @param origID The original ID
	 * @param mappedID The ID to be mapped to the original ID.
	 * @param isClassID true if the given original ID specifies a class slot, false otherwise.
	 */
	void mapId(int origId, int mappedId, boolean isClassId);
	
	
	/**
	 * Maps an ID to a slot
	 * @param id
	 * @param slot
	 */
	void mapId(int id, Slot slot);
	
	
	/**
	 * provides a Visitable of all mappings of IDs to slots.
	 */
	Visitable<SlotChange> slotChanges();
	

	/**
	 * Prepares the mapping for use.
	 */
	void open() throws IOException;	
	
	/**
	 * Shuts down the mapping after use.
	 */
	void close();
}