/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class TransactionalReferenceSystem implements ReferenceSystem{
	
	private final ReferenceSystem _committedReferences;
	
	private ReferenceSystem _newReferences;
	
	public TransactionalReferenceSystem() {
		createNewReferences();
		_committedReferences = newReferenceSystem();
	}
	
	private ReferenceSystem newReferenceSystem(){
	    return new HashcodeReferenceSystem();
	    
	    // An alternative reference system using a hashtable: 
	    // return new HashtableReferenceSystem();
	}

	public void addExistingReference(ObjectReference ref) {
		_committedReferences.addExistingReference(ref);
	}

	public void addNewReference(ObjectReference ref) {
		_newReferences.addNewReference(ref);
	}
	
	public void commit(){
		traveseNewReferences(new Visitor4() {
			public void visit(Object obj) {
				ObjectReference oref = (ObjectReference)obj;
				Object referent = oref.getObject();
				if(referent != null){
					_committedReferences.addExistingReference(oref);
				}
			}
		});
		createNewReferences();
	}

	public void traveseNewReferences(final Visitor4 visitor) {
		_newReferences.traverseReferences(visitor);
	}
	
	private void createNewReferences(){
		_newReferences = newReferenceSystem();
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
		traveseNewReferences(visitor);
		_committedReferences.traverseReferences(visitor);
	}

}
