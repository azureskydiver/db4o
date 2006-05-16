/* Copyright (C) 2bitbit4 - 2bitbit6  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.foundation;

import com.db4o.foundation.*;
import com.db4o.test.*;


public class BitMap4TestCase {
    
    public void test(){
        
        byte[] buffer = new byte[100];

        
        for (int i = 0; i < 17; i++) {
            BitMap4 map = new BitMap4(i);
            
            map.writeTo(buffer, 11);
            
            BitMap4 reReadMap = new BitMap4(buffer,11, i);
            
            for (int j = 0; j < i; j++) {
                tBit(map, j);
                tBit(reReadMap, j);
            }
        }
        
    }
    
    private void tBit(BitMap4 map, int bit){
        map.setTrue(bit);
        Test.ensure(map.isTrue(bit));
        map.setFalse(bit);
        Test.ensure(! map.isTrue(bit));
        map.setTrue(bit);
        Test.ensure(map.isTrue(bit));
        
    }

}
