/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.caching.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;

/**
 * @exclude
 */
public class LocalTransaction extends Transaction {

    private final IdentitySet4 _participants = new IdentitySet4(); 

    Tree _writtenUpdateAdjustedIndexes;
    
	protected final LocalObjectContainer _file;
	
	private final CommittedCallbackDispatcher _committedCallbackDispatcher;
	
	private final Cache4<Integer, ByteArrayBuffer> _slotCache;
	
	public LocalTransaction(ObjectContainerBase container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
		_file = (LocalObjectContainer) container;
        _committedCallbackDispatcher = new CommittedCallbackDispatcher() {
    		public boolean willDispatchCommitted() {
    			return callbacks().caresAboutCommitted();
    		}
    		public void dispatchCommitted(CallbackObjectInfoCollections committedInfo) {
    			callbacks().commitOnCompleted(LocalTransaction.this, committedInfo);
    		}
    	};
    	_slotCache = createSlotCache();
	}

	private Cache4<Integer, ByteArrayBuffer> createSlotCache() {
	    if(isSystemTransaction()) {
	    	int slotCacheSize = config().slotCacheSize();
	    	if (slotCacheSize > 0) {
	    		return CacheFactory.new2QCache(slotCacheSize);
	    	}
    	}
    	return new NullCache4<Integer, ByteArrayBuffer>();
    }

	public Config4Impl config() {
		return container().config();
	}

	public LocalObjectContainer localContainer() {
		return _file;
	}
	
    public void commit() {
    	commit(_committedCallbackDispatcher);
    }
    
    public void commit(CommittedCallbackDispatcher dispatcher) {
        synchronized (container().lock()) {
        	
        	dispatchCommittingCallback();   
        	
        	if (!doCommittedCallbacks(dispatcher)) {
        		commitListeners();
        		commitImpl();
        		commitClearAll();
    		} else {
    			commitListeners();
    			Collection4 deleted = collectCommittedCallbackDeletedInfo();
                commitImpl();
                final CallbackObjectInfoCollections committedInfo = collectCommittedCallbackInfo(deleted);
        		commitClearAll();
        		dispatcher.dispatchCommitted(
        				CallbackObjectInfoCollections.EMTPY == committedInfo
        				? committedInfo
        				: new CallbackObjectInfoCollections(
        						committedInfo.added,
        						committedInfo.updated,
        						new ObjectInfoCollectionImpl(deleted)));
    		}
        }
    }	

	private void dispatchCommittingCallback() {
		if(doCommittingCallbacks()){
			callbacks().commitOnStarted(this, collectCommittingCallbackInfo());
		}
	}

	private boolean doCommittedCallbacks(CommittedCallbackDispatcher dispatcher) {
        if (isSystemTransaction()){
            return false;
        }
		return dispatcher.willDispatchCommitted();
	}

	private boolean doCommittingCallbacks() {
		if (isSystemTransaction()) {
			return false;
		}
		return callbacks().caresAboutCommitting();
	}
    
	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException();
		}
		checkSynchronization();	
		if (!_participants.contains(participant)) {
			_participants.add(participant);
		}
	}

	private void commitImpl(){
        
        if(DTrace.enabled){
            DTrace.TRANS_COMMIT.logInfo( "server == " + container().isServer() + ", systemtrans == " +  isSystemTransaction());
        }
        
        commit3Stream();
        
        commitParticipants();
        
        container().writeDirty();
        
        idSystem().commit(this);
        
    }
	
	private void commitListeners(){
        commitParentListeners(); 
        commitTransactionListeners();
    }

	private void commitParentListeners() {
		if (_systemTransaction != null) {
            parentLocalTransaction().commitListeners();
        }
	}
	
    private void commitParticipants() {
        if (parentLocalTransaction() != null) {
        	parentLocalTransaction().commitParticipants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    private void commit3Stream(){
        container().processPendingClassUpdates();
        container().writeDirty();
        container().classCollection().write(container().systemTransaction());
    }
    
	private LocalTransaction parentLocalTransaction() {
		return (LocalTransaction) _systemTransaction;
	}
    
	private void commitClearAll(){
		if(_systemTransaction != null){
            parentLocalTransaction().commitClearAll();
        }
        clearAll();
    }

	
	protected void clear() {
		idSystem().clear(this);
		disposeParticipants();
        _participants.clear();
	}
	
	private void disposeParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).dispose(this);
		}
	}
	
    public void rollback() {
        synchronized (container().lock()) {
            
            rollbackParticipants();
            
            idSystem().rollback(this);
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }
    
    private void rollbackParticipants() {
        Iterator4 iterator = _participants.valuesIterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}
	
    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        _file.syncFiles();
    }
    
	public void processDeletes() {
		if (_delete == null) {
			_writtenUpdateAdjustedIndexes = null;
			return;
		}

		while (_delete != null) {

			Tree delete = _delete;
			_delete = null;

			delete.traverse(new Visitor4() {
				public void visit(Object a_object) {
					DeleteInfo info = (DeleteInfo) a_object;
					// if the object has been deleted
					if (localContainer().isDeleted(LocalTransaction.this, info._key)) {
						return;
					}
					
					// We need to hold a hard reference here, otherwise we can get 
					// intermediate garbage collection kicking in.
					Object obj = null;  
					
					if (info._reference != null) {
						obj = info._reference.getObject();
					}
					if (obj == null || info._reference.getID() < 0) {

						// This means the object was gc'd.

						// Let's try to read it again, but this may fail in
						// CS mode if another transaction has deleted it. 

						HardObjectReference hardRef = container().getHardObjectReferenceById(
							LocalTransaction.this, info._key);
						if(hardRef == HardObjectReference.INVALID){
							return;
						}
						info._reference = hardRef._reference;
						info._reference.flagForDelete(container().topLevelCallId());
						obj = info._reference.getObject();
					}
					container().delete3(LocalTransaction.this, info._reference,
							obj, info._cascade, false);
				}
			});
		}
		_writtenUpdateAdjustedIndexes = null;
	}
	
	
	public void writeUpdateAdjustIndexes(int id, ClassMetadata clazz, ArrayType typeInfo, int cascade) {
    	new WriteUpdateProcessor(this, id, clazz, typeInfo, cascade).run();
    }
    
	private Callbacks callbacks(){
		return container().callbacks();
	}
	
	private Collection4 collectCommittedCallbackDeletedInfo() {
		final Collection4 deleted = new Collection4();
		collectCallBackInfo(new CallbackInfoCollector() {
			public void deleted(int id) {
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}

			public void updated(int id) {
			}
		
			public void added(int id) {
			}
		});
		return deleted;
	}
	
	private CallbackObjectInfoCollections collectCommittedCallbackInfo(Collection4 deleted) {
		if (! idSystem().isDirty(this)) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		final Collection4 added = new Collection4();
		final Collection4 updated = new Collection4();		
		collectCallBackInfo(new CallbackInfoCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id) {
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections collectCommittingCallbackInfo() {
		if (! idSystem().isDirty(this)) {
			return CallbackObjectInfoCollections.EMTPY;
		}
		
		final Collection4 added = new Collection4();
		final Collection4 deleted = new Collection4();
		final Collection4 updated = new Collection4();		
		collectCallBackInfo(new CallbackInfoCollector() {
			public void added(int id) {
				added.add(lazyReferenceFor(id));
			}

			public void updated(int id) {
				updated.add(lazyReferenceFor(id));
			}
			
			public void deleted(int id){
				ObjectInfo ref = frozenReferenceFor(id);
				if(ref != null){
					deleted.add(ref);
				}
			}
		});
		return newCallbackObjectInfoCollections(added, updated, deleted);
	}

	private CallbackObjectInfoCollections newCallbackObjectInfoCollections(
			final Collection4 added,
			final Collection4 updated,
			final Collection4 deleted) {
		return new CallbackObjectInfoCollections(
				new ObjectInfoCollectionImpl(added),
				new ObjectInfoCollectionImpl(updated),
				new ObjectInfoCollectionImpl(deleted));
	}

	private void collectCallBackInfo(final CallbackInfoCollector collector) {
		idSystem().collectCallBackInfo(this, collector);
	}
	
	private IdSystem idSystem() {
		return localContainer().idSystem();
	}

	public ObjectInfo frozenReferenceFor(final int id) {
		ObjectReference ref = referenceForId(id);
		if(ref != null){
			return new FrozenObjectInfo(this, ref, true);
		}
		ref = container().peekReference(systemTransaction(), id, new FixedActivationDepth(0), true);
		if(ref == null || ref.getObject() == null){
			return null;
		}
		return new FrozenObjectInfo(systemTransaction(), ref, true);
	}
	
	public LazyObjectReference lazyReferenceFor(final int id) {
		return new LazyObjectReference(LocalTransaction.this, id);
	}
	
	public Cache4<Integer, ByteArrayBuffer> slotCache(){
		return _slotCache;
	}
	
}
