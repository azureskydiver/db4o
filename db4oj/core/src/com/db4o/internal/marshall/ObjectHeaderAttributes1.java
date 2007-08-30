/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class ObjectHeaderAttributes1 extends ObjectHeaderAttributes{
    
    private static final byte VERSION = (byte)1;
    
    private final int _fieldCount;
    
    private final BitMap4 _nullBitMap;
    
    private int _baseLength;
    
    private int _payLoadLength;
    
    
    public ObjectHeaderAttributes1(Buffer reader){
        _fieldCount = reader.readInt();
        _nullBitMap = reader.readBitMap(_fieldCount);
    }
    
    public void addBaseLength(int length){
        _baseLength += length;
    }
    
    public void addPayLoadLength(int length){
        _payLoadLength += length;
    }
    
    public boolean isNull(int fieldIndex){
        return _nullBitMap.isTrue(fieldIndex);
    }
    
    public int objectLength(){
        return _baseLength + _payLoadLength;
    }
    
    public void prepareIndexedPayLoadEntry(Transaction trans){
        _payLoadLength =  trans.container().blockAlignedBytes(_payLoadLength);
    }
    
    public void write(StatefulBuffer writer){
        writer.writeByte(VERSION);
        writer.writeInt(_fieldCount);
        writer.writeBitMap(_nullBitMap);
        writer._payloadOffset = _baseLength;
    }

}
