/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class TransactionClient extends Transaction {

    private final YapClient i_client;
    private Tree i_yapObjectsToGc; 
    

    TransactionClient(YapClient a_stream, Transaction a_parent) {
        super(a_stream, a_parent);
        i_client = a_stream;
    }

    void beginEndSet() {
        if (i_delete != null) {
            i_delete.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    TreeIntObject tio = (TreeIntObject) a_object;
                    if (tio.i_object != null) {
                        Object[] arr = (Object[]) tio.i_object;
                        YapObject yo = (YapObject) arr[0];
                        i_yapObjectsToGc = Tree.add(i_yapObjectsToGc, new TreeIntObject(yo.getID(), yo));
                    }
                }
            });
        }
        i_delete = null;
        i_writtenUpdateDeletedMembers = null;
        i_client.writeMsg(Msg.TA_BEGIN_END_SET);
    }

    void commit() {
        commitTransactionListeners();
        if(i_yapObjectsToGc != null){
            i_yapObjectsToGc.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    YapObject yo = (YapObject)((TreeIntObject) a_object).i_object;
                    i_stream.yapObjectGCd(yo);
                }
            });
        }
        i_yapObjectsToGc = null;
        i_client.writeMsg(Msg.COMMIT);
    }

    void delete(YapObject a_yo, Object a_object, int a_cascade,
        boolean a_deleteMembers) {
        super.delete(a_yo, a_object, a_cascade, false);
        i_client.writeMsg(Msg.TA_DELETE.getWriterFor2Ints(this, a_yo.getID(), a_cascade));
    }

    void dontDelete(int a_id, boolean a_deleteMembers) {
        super.dontDelete(a_id, false);
        i_client.writeMsg(Msg.TA_DONT_DELETE.getWriterForInt(this, a_id));
    }

    boolean isDeleted(int a_id) {

        // This one really is a hack.
        // It only helps to get information about the current
        // transaction.

        // We need a better strategy for C/S concurrency behaviour.
        
        i_client.writeMsg(Msg.TA_IS_DELETED.getWriterForInt(this, a_id));
        int res = i_client.expectedByteResponse(Msg.TA_IS_DELETED).readInt();
        return res == 1;
    }
    
    Object[] objectAndYapObjectBySignature(final long a_uuid, final byte[] a_signature) {
        int messageLength = YapConst.YAPLONG_LENGTH + YapConst.YAPINT_LENGTH + a_signature.length;
        MsgD message = Msg.OBJECT_BY_UUID.getWriterForLength(this, messageLength);
        message.writeLong(a_uuid);
        message.writeBytes(a_signature);
        i_client.writeMsg(message);
        message = (MsgD)i_client.expectedResponse(Msg.OBJECT_BY_UUID);
        int id = message.readInt();
        if(id > 0){
            return i_stream.getObjectAndYapObjectByID(this, id);
        }
        return new Object[2];
    }
    
    public void rollback() {
        i_yapObjectsToGc = null;
        rollBackTransactionListeners();
    }

    void writeUpdateDeleteMembers(int a_id, YapClass a_yc, int a_type,
        int a_cascade) {
        i_client.writeMsg(Msg.WRITE_UPDATE_DELETE_MEMBERS.getWriterFor4Ints(this, a_id,
            a_yc.getID(), a_type, a_cascade));
    }
}