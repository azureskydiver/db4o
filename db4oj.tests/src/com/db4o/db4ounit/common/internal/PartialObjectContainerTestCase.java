/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;


import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
public class PartialObjectContainerTestCase
	extends AbstractDb4oTestCase
	implements OptOutTA {
    
    public static void main(String[] arguments) {
        new PartialObjectContainerTestCase().runSolo();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.blockSize(8);
    }
    
    public void testBlocksToBytes(){
        int[] blocks = new int[]{0, 1, 8, 9};
        int[] bytes  = new int[]{0, 8, 64, 72};
        
        for (int i = 0; i < blocks.length; i++) {
            Assert.areEqual(bytes[i], localContainer().blocksToBytes(blocks[i]));
        }
    }
    
    public void testBytesToBlocks(){
        int[] bytes  = new int[]{0, 1, 2, 7, 8, 9, 16, 17, 799, 800, 801};
        int[] blocks = new int[]{0, 1, 1, 1, 1, 2,  2,  3, 100, 100, 101};
        
        for (int i = 0; i < blocks.length; i++) {
            Assert.areEqual(blocks[i], localContainer().bytesToBlocks(bytes[i]));
        }
    }

    private ObjectContainerBase localContainer() {
        return stream().container();
    }
}