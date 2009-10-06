/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
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

    public boolean isDeleted(int a_id) {

        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        MsgD msg = Msg.TA_IS_DELETED.getWriterForInt(this, a_id);
		_client.write(msg);
        int res = _client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
    }
    
    public final HardObjectReference getHardReferenceBySignature(final long a_uuid, final byte[] a_signature) {
        int messageLength = Const4.LONG_LENGTH + Const4.INT_LENGTH + a_signature.length;
        MsgD message = Msg.OBJECT_BY_UUID.getWriterForLength(this, messageLength);
        message.writeLong(a_uuid);
        message.writeInt(a_signature.length);
        message.writeBytes(a_signature);
        _client.write(message);
        message = (MsgD)_client.expectedResponse(Msg.OBJECT_BY_UUID);
        int id = message.readInt();
        if(id > 0){
            return container().getHardObjectReferenceById(this, id);
        }
        return HardObjectReference.INVALID;
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
        _objectRefrencesToGC = null;
        rollBackTransactionListeners();
        clearAll();
    }

    public void writeUpdateAdjustIndexes(int id, ClassMetadata classMetadata, ArrayType arrayType,
        int cascade) {
    	// do nothing
    }

}