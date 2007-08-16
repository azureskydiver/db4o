/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.activation.Activator;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ObjectReference extends PersistentBase implements ObjectInfo, Activator {
    
	private ClassMetadata _class;
	private Object _object;
	private VirtualAttributes _virtualAttributes;

	private ObjectReference _idPreceding;
	private ObjectReference _idSubsequent;
	private int _idSize;

	private ObjectReference _hcPreceding;
	private ObjectReference _hcSubsequent;
	private int _hcSize;
	private int _hcHashcode; // redundant hashCode
	
	private int _lastTopLevelCallId;
    
    public ObjectReference(){
    }
	
	public ObjectReference(int a_id) {
		_id = a_id;
	}

	public ObjectReference(ClassMetadata classMetadata, int id) {
		_class = classMetadata;
		_id = id;
	}
	
	public void activate() {
		if (isActive()) {
			return;
		}
		activate(container().transaction(), getObject(), 1, false);
	}

	public void activate(Transaction ta, Object obj, int depth, boolean isRefresh) {
	    activate1(ta, obj, depth, isRefresh);
		ta.container().activate3CheckStill(ta);
	}
	
	void activate1(Transaction ta, Object obj, int depth, boolean isRefresh) {
	    if(obj instanceof Db4oTypeImpl){
	        depth = ((Db4oTypeImpl)obj).adjustReadDepth(depth);
	    }
		if (depth > 0) {
		    ObjectContainerBase container = ta.container();
		    if(isRefresh){
				logActivation(container, "refresh");
		    }else{
				if (isActive()) {
					if (obj != null) {
						if (depth > 1) {
					        if (_class.config() != null) {
					            depth = _class.config().adjustActivationDepth(depth);
					        }
							_class.activateFields(ta, obj, depth);
						}
						return;
					}
				}
				logActivation(container, "activate");
		    }
			read(ta, null, obj, depth, Const4.ADD_MEMBERS_TO_ID_TREE_ONLY, false);
		}
	}
	
	private void logActivation(ObjectContainerBase container, String event) {
		logEvent(container, event, Const4.ACTIVATION);
	}

	private void logEvent(ObjectContainerBase container, String event, final int level) {
		if (container.configImpl().messageLevel() > level) {
			container.message("" + getID() + " " + event + " " + _class.getName());
		}
	}

	final void addExistingReferenceToIdTree(Transaction trans) {
		if (!(_class instanceof PrimitiveFieldHandler)) {
			trans.referenceSystem().addExistingReferenceToIdTree(this);
		}
	}
	
	/** return false if class not completely initialized, otherwise true **/
	boolean continueSet(Transaction trans, int updateDepth) {
		if (bitIsTrue(Const4.CONTINUE)) {
		    if(! _class.stateOKAndAncestors()){
		        return false;
		    }
            
            if(DTrace.enabled){
                DTrace.CONTINUESET.log(getID());
            }
            
			bitFalse(Const4.CONTINUE);
            
            StatefulBuffer writer = MarshallerFamily.current()._object.marshallNew(trans, this, updateDepth);

            ObjectContainerBase container = trans.container();
			container.writeNew(_class, writer);

            Object obj = _object;
			objectOnNew(trans, obj);
			
            if(! _class.isPrimitive()){
                _object = container._references.createYapRef(this, obj);
            }
			
			setStateClean();
			endProcessing();
		}
		return true;
	}

	private void objectOnNew(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		container.callbacks().objectOnNew(transaction, obj);
		_class.dispatchEvent(container, obj, EventDispatcher.NEW);
	}

	public void deactivate(Transaction trans, int depth) {
		if (depth > 0) {
			Object obj = getObject();
			if (obj != null) {
			    if(obj instanceof Db4oTypeImpl){
			        ((Db4oTypeImpl)obj).preDeactivate();
			    }
			    ObjectContainerBase container = trans.container();
				logActivation(container, "deactivate");
				setStateDeactivated();
				_class.deactivate(trans, obj, depth);
			}
		}
	}
	
	public byte getIdentifier() {
		return Const4.YAPOBJECT;
	}
	
	public long getInternalID() {
		return getID();
	}
	
	public Object getObject() {
		if (Platform4.hasWeakReferences()) {
			return Platform4.getYapRefObject(_object);
		}
		return _object;
	}
	
	public Object getObjectReference(){
		return _object;
	}
    
    public ObjectContainerBase container(){
        if(_class == null){
            return null;
        }
        return _class.container();
    }
    
    // this method will only work client-side or on
    // single ObjectContainers, after the YapClass
    // is set.
    public Transaction transaction(){
        ObjectContainerBase container = container();
        if(container != null){
            return container.transaction();
        }
        return null;
    }
    
    public Db4oUUID getUUID(){
        VirtualAttributes va = virtualAttributes(transaction());
        if(va != null && va.i_database != null){
            return new Db4oUUID(va.i_uuid, va.i_database.i_signature);
        }
        return null;
    }
	
    public long getVersion(){
        VirtualAttributes va = virtualAttributes(transaction());
        if(va == null) {
			return 0;
        }
		return va.i_version;
    }


	public final ClassMetadata classMetadata() {
		return _class;
	}

	public int ownLength() {
        throw Exceptions4.shouldNeverBeCalled();
	}
	
	public VirtualAttributes produceVirtualAttributes() {
		if(_virtualAttributes == null){
			_virtualAttributes = new VirtualAttributes();
		}
		return _virtualAttributes;
	}
	
	final Object peekPersisted(Transaction trans, int depth) {
        return read(trans, depth, Const4.TRANSIENT, false);
	}
	
	final Object read (Transaction trans, int instantiationDepth,int addToIDTree,boolean checkIDTree) {
		return read(trans, null, null, instantiationDepth, addToIDTree, checkIDTree); 
	}
	
	final Object read(
		Transaction trans,
		StatefulBuffer buffer,
		Object obj,
		int instantiationDepth,
		int addToIDTree,
        boolean checkIDTree) {

		// instantiationDepth is a way of overriding instantiation
		// in a positive manner

		if (beginProcessing()) {
		    
		    ObjectContainerBase container = trans.container();
		    int id = getID();
			if (buffer == null && id > 0) {
				buffer = container.readWriterByID(trans, id);
			}
			if (buffer != null) {
                
                ObjectHeader header = new ObjectHeader(container, buffer);
			    
				_class = header.classMetadata();

				if (_class == null) {
					return null;
				}
                
                if(checkIDTree){
                    // the typical side effect: static fields and enums
                    
                    Object objectInCacheFromClassCreation = trans.objectForIdFromCache(getID());
                    if(objectInCacheFromClassCreation != null){
                        return objectInCacheFromClassCreation;
                    }
                }

				buffer.setInstantiationDepth(instantiationDepth);
				buffer.setUpdateDepth(addToIDTree);
				
				if(addToIDTree == Const4.TRANSIENT){
				    obj = _class.instantiateTransient(this, obj, header._marshallerFamily, header._headerAttributes, buffer);
				}else{
				    obj = _class.instantiate(this, obj, header._marshallerFamily, header._headerAttributes, buffer, addToIDTree == Const4.ADD_TO_ID_TREE);
				}
				
			}
			endProcessing();
		}
		return obj;
	}

	public final Object readPrefetch(ObjectContainerBase container, StatefulBuffer buffer) {

		Object readObject = null;
		if (beginProcessing()) {
            
            ObjectHeader header = new ObjectHeader(container, buffer);

			_class = header.classMetadata();

			if (_class == null) {
				return null;
			}

			// We use an instantiationdepth of 1 only, if there is no special
			// configuration for the class. This is a quick fix due to a problem
			// instantiating Hashtables. There may be a better workaround that
			// works with configured objects only to make them fast also.
			//
			// An instantiation depth of 1 makes use of possibly prefetched strings
			// that are carried around in a_bytes.
			//
			// TODO: optimize  
			buffer.setInstantiationDepth(_class.configOrAncestorConfig() == null ? 1 : 0);

			readObject = _class.instantiate(this, getObject(), header._marshallerFamily, header._headerAttributes, buffer, true);
			
			endProcessing();
		}
		return readObject;
	}

	public final void readThis(Transaction trans, Buffer buffer) {
		if (Deploy.debug) {
			System.out.println(
				"YapObject.readThis should never be called. All handling takes place in read");
		}
	}

	void setObjectWeak(ObjectContainerBase container, Object obj) {
		if (container._references._weak) {
			if(_object != null){
				Platform4.killYapRef(_object);
			}
			_object = Platform4.createYapRef(container._references._queue, this, obj);
		} else {
			_object = obj;
		}
	}

	public void setObject(Object obj) {
		_object = obj;
	}

	final void store(Transaction trans, ClassMetadata classMetadata, Object obj){
		_object = obj;
		_class = classMetadata;
		
		writeObjectBegin();
		
		int id = trans.container().newUserObject();
		trans.slotFreePointerOnRollback(id);

        setID(id);

        // will be ended in continueset()
        beginProcessing();

        bitTrue(Const4.CONTINUE);
	}
	
	public void flagForDelete(int callId){
		_lastTopLevelCallId = - callId;
	}
	
	public boolean isFlaggedForDelete(){
		return _lastTopLevelCallId < 0;
	}
	
	public void flagAsHandled(int callId){
		_lastTopLevelCallId = callId;
	}
	
	public final boolean isFlaggedAsHandled(int callID){
		return _lastTopLevelCallId == callID;
	}
	
	public final boolean isValid() {
		return isValidId(getID()) && getObject() != null;
	}
	
	public static final boolean isValidId(int id){
		return id > 0;
	}
	
	public VirtualAttributes virtualAttributes(){
		return _virtualAttributes;
	}
	
	public VirtualAttributes virtualAttributes(Transaction trans){
        if(trans == null){
            return _virtualAttributes;
        }
        synchronized(trans.container().lock()){
    	    if(_virtualAttributes == null){ 
                if(_class.hasVirtualAttributes()){
                    _virtualAttributes = new VirtualAttributes();
                    _class.readVirtualAttributes(trans, this);
                }
    	    }else{
                if(! _virtualAttributes.suppliesUUID()){
                    if(_class.hasVirtualAttributes()){
                        _class.readVirtualAttributes(trans, this);
                    }
                }
            }
    	    return _virtualAttributes;
        }
	}
    
    public void setVirtualAttributes(VirtualAttributes at){
        _virtualAttributes = at;
    }

	public void writeThis(Transaction trans, Buffer buffer) {
		if (Deploy.debug) {
			System.out.println("YapObject.writeThis should never be called.");
		}
	}

	public void writeUpdate(Transaction trans, int updatedepth) {

		continueSet(trans, updatedepth);
		// make sure, a concurrent new, possibly triggered by objectOnNew
		// is written to the file

		// preventing recursive
		if (beginProcessing()) {
		    
		    Object obj = getObject();
		    
		    if(objectCanUpdate(trans, obj)){
				
				if ((!isActive()) || obj == null) {
					endProcessing();
					return;
				}
				if (Deploy.debug) {
					if (!(getID() > 0)) {
						System.out.println(
							"Object passed to set() with valid YapObject. YapObject had no ID.");
						throw new RuntimeException();
					}
					if (_class == null) {
						System.out.println(
							"Object passed to set() with valid YapObject. YapObject has no valid yapClass.");
						throw new RuntimeException();
					}
				}
				
				logEvent(trans.container(), "update", Const4.STATE);
				
				setStateClean();

				trans.writeUpdateDeleteMembers(getID(), _class, trans
						.container()._handlers.arrayType(obj), 0);
                
                MarshallerFamily.current()._object.marshallUpdate(trans, updatedepth, this, obj);
				
		    } else{
		        endProcessing();
		    }
		}
	}

	private boolean objectCanUpdate(Transaction transaction, Object obj) {
		ObjectContainerBase container = transaction.container();
		return container.callbacks().objectCanUpdate(transaction, obj)
			&& _class.dispatchEvent(container, obj, EventDispatcher.CAN_UPDATE);
	}

	/***** HCTREE *****/

	public ObjectReference hc_add(ObjectReference newRef) {
		if (newRef.getObject() == null) {
			return this;
		}
		newRef.hc_init();
		return hc_add1(newRef);
	}
    
    public void hc_init(){
        _hcPreceding = null;
        _hcSubsequent = null;
        _hcSize = 1;
        _hcHashcode = hc_getCode(getObject());
    }
    
	private ObjectReference hc_add1(ObjectReference newRef) {
		int cmp = hc_compare(newRef);
		if (cmp < 0) {
			if (_hcPreceding == null) {
				_hcPreceding = newRef;
				_hcSize++;
			} else {
				_hcPreceding = _hcPreceding.hc_add1(newRef);
				if (_hcSubsequent == null) {
					return hc_rotateRight();
				} 
				return hc_balance();
			}
		} else {
			if (_hcSubsequent == null) {
				_hcSubsequent = newRef;
				_hcSize++;
			} else {
				_hcSubsequent = _hcSubsequent.hc_add1(newRef);
				if (_hcPreceding == null) {
					return hc_rotateLeft();
				} 
				return hc_balance();
			}
		}
		return this;
	}

	private ObjectReference hc_balance() {
		int cmp = _hcSubsequent._hcSize - _hcPreceding._hcSize;
		if (cmp < -2) {
			return hc_rotateRight();
		} else if (cmp > 2) {
			return hc_rotateLeft();
		} else {
			_hcSize = _hcPreceding._hcSize + _hcSubsequent._hcSize + 1;
			return this;
		}
	}

	private void hc_calculateSize() {
		if (_hcPreceding == null) {
			if (_hcSubsequent == null) {
				_hcSize = 1;
			} else {
				_hcSize = _hcSubsequent._hcSize + 1;
			}
		} else {
			if (_hcSubsequent == null) {
				_hcSize = _hcPreceding._hcSize + 1;
			} else {
				_hcSize = _hcPreceding._hcSize + _hcSubsequent._hcSize + 1;
			}
		}
	}

	private int hc_compare(ObjectReference toRef) {
	    int cmp = toRef._hcHashcode - _hcHashcode;
	    if(cmp == 0){
	        cmp = toRef._id - _id;
	    }
		return cmp;
	}

	public ObjectReference hc_find(Object obj) {
		return hc_find(hc_getCode(obj), obj);
	}

	private ObjectReference hc_find(int id, Object obj) {
		int cmp = id - _hcHashcode;
		if (cmp < 0) {
			if (_hcPreceding != null) {
				return _hcPreceding.hc_find(id, obj);
			}
		} else if (cmp > 0) {
			if (_hcSubsequent != null) {
				return _hcSubsequent.hc_find(id, obj);
			}
		} else {
			if (obj == getObject()) {
				return this;
			}
			if (_hcPreceding != null) {
				ObjectReference inPreceding = _hcPreceding.hc_find(id, obj);
				if (inPreceding != null) {
					return inPreceding;
				}
			}
			if (_hcSubsequent != null) {
				return _hcSubsequent.hc_find(id, obj);
			}
		}
		return null;
	}

	private int hc_getCode(Object obj) {
		int hcode = System.identityHashCode(obj);
		if (hcode < 0) {
			hcode = ~hcode;
		}
		return hcode;
	}

	private ObjectReference hc_rotateLeft() {
		ObjectReference tree = _hcSubsequent;
		_hcSubsequent = tree._hcPreceding;
		hc_calculateSize();
		tree._hcPreceding = this;
		if(tree._hcSubsequent == null){
			tree._hcSize = 1 + _hcSize;
		}else{
			tree._hcSize = 1 + _hcSize + tree._hcSubsequent._hcSize;
		}
		return tree;
	}

	private ObjectReference hc_rotateRight() {
		ObjectReference tree = _hcPreceding;
		_hcPreceding = tree._hcSubsequent;
		hc_calculateSize();
		tree._hcSubsequent = this;
		if(tree._hcPreceding == null){
			tree._hcSize = 1 + _hcSize;
		}else{
			tree._hcSize = 1 + _hcSize + tree._hcPreceding._hcSize;
		}
		return tree;
	}

	private ObjectReference hc_rotateSmallestUp() {
		if (_hcPreceding != null) {
			_hcPreceding = _hcPreceding.hc_rotateSmallestUp();
			return hc_rotateRight();
		}
		return this;
	}

	ObjectReference hc_remove(ObjectReference findRef) {
		if (this == findRef) {
			return hc_remove();
		}
		int cmp = hc_compare(findRef);
		if (cmp <= 0) {
			if (_hcPreceding != null) {
				_hcPreceding = _hcPreceding.hc_remove(findRef);
			}
		}
		if (cmp >= 0) {
			if (_hcSubsequent != null) {
				_hcSubsequent = _hcSubsequent.hc_remove(findRef);
			}
		}
		hc_calculateSize();
		return this;
	}
    
    public void hc_traverse(Visitor4 visitor){
        if(_hcPreceding != null){
            _hcPreceding.hc_traverse(visitor);
        }
        if(_hcSubsequent != null){
            _hcSubsequent.hc_traverse(visitor);
        }
        
        // Traversing the leaves first allows to add ObjectReference 
        // nodes to different ReferenceSystem trees during commit
        
        visitor.visit(this);
    }

	private ObjectReference hc_remove() {
		if (_hcSubsequent != null && _hcPreceding != null) {
			_hcSubsequent = _hcSubsequent.hc_rotateSmallestUp();
			_hcSubsequent._hcPreceding = _hcPreceding;
			_hcSubsequent.hc_calculateSize();
			return _hcSubsequent;
		}
		if (_hcSubsequent != null) {
			return _hcSubsequent;
		}
		return _hcPreceding;
	}

	/***** IDTREE *****/

	ObjectReference id_add(ObjectReference newRef) {
		newRef._idPreceding = null;
		newRef._idSubsequent = null;
		newRef._idSize = 1;
		return id_add1(newRef);
	}

	private ObjectReference id_add1(ObjectReference newRef) {
		int cmp = newRef._id - _id;
		if (cmp < 0) {
			if (_idPreceding == null) {
				_idPreceding = newRef;
				_idSize++;
			} else {
				_idPreceding = _idPreceding.id_add1(newRef);
				if (_idSubsequent == null) {
					return id_rotateRight();
				} 
				return id_balance();
			}
		} else if(cmp > 0) {
			if (_idSubsequent == null) {
				_idSubsequent = newRef;
				_idSize++;
			} else {
				_idSubsequent = _idSubsequent.id_add1(newRef);
				if (_idPreceding == null) {
					return id_rotateLeft();
				} 
				return id_balance();
			}
		}
		return this;
	}

	private ObjectReference id_balance() {
		int cmp = _idSubsequent._idSize - _idPreceding._idSize;
		if (cmp < -2) {
			return id_rotateRight();
		} else if (cmp > 2) {
			return id_rotateLeft();
		} else {
			_idSize = _idPreceding._idSize + _idSubsequent._idSize + 1;
			return this;
		}
	}

	private void id_calculateSize() {
		if (_idPreceding == null) {
			if (_idSubsequent == null) {
				_idSize = 1;
			} else {
				_idSize = _idSubsequent._idSize + 1;
			}
		} else {
			if (_idSubsequent == null) {
				_idSize = _idPreceding._idSize + 1;
			} else {
				_idSize = _idPreceding._idSize + _idSubsequent._idSize + 1;
			}
		}
	}

	ObjectReference id_find(int id) {
		int cmp = id - _id;
		if (cmp > 0) {
			if (_idSubsequent != null) {
				return _idSubsequent.id_find(id);
			}
		} else if (cmp < 0) {
			if (_idPreceding != null) {
				return _idPreceding.id_find(id);
			}
		} else {
			return this;
		}
		return null;
	}

	private ObjectReference id_rotateLeft() {
		ObjectReference tree = _idSubsequent;
		_idSubsequent = tree._idPreceding;
		id_calculateSize();
		tree._idPreceding = this;
		if(tree._idSubsequent == null){
			tree._idSize = _idSize + 1;
		}else{
			tree._idSize = _idSize + 1 + tree._idSubsequent._idSize;
		}
		return tree;
	}

	private ObjectReference id_rotateRight() {
		ObjectReference tree = _idPreceding;
		_idPreceding = tree._idSubsequent;
		id_calculateSize();
		tree._idSubsequent = this;
		if(tree._idPreceding == null){
			tree._idSize = _idSize + 1;
		}else{
			tree._idSize = _idSize + 1 + tree._idPreceding._idSize;
		}
		return tree;
	}

	private ObjectReference id_rotateSmallestUp() {
		if (_idPreceding != null) {
			_idPreceding = _idPreceding.id_rotateSmallestUp();
			return id_rotateRight();
		}
		return this;
	}

	ObjectReference id_remove(int id) {
		int cmp = id - _id;
		if (cmp < 0) {
			if (_idPreceding != null) {
				_idPreceding = _idPreceding.id_remove(id);
			}
		} else if (cmp > 0) {
			if (_idSubsequent != null) {
				_idSubsequent = _idSubsequent.id_remove(id);
			}
		} else {
			return id_remove();
		}
		id_calculateSize();
		return this;
	}

	private ObjectReference id_remove() {
		if (_idSubsequent != null && _idPreceding != null) {
			_idSubsequent = _idSubsequent.id_rotateSmallestUp();
			_idSubsequent._idPreceding = _idPreceding;
			_idSubsequent.id_calculateSize();
			return _idSubsequent;
		}
		if (_idSubsequent != null) {
			return _idSubsequent;
		}
		return _idPreceding;
	}
	
	public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
	    try{
		    int id = getID();
		    String str = "ObjectReference\nID=" + id;
		    if(_class != null){
		        ObjectContainerBase container = _class.container();
		        if(container != null && id > 0){
		            StatefulBuffer writer = container.readWriterByID(container.transaction(), id);
		            if(writer != null){
		                str += "\nAddress=" + writer.getAddress();
		            }
                    ObjectHeader oh = new ObjectHeader(container(), writer);
		            ClassMetadata yc = oh.classMetadata();
		            if(yc != _class){
		                str += "\nYapClass corruption";
		            }else{
		                str += yc.toString(oh._marshallerFamily, writer, this, 0, 5);
		            }
		        }
		    }
		    Object obj = getObject();
		    if(obj == null){
		        str += "\nfor [null]";
		    }else{
		        String objToString ="";
			    try{
			        objToString = obj.toString();
			    }catch(Exception e){
			    }
			    ReflectClass claxx = classMetadata().reflector().forObject(obj);
			    str += "\n" + claxx.getName() + "\n" + objToString;
		    }
		    return str;
	    }catch(Exception e){
	        // e.printStackTrace();
	    }
	    return "Exception in YapObject analyzer";
	}

	
	
}
