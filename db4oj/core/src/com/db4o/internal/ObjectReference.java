/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.reflect.*;


/**
 * @renameto ObjectReference
 * @exclude
 */
public class ObjectReference extends PersistentBase implements ObjectInfo{
    
	private ClassMetadata _class;
	private Object _object;
	private VirtualAttributes _virtualAttributes;

	private ObjectReference id_preceding;
	private ObjectReference id_subsequent;
	private int id_size;

	private ObjectReference hc_preceding;
	private ObjectReference hc_subsequent;
	private int hc_size;
	private int hc_code; // redundant hashCode
	
	private int _lastTopLevelCallId;
    
    public ObjectReference(){
    }
	
	public ObjectReference(int a_id) {
		i_id = a_id;
	}

	ObjectReference(ClassMetadata a_yapClass, int a_id) {
		_class = a_yapClass;
		i_id = a_id;
	}
	
	public void activate(Transaction ta, Object a_object, int a_depth, boolean a_refresh) {
	    activate1(ta, a_object, a_depth, a_refresh);
		ta.stream().activate3CheckStill(ta);
	}
	
	void activate1(Transaction ta, Object a_object, int a_depth, boolean a_refresh) {
	    if(a_object instanceof Db4oTypeImpl){
	        a_depth = ((Db4oTypeImpl)a_object).adjustReadDepth(a_depth);
	    }
		if (a_depth > 0) {
		    ObjectContainerBase stream = ta.stream();
		    if(a_refresh){
				logActivation(stream, "refresh");
		    }else{
				if (isActive()) {
					if (a_object != null) {
						if (a_depth > 1) {
					        if (_class.i_config != null) {
					            a_depth = _class.i_config.adjustActivationDepth(a_depth);
					        }
							_class.activateFields(ta, a_object, a_depth);
						}
						return;
					}
				}
				logActivation(stream, "activate");
		    }
			read(ta, null, a_object, a_depth, Const4.ADD_MEMBERS_TO_ID_TREE_ONLY, false);
		}
	}
	
	private void logActivation(ObjectContainerBase stream, String event) {
		logEvent(stream, event, Const4.ACTIVATION);
	}

	private void logEvent(ObjectContainerBase stream, String event, final int level) {
		if (stream.configImpl().messageLevel() > level) {
			stream.message("" + getID() + " " + event + " " + _class.getName());
		}
	}

	final void addToIDTree(ObjectContainerBase a_stream) {
		if (!(_class instanceof PrimitiveFieldHandler)) {
			a_stream.idTreeAdd(this);
		}
	}
	
	/** return false if class not completely initialized, otherwise true **/
	boolean continueSet(Transaction a_trans, int a_updateDepth) {
		if (bitIsTrue(Const4.CONTINUE)) {
		    if(! _class.stateOKAndAncestors()){
		        return false;
		    }
            
            if(DTrace.enabled){
                DTrace.CONTINUESET.log(getID());
            }
            
			bitFalse(Const4.CONTINUE);
            
            StatefulBuffer writer = MarshallerFamily.current()._object.marshallNew(a_trans, this, a_updateDepth);

            ObjectContainerBase stream = a_trans.stream();
			stream.writeNew(_class, writer);

            Object obj = _object;
			objectOnNew(stream, obj);
			
            if(! _class.isPrimitive()){
                _object = stream.i_references.createYapRef(this, obj);
            }
			
			setStateClean();
			endProcessing();
		}
		return true;
	}

	private void objectOnNew(ObjectContainerBase stream, Object obj) {
		stream.callbacks().objectOnNew(obj);
		_class.dispatchEvent(stream, obj, EventDispatcher.NEW);
	}

	public void deactivate(Transaction a_trans, int a_depth) {
		if (a_depth > 0) {
			Object obj = getObject();
			if (obj != null) {
			    if(obj instanceof Db4oTypeImpl){
			        ((Db4oTypeImpl)obj).preDeactivate();
			    }
			    ObjectContainerBase stream = a_trans.stream();
				logActivation(stream, "deactivate");
				setStateDeactivated();
				_class.deactivate(a_trans, obj, a_depth);
			}
		}
	}
	
	public byte getIdentifier() {
		return Const4.YAPOBJECT;
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
    
    public ObjectContainerBase getStream(){
        if(_class == null){
            return null;
        }
        return _class.getStream();
    }
    
    // this method will only work client-side or on
    // single ObjectContainers, after the YapClass
    // is set.
    public Transaction getTrans(){
        ObjectContainerBase stream = getStream();
        if(stream != null){
            return stream.getTransaction();
        }
        return null;
    }
    
    public Db4oUUID getUUID(){
        VirtualAttributes va = virtualAttributes(getTrans());
        if(va != null && va.i_database != null){
            return new Db4oUUID(va.i_uuid, va.i_database.i_signature);
        }
        return null;
    }
	
    public long getVersion(){
        VirtualAttributes va = virtualAttributes(getTrans());
        if(va == null) {
			return 0;
        }
		return va.i_version;
    }


	public ClassMetadata getYapClass() {
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
	
	final Object read(
		Transaction ta,
		StatefulBuffer a_reader,
		Object a_object,
		int a_instantiationDepth,
		int addToIDTree,
        boolean checkIDTree) {

		// a_instantiationDepth is a way of overriding instantiation
		// in a positive manner

		if (beginProcessing()) {
		    
		    ObjectContainerBase stream = ta.stream();

			if (a_reader == null) {
				a_reader = stream.readWriterByID(ta, getID());
			}
			if (a_reader != null) {
                
                ObjectHeader header = new ObjectHeader(stream, a_reader);
			    
				_class = header.yapClass();

				if (_class == null) {
					return null;
				}
                
                if(checkIDTree){
                    // the typical side effect: static fields and enums
                    
                    Object objectInCacheFromClassCreation = stream.objectForIDFromCache(getID());
                    if(objectInCacheFromClassCreation != null){
                        return objectInCacheFromClassCreation;
                    }
                }

				a_reader.setInstantiationDepth(a_instantiationDepth);
				a_reader.setUpdateDepth(addToIDTree);
				
				if(addToIDTree == Const4.TRANSIENT){
				    a_object = _class.instantiateTransient(this, a_object, header._marshallerFamily, header._headerAttributes, a_reader);
				}else{
				    a_object = _class.instantiate(this, a_object, header._marshallerFamily, header._headerAttributes, a_reader, addToIDTree == Const4.ADD_TO_ID_TREE);
				}
				
			}
			endProcessing();
		}
		return a_object;
	}

	public final Object readPrefetch(ObjectContainerBase a_stream, StatefulBuffer a_reader) {

		Object readObject = null;
		if (beginProcessing()) {
            
            ObjectHeader header = new ObjectHeader(a_stream, a_reader);

			_class = header.yapClass();

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
			a_reader.setInstantiationDepth(_class.configOrAncestorConfig() == null ? 1 : 0);

			readObject = _class.instantiate(this, getObject(), header._marshallerFamily, header._headerAttributes, a_reader, true);
			
			endProcessing();
		}
		return readObject;
	}

	public final void readThis(Transaction a_trans, Buffer a_bytes) {
		if (Deploy.debug) {
			System.out.println(
				"YapObject.readThis should never be called. All handling takes place in read");
		}
	}

	void setObjectWeak(ObjectContainerBase a_stream, Object a_object) {
		if (a_stream.i_references._weak) {
			if(_object != null){
				Platform4.killYapRef(_object);
			}
			_object = Platform4.createYapRef(a_stream.i_references._queue, this, a_object);
		} else {
			_object = a_object;
		}
	}

	public void setObject(Object a_object) {
		_object = a_object;
	}

	final void store(Transaction trans, ClassMetadata yapClass, Object obj){
		_object = obj;
		_class = yapClass;
		
		writeObjectBegin();

        setID(trans.stream().newUserObject());

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
	
	public VirtualAttributes virtualAttributes(){
		return _virtualAttributes;
	}
	
	public VirtualAttributes virtualAttributes(Transaction a_trans){
        if(a_trans == null){
            return _virtualAttributes;
        }
	    if(_virtualAttributes == null){ 
            if(_class.hasVirtualAttributes()){
                _virtualAttributes = new VirtualAttributes();
                _class.readVirtualAttributes(a_trans, this);
            }
	    }else{
            if(! _virtualAttributes.suppliesUUID()){
                if(_class.hasVirtualAttributes()){
                    _class.readVirtualAttributes(a_trans, this);
                }
            }
        }
	    return _virtualAttributes;
	}
    
    public void setVirtualAttributes(VirtualAttributes at){
        _virtualAttributes = at;
    }

	public void writeThis(Transaction trans, Buffer a_writer) {
		if (Deploy.debug) {
			System.out.println("YapObject.writeThis should never be called.");
		}
	}

	public void writeUpdate(Transaction a_trans, int a_updatedepth) {

		continueSet(a_trans, a_updatedepth);
		// make sure, a concurrent new, possibly triggered by objectOnNew
		// is written to the file

		// preventing recursive
		if (beginProcessing()) {
		    
		    Object obj = getObject();
		    
		    if(objectCanUpdate(a_trans.stream(), obj)){
				
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
				
				logEvent(a_trans.stream(), "update", Const4.STATE);
				
				setStateClean();

				a_trans.writeUpdateDeleteMembers(getID(), _class, a_trans
						.stream().i_handlers.arrayType(obj), 0);
                
                MarshallerFamily.current()._object.marshallUpdate(a_trans, a_updatedepth, this, obj);
				
		    } else{
		        endProcessing();
		    }
		}
	}

	private boolean objectCanUpdate(ObjectContainerBase stream, Object obj) {
		return stream.callbacks().objectCanUpdate(obj)
			&& _class.dispatchEvent(stream, obj, EventDispatcher.CAN_UPDATE);
	}

	/***** HCTREE *****/

	public ObjectReference hc_add(ObjectReference a_add) {
		Object obj = a_add.getObject();
		if (obj != null) {
			a_add.hc_init(obj);
			return hc_add1(a_add);
		} 
		return this;
	}
    
    public void hc_init(Object obj){
        hc_preceding = null;
        hc_subsequent = null;
        hc_size = 1;
        hc_code = hc_getCode(obj);
    }
    
	private ObjectReference hc_add1(ObjectReference a_new) {
		int cmp = hc_compare(a_new);
		if (cmp < 0) {
			if (hc_preceding == null) {
				hc_preceding = a_new;
				hc_size++;
			} else {
				hc_preceding = hc_preceding.hc_add1(a_new);
				if (hc_subsequent == null) {
					return hc_rotateRight();
				} 
				return hc_balance();
			}
		} else {
			if (hc_subsequent == null) {
				hc_subsequent = a_new;
				hc_size++;
			} else {
				hc_subsequent = hc_subsequent.hc_add1(a_new);
				if (hc_preceding == null) {
					return hc_rotateLeft();
				} 
				return hc_balance();
			}
		}
		return this;
	}

	private ObjectReference hc_balance() {
		int cmp = hc_subsequent.hc_size - hc_preceding.hc_size;
		if (cmp < -2) {
			return hc_rotateRight();
		} else if (cmp > 2) {
			return hc_rotateLeft();
		} else {
			hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
			return this;
		}
	}

	private void hc_calculateSize() {
		if (hc_preceding == null) {
			if (hc_subsequent == null) {
				hc_size = 1;
			} else {
				hc_size = hc_subsequent.hc_size + 1;
			}
		} else {
			if (hc_subsequent == null) {
				hc_size = hc_preceding.hc_size + 1;
			} else {
				hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
			}
		}
	}

	private int hc_compare(ObjectReference a_to) {
	    int cmp = a_to.hc_code - hc_code;
	    if(cmp == 0){
	        cmp = a_to.i_id - i_id;
	    }
		return cmp;
	}

	public ObjectReference hc_find(Object obj) {
		return hc_find(hc_getCode(obj), obj);
	}

	private ObjectReference hc_find(int a_id, Object obj) {
		int cmp = a_id - hc_code;
		if (cmp < 0) {
			if (hc_preceding != null) {
				return hc_preceding.hc_find(a_id, obj);
			}
		} else if (cmp > 0) {
			if (hc_subsequent != null) {
				return hc_subsequent.hc_find(a_id, obj);
			}
		} else {
			if (obj == getObject()) {
				return this;
			}
			if (hc_preceding != null) {
				ObjectReference inPreceding = hc_preceding.hc_find(a_id, obj);
				if (inPreceding != null) {
					return inPreceding;
				}
			}
			if (hc_subsequent != null) {
				return hc_subsequent.hc_find(a_id, obj);
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
		ObjectReference tree = hc_subsequent;
		hc_subsequent = tree.hc_preceding;
		hc_calculateSize();
		tree.hc_preceding = this;
		if(tree.hc_subsequent == null){
			tree.hc_size = 1 + hc_size;
		}else{
			tree.hc_size = 1 + hc_size + tree.hc_subsequent.hc_size;
		}
		return tree;
	}

	private ObjectReference hc_rotateRight() {
		ObjectReference tree = hc_preceding;
		hc_preceding = tree.hc_subsequent;
		hc_calculateSize();
		tree.hc_subsequent = this;
		if(tree.hc_preceding == null){
			tree.hc_size = 1 + hc_size;
		}else{
			tree.hc_size = 1 + hc_size + tree.hc_preceding.hc_size;
		}
		return tree;
	}

	private ObjectReference hc_rotateSmallestUp() {
		if (hc_preceding != null) {
			hc_preceding = hc_preceding.hc_rotateSmallestUp();
			return hc_rotateRight();
		}
		return this;
	}

	ObjectReference hc_remove(ObjectReference a_find) {
		if (this == a_find) {
			return hc_remove();
		}
		int cmp = hc_compare(a_find);
		if (cmp <= 0) {
			if (hc_preceding != null) {
				hc_preceding = hc_preceding.hc_remove(a_find);
			}
		}
		if (cmp >= 0) {
			if (hc_subsequent != null) {
				hc_subsequent = hc_subsequent.hc_remove(a_find);
			}
		}
		hc_calculateSize();
		return this;
	}
    
    public void hc_traverse(Visitor4 visitor){
        if(hc_preceding != null){
            hc_preceding.hc_traverse(visitor);
        }
        visitor.visit(this);
        if(hc_subsequent != null){
            hc_subsequent.hc_traverse(visitor);
        }
    }

	private ObjectReference hc_remove() {
		if (hc_subsequent != null && hc_preceding != null) {
			hc_subsequent = hc_subsequent.hc_rotateSmallestUp();
			hc_subsequent.hc_preceding = hc_preceding;
			hc_subsequent.hc_calculateSize();
			return hc_subsequent;
		}
		if (hc_subsequent != null) {
			return hc_subsequent;
		}
		return hc_preceding;
	}

	/***** IDTREE *****/

	ObjectReference id_add(ObjectReference a_add) {
		a_add.id_preceding = null;
		a_add.id_subsequent = null;
		a_add.id_size = 1;
		return id_add1(a_add);
	}

	private ObjectReference id_add1(ObjectReference a_new) {
		int cmp = a_new.i_id - i_id;
		if (cmp < 0) {
			if (id_preceding == null) {
				id_preceding = a_new;
				id_size++;
			} else {
				id_preceding = id_preceding.id_add1(a_new);
				if (id_subsequent == null) {
					return id_rotateRight();
				} 
				return id_balance();
			}
		} else if(cmp > 0) {
			if (id_subsequent == null) {
				id_subsequent = a_new;
				id_size++;
			} else {
				id_subsequent = id_subsequent.id_add1(a_new);
				if (id_preceding == null) {
					return id_rotateLeft();
				} 
				return id_balance();
			}
		}
		return this;
	}

	private ObjectReference id_balance() {
		int cmp = id_subsequent.id_size - id_preceding.id_size;
		if (cmp < -2) {
			return id_rotateRight();
		} else if (cmp > 2) {
			return id_rotateLeft();
		} else {
			id_size = id_preceding.id_size + id_subsequent.id_size + 1;
			return this;
		}
	}

	private void id_calculateSize() {
		if (id_preceding == null) {
			if (id_subsequent == null) {
				id_size = 1;
			} else {
				id_size = id_subsequent.id_size + 1;
			}
		} else {
			if (id_subsequent == null) {
				id_size = id_preceding.id_size + 1;
			} else {
				id_size = id_preceding.id_size + id_subsequent.id_size + 1;
			}
		}
	}

	ObjectReference id_find(int a_id) {
		int cmp = a_id - i_id;
		if (cmp > 0) {
			if (id_subsequent != null) {
				return id_subsequent.id_find(a_id);
			}
		} else if (cmp < 0) {
			if (id_preceding != null) {
				return id_preceding.id_find(a_id);
			}
		} else {
			return this;
		}
		return null;
	}

	private ObjectReference id_rotateLeft() {
		ObjectReference tree = id_subsequent;
		id_subsequent = tree.id_preceding;
		id_calculateSize();
		tree.id_preceding = this;
		if(tree.id_subsequent == null){
			tree.id_size = id_size + 1;
		}else{
			tree.id_size = id_size + 1 + tree.id_subsequent.id_size;
		}
		return tree;
	}

	private ObjectReference id_rotateRight() {
		ObjectReference tree = id_preceding;
		id_preceding = tree.id_subsequent;
		id_calculateSize();
		tree.id_subsequent = this;
		if(tree.id_preceding == null){
			tree.id_size = id_size + 1;
		}else{
			tree.id_size = id_size + 1 + tree.id_preceding.id_size;
		}
		return tree;
	}

	private ObjectReference id_rotateSmallestUp() {
		if (id_preceding != null) {
			id_preceding = id_preceding.id_rotateSmallestUp();
			return id_rotateRight();
		}
		return this;
	}

	ObjectReference id_remove(int a_id) {
		int cmp = a_id - i_id;
		if (cmp < 0) {
			if (id_preceding != null) {
				id_preceding = id_preceding.id_remove(a_id);
			}
		} else if (cmp > 0) {
			if (id_subsequent != null) {
				id_subsequent = id_subsequent.id_remove(a_id);
			}
		} else {
			return id_remove();
		}
		id_calculateSize();
		return this;
	}

	private ObjectReference id_remove() {
		if (id_subsequent != null && id_preceding != null) {
			id_subsequent = id_subsequent.id_rotateSmallestUp();
			id_subsequent.id_preceding = id_preceding;
			id_subsequent.id_calculateSize();
			return id_subsequent;
		}
		if (id_subsequent != null) {
			return id_subsequent;
		}
		return id_preceding;
	}
	
	public String toString(){
        if(! Debug4.prettyToStrings){
            return super.toString();
        }
	    try{
		    int id = getID();
		    String str = "YapObject\nID=" + id;
		    if(_class != null){
		        ObjectContainerBase stream = _class.getStream();
		        if(stream != null && id > 0){
		            StatefulBuffer writer = stream.readWriterByID(stream.getTransaction(), id);
		            if(writer != null){
		                str += "\nAddress=" + writer.getAddress();
		            }
                    ObjectHeader oh = new ObjectHeader(stream, writer);
		            ClassMetadata yc = oh.yapClass();
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
			    ReflectClass claxx = getYapClass().reflector().forObject(obj);
			    str += "\n" + claxx.getName() + "\n" + objToString;
		    }
		    return str;
	    }catch(Exception e){
	        // e.printStackTrace();
	    }
	    return "Exception in YapObject analyzer";
	}
	


    
}
