/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;

/**
 * @exclude
 */
class ObjectMarshaller0 extends ObjectMarshallerBase {
    
    protected int fieldLength(YapField yf, YapObject yo){
        return yf.linkLength();
    }
    
    public boolean findOffset(YapClass yc, YapReader a_bytes, YapField a_field) {
        int length = Debug.atHome ? yc.readFieldLengthSodaAtHome(a_bytes) : yc.readFieldLength(a_bytes);
        for (int i = 0; i < length; i++) {
            if (yc.i_fields[i] == a_field) {
                return true;
            }
            a_bytes.incrementOffset(yc.i_fields[i].linkLength());
        }
        if (yc.i_ancestor == null) {
            return false;
        }
        return findOffset(yc.i_ancestor, a_bytes, a_field);
    }
    
    protected final int headerLength(){
        return YapConst.OBJECT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
    public void instantiateFields(YapClass yc, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes) {
        int length = yc.readFieldLength(a_bytes);
        try {
            for (int i = 0; i < length; i++) {
                yc.i_fields[i].instantiate(a_yapObject, a_onObject, a_bytes);
            }
            if (yc.i_ancestor != null) {
                instantiateFields(yc.i_ancestor, a_yapObject, a_onObject, a_bytes);
            }
        } catch (CorruptionException ce) {
        }
    }

    
    public YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth){
        
        YapWriter writer = createWriter(a_trans, yo, a_updateDepth);
        
        YapClass yc = yo.getYapClass();
        Object obj = yo.getObject();
        
        if(yc.isPrimitive()){
            ((YapClassPrimitive)yc).marshallNew(yo, writer, obj);
        }else{
            writer.writeInt(yc.getID());
            yc.checkUpdateDepth(writer);
            marshall(yc, yo, obj, writer, true);
        }

        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
        
        return writer;
    }


    public void marshallUpdate(
        Transaction a_trans,
        YapClass yapClass,
        int a_id,
        int a_updateDepth,
        YapObject a_yapObject,
        Object a_object
        ) {

        int length = yapClass.objectLength();
        
        YapWriter writer = createWriter(a_trans, a_updateDepth, a_id, 0, length);
        
        yapClass.checkUpdateDepth(writer);
        
        writer.writeInt(yapClass.getID());
        marshall(yapClass, a_yapObject, a_object, writer, false);
        
        marshallUpdateWrite(a_trans, yapClass, a_yapObject, a_object, writer);
    }


    private void marshall(YapClass yapClass, YapObject a_yapObject, Object a_object, YapWriter a_bytes, boolean a_new) {
        Config4Class config = yapClass.configOrAncestorConfig();
        a_bytes.writeInt(yapClass.i_fields.length);
        for (int i = 0; i < yapClass.i_fields.length; i++) {
            Object obj = yapClass.i_fields[i].getOrCreate(a_bytes.getTransaction(), a_object);
            if (obj instanceof Db4oTypeImpl) {
                obj = ((Db4oTypeImpl)obj).storedTo(a_bytes.getTransaction());
            }
            yapClass.i_fields[i].marshall(a_yapObject, obj, a_bytes, config, a_new);
        }
        if (yapClass.i_ancestor != null) {
            marshall(yapClass.i_ancestor, a_yapObject, a_object, a_bytes, a_new);
        }
    }




}
