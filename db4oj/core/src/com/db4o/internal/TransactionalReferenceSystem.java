/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class TransactionalReferenceSystem implements ReferenceSystem{
	
	final ReferenceSystem _committedReferences = new HashcodeReferenceSystem();
	
	private ReferenceSystem _newReferences;
	
	public TransactionalReferenceSystem() {
		createNewReferences();
	}

	public void addExistingReference(ObjectReference ref) {
		_committedReferences.addExistingReference(ref);
	}

	public void addExistingReferenceToIdTree(ObjectReference ref) {
		_committedReferences.addExistingReferenceToIdTree(ref);
	}

	public void addExistingReferenceToObjectTree(ObjectReference ref) {
		_committedReferences.addExistingReferenceToObjectTree(ref);
	}

	public void addNewReference(ObjectReference ref) {
		_newReferences.addNewReference(ref);
	}
	
	public void commit(){
		_newReferences.traverseReferences(new Visitor4() {
			public void visit(Object obj) {
				_committedReferences.addExistingReference((ObjectReference)obj);
			}
		});
		createNewReferences();
	}
	
	private void createNewReferences(){
		_newReferences = new HashcodeReferenceSystem();
	}

	public ObjectReference referenceForId(int id) {
		ObjectReference ref = _newReferences.referenceForId(id);
		if(ref != null){
			return ref;
		}
		return _committedReferences.referenceForId(id);
	}

	public ObjectReference referenceForObject(Object obj) {
		ObjectReference ref = _newReferences.referenceForObject(obj);
		if(ref != null){
			return ref;
		}
		return _committedReferences.referenceForObject(obj);
	}

	public void removeReference(ObjectReference ref) {
		_newReferences.removeReference(ref);
		_committedReferences.removeReference(ref);
	}
	
	public void rollback(){
		createNewReferences();
	}
	
	public void traverseReferences(Visitor4 visitor) {
		_newReferences.traverseReferences(visitor);
		_committedReferences.traverseReferences(visitor);
	}

}
