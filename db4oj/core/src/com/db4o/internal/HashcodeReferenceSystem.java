/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;


/**
 * @exclude
 */
public class HashcodeReferenceSystem implements ReferenceSystem {
	
	private ObjectReference       _hashCodeTree;
	
	private ObjectReference       _idTree;
	
	public HashcodeReferenceSystem() {
		addOneEmptyNode();
	}
	
	private void addOneEmptyNode(){
	    _idTree = new ObjectReference(0);
	    _idTree.setObject(new Object());
	    _hashCodeTree = _idTree;
	}

	public void addNewReference(ObjectReference ref){
		addReference(ref);
	}

	public void addExistingReference(ObjectReference ref){
		addReference(ref);
	}

	private void addReference(ObjectReference ref){
		idAdd(ref);
		hashCodeAdd(ref);
	}
	
	public void addExistingReferenceToObjectTree(ObjectReference ref) {
		hashCodeAdd(ref);
	}
	
	public void addExistingReferenceToIDTree(ObjectReference ref) {
		idAdd(ref);
	}
	
	private void hashCodeAdd(ObjectReference ref){
		if (Deploy.debug) {
		    Object obj = ref.getObject();
		    if (obj != null) {
		        ObjectReference existing = referenceForObject(obj);
		        if (existing != null) {
		            System.out.println("Duplicate alarm hc_Tree");
		        }
		    }
		}
		_hashCodeTree = _hashCodeTree.hc_add(ref);
	}
	
	private void idAdd(ObjectReference ref){
		if(DTrace.enabled){
		    DTrace.ID_TREE_ADD.log(ref.getID());
		}
		if (Deploy.debug) {
		    ObjectReference existing = referenceForId(ref.getID());
		    if (existing != null) {
		        System.out.println("Duplicate alarm id_Tree:" + ref.getID());
		    }
		}
		_idTree = _idTree.id_add(ref);
	}
	
	public ObjectReference referenceForId(int id){
        if(DTrace.enabled){
            DTrace.GET_YAPOBJECT.log(id);
        }
        if(id <= 0){
            return null;
        }
        return _idTree.id_find(id);
	}
	
	public ObjectReference referenceForObject(Object obj) {
		return _hashCodeTree.hc_find(obj);
	}

	public void removeReference(ObjectReference ref) {
        if(DTrace.enabled){
            DTrace.REFERENCE_REMOVED.log(ref.getID());
        }
        _hashCodeTree = _hashCodeTree.hc_remove(ref);
        _idTree = _idTree.id_remove(ref.getID());
	}
	
}
