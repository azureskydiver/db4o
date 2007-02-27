/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

/**
 * @exclude
 */
public interface ReferenceSystem {

	public void addNewReference(ObjectReference ref);

	public void addExistingReference(ObjectReference ref);

	public void addExistingReferenceToObjectTree(ObjectReference ref);

	public void addExistingReferenceToIDTree(ObjectReference ref);

	public ObjectReference referenceForId(int id);

	public ObjectReference referenceForObject(Object obj);

	public void removeReference(ObjectReference ref);

}