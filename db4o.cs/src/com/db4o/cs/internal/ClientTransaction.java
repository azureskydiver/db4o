/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;

public final class ClientTransaction extends Transaction {

    private final ClientObjectContainer _client;
    
    protected Tree _objectRefrencesToGC;
    
    ClientTransaction(ClientObjectContainer container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
        super(container, parentTransaction, referenceSystem);
        _client = container;
    }
    
    public void commit() {
    	commitTransactionListeners();
        clearAll();
        if(isSystemTransaction()){
        	_client.write(Msg.COMMIT_SYSTEMTRANS);
        }else{
        	_client.write(Msg.COMMIT);
        	_client.expectedResponse(Msg.OK);
        }
    }
    
    protected void clear() {
    	removeObjectReferences();
    }

	private void removeObjectReferences() {
		if(_objectRefrencesToGC != null){
            _objectRefrencesToGC.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ObjectReference yo = (ObjectReference)((TreeIntObject) a_object)._object;
                    ClientTransaction.this.removeReference(yo);
                }
            });
        }
        _objectRefrencesToGC = null;
	}

    public boolean delete(ObjectReference ref, int id, int cascade) {
        if (! super.delete(ref, id, cascade)){
        	return false;
        }
        MsgD msg = Msg.TA_DELETE.getWriterForInts(this, new int[] {id, cascade});
        _client.writeBatchedMessage(msg);
        return true;
    }

    public void processDeletes() {
        Visitor4 deleteVisitor = new Visitor4() {
            public void visit(Object a_object) {
                DeleteInfo info = (DeleteInfo) a_object;
                if (info._reference != null) {
                    _objectRefrencesToGC = Tree.add(_objectRefrencesToGC, new TreeIntObject(info._key, info._reference));
                }
            }
        };
        traverseDelete(deleteVisitor);
		_client.writeBatchedMessage(Msg.PROCESS_DELETES);
    }

    public void rollback() {
    	synchronized (container().lock()) {
	        _objectRefrencesToGC = null;
	        rollBackTransactionListeners();
	        clearAll();
    	}
    }

    public void writeUpdateAdjustIndexes(int id, ClassMetadata classMetadata, ArrayType arrayType) {
    	// do nothing
    }

	@Override
	public TransactionalIdSystem idSystem() {
		return null;
	}

	@Override
	public long versionForId(int id) {
        MsgD msg = Msg.VERSION_FOR_ID.getWriterForInt(systemTransaction(), id);
		_client.write(msg);
        return _client.expectedBufferResponse(Msg.VERSION_FOR_ID).readLong();
	}
	
	public long generateTransactionTimestamp(long forcedTimeStamp){
		_client.writeMsg(Msg.GENERATE_TRANSACTION_TIMESTAMP.getWriterForLong(this, forcedTimeStamp), true);
		return _client.expectedBufferResponse(Msg.GENERATE_TRANSACTION_TIMESTAMP).readLong();		
	}
	
	public void useDefaultTransactionTimestamp(){
		_client.writeMsg(Msg.USE_DEFAULT_TRANSACTION_TIMESTAMP, true);
	}

}