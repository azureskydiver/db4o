/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

final class ClientTransaction extends Transaction {

    private final ClientObjectContainer i_client;
    
    private Tree i_yapObjectsToGc;
    
    ClientTransaction(ClientObjectContainer a_stream, Transaction a_parent) {
        super(a_stream, a_parent);
        i_client = a_stream;
    }
    
    public void commit() {
    	commitTransactionListeners();
        clearAll();
        i_client.writeMsg(Msg.COMMIT, true); 
    }
    
    protected void clearAll() {
    	removeYapObjectReferences();
    	super.clearAll();
    }

	private void removeYapObjectReferences() {
		if(i_yapObjectsToGc != null){
            i_yapObjectsToGc.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    ObjectReference yo = (ObjectReference)((TreeIntObject) a_object)._object;
                    stream().removeReference(yo);
                }
            });
        }
        i_yapObjectsToGc = null;
	}

    public boolean delete(ObjectReference ref, int id, int cascade) {
        if (! super.delete(ref, id, cascade)){
        	return false;
        }
        MsgD msg = Msg.TA_DELETE.getWriterForInts(this, new int[] {id, cascade});
        i_client.writeMsg(msg, false);
        return true;
    }

    public boolean isDeleted(int a_id) {

        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        MsgD msg = Msg.TA_IS_DELETED.getWriterForInt(this, a_id);
		i_client.writeMsg(msg, true);
        int res = i_client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
    }
    
    public Object[] objectAndYapObjectBySignature(final long a_uuid, final byte[] a_signature) {
        int messageLength = Const4.LONG_LENGTH + Const4.INT_LENGTH + a_signature.length;
        MsgD message = Msg.OBJECT_BY_UUID.getWriterForLength(this, messageLength);
        message.writeLong(a_uuid);
        message.writeBytes(a_signature);
        i_client.writeMsg(message);
        message = (MsgD)i_client.expectedResponse(Msg.OBJECT_BY_UUID);
        int id = message.readInt();
        if(id > 0){
            return stream().getObjectAndYapObjectByID(this, id);
        }
        return new Object[2];
    }
    
    
    public void processDeletes() {
        if (i_delete != null) {
            i_delete.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    DeleteInfo info = (DeleteInfo) a_object;
                    if (info._reference != null) {
                        i_yapObjectsToGc = Tree.add(i_yapObjectsToGc, new TreeIntObject(info._key, info._reference));
                    }
                }
            });
        }
        i_delete = null;
        i_writtenUpdateDeletedMembers = null;
		i_client.writeMsg(Msg.PROCESS_DELETES, false);
    }
    
    public void rollback() {
        i_yapObjectsToGc = null;
        rollBackTransactionListeners();
        clearAll();
    }

    public void writeUpdateDeleteMembers(int a_id, ClassMetadata a_yc, int a_type,
        int a_cascade) {
    	MsgD msg = Msg.WRITE_UPDATE_DELETE_MEMBERS.getWriterForInts(this,
				new int[] { a_id, a_yc.getID(), a_type, a_cascade });
		i_client.writeMsg(msg, false);
    }

	public void setPointer(int a_id, int a_address, int a_length) {
	}
}