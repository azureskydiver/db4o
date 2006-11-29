/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import com.db4o.*;
import com.db4o.cs.messages.*;
import com.db4o.foundation.*;

final class TransactionClient extends Transaction {

    private final YapClient i_client;
    
    private Tree i_yapObjectsToGc;
    
    TransactionClient(YapClient a_stream, Transaction a_parent) {
        super(a_stream, a_parent);
        i_client = a_stream;
    }
    
    public void commit() {
        commitTransactionListeners();
        if(i_yapObjectsToGc != null){
            i_yapObjectsToGc.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    YapObject yo = (YapObject)((TreeIntObject) a_object)._object;
                    stream().removeReference(yo);
                }
            });
        }
        i_yapObjectsToGc = null;
        i_client.writeMsg(Msg.COMMIT);
    }

    public boolean delete(YapObject ref, int id, int cascade) {
        if (! super.delete(ref, id, cascade)){
        	return false;
        }
        i_client.writeMsg(Msg.TA_DELETE.getWriterForInts(this, new int[] {id, cascade}));
        return true;
    }

    public boolean isDeleted(int a_id) {

        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        
        i_client.writeMsg(Msg.TA_IS_DELETED.getWriterForInt(this, a_id));
        int res = i_client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
    }
    
    public Object[] objectAndYapObjectBySignature(final long a_uuid, final byte[] a_signature) {
        int messageLength = YapConst.LONG_LENGTH + YapConst.INT_LENGTH + a_signature.length;
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
        i_client.writeMsg(Msg.PROCESS_DELETES);
    }
    
    public void rollback() {
        i_yapObjectsToGc = null;
        rollBackTransactionListeners();
    }

    public void writeUpdateDeleteMembers(int a_id, YapClass a_yc, int a_type,
        int a_cascade) {
        i_client.writeMsg(Msg.WRITE_UPDATE_DELETE_MEMBERS.getWriterForInts(this,
            new int[]{
            a_id,
            a_yc.getID(),
            a_type,
            a_cascade
        }));
    }
}