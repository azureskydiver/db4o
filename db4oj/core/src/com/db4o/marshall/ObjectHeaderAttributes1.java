/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class ObjectHeaderAttributes1 extends ObjectHeaderAttributes{
    
    public final int _fieldCount;
    
    public BitMap4 _nullBitMap;
    
    public ObjectHeaderAttributes1(YapClass yapClass) {
        _fieldCount = yapClass.fieldCount();
        _nullBitMap = new BitMap4(_fieldCount);
    }
    
    public ObjectHeaderAttributes1(YapReader reader){
        _fieldCount = reader.readInt();
        _nullBitMap = reader.readBitMap(_fieldCount);
    }
    
    public void write(YapReader writer){
        writer.append(ObjectMarshaller1.VERSION);
        writer.writeInt(_fieldCount);
        writer.writeBitMap(_nullBitMap);
    }
    
    public int marshalledLength(){
        return YapConst.YAPINT_LENGTH + _nullBitMap.marshalledLength();
    }


}
