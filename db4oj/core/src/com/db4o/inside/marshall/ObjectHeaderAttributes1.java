/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class ObjectHeaderAttributes1 extends ObjectHeaderAttributes{
    
    private static final byte VERSION = (byte)1;
    
    private final int _fieldCount;
    
    private final BitMap4 _nullBitMap;
    
    private int _baseLength;
    
    private int _payLoadLength;
    
    
    public ObjectHeaderAttributes1(YapObject yo) {
        _fieldCount = yo.getYapClass().fieldCount();
        _nullBitMap = new BitMap4(_fieldCount);
        calculateLengths(yo);
    }
    
    public ObjectHeaderAttributes1(YapReader reader){
        _fieldCount = reader.readInt();
        _nullBitMap = reader.readBitMap(_fieldCount);
    }
    
    public void addBaseLength(int length){
        _baseLength += length;
    }
    
    public void addPayLoadLength(int length){
        _payLoadLength += length;
    }
    
    private void calculateLengths(YapObject yo) {
        _baseLength = headerLength() + nullBitMapLength();
        _payLoadLength = 0;
        YapClass yc = yo.getYapClass();
        Transaction trans = yo.getTrans();
        Object obj = yo.getObject();
        calculateLengths(trans, yc, obj, 0);
        _baseLength = yo.getStream().alignToBlockSize(_baseLength);        
    }
    
    private void calculateLengths(Transaction trans, YapClass yc, Object obj, int fieldIndex) {
        _baseLength += YapConst.INT_LENGTH;
        if (yc.i_fields != null) {
            for (int i = 0; i < yc.i_fields.length; i++) {
                YapField yf = yc.i_fields[i];
                Object child = yf.getOrCreate(trans, obj);
                if( child == null && yf.canUseNullBitmap()){
                    _nullBitMap.setTrue(fieldIndex);
                }else{
                    yf.calculateLengths(trans, this, child);
                }
                fieldIndex ++;
            }
        }
        if (yc.i_ancestor == null) {
            return;
        }
        calculateLengths(trans, yc.i_ancestor, obj, fieldIndex);
    }

    private int headerLength(){
        return YapConst.OBJECT_LENGTH 
            + YapConst.ID_LENGTH  // YapClass ID 
            + 1; // Marshaller Version 
    }
    
    public boolean isNull(int fieldIndex){
        return _nullBitMap.isTrue(fieldIndex);
    }
    
    private int nullBitMapLength(){
        return YapConst.INT_LENGTH + _nullBitMap.marshalledLength();
    }

    public int objectLength(){
        return _baseLength + _payLoadLength;
    }
    
    public void prepareIndexedPayLoadEntry(Transaction trans){
        _payLoadLength =  trans.stream().alignToBlockSize(_payLoadLength);
    }
    
    public void write(YapWriter writer){
        writer.append(VERSION);
        writer.writeInt(_fieldCount);
        writer.writeBitMap(_nullBitMap);
        writer._payloadOffset = _baseLength;
    }

}
