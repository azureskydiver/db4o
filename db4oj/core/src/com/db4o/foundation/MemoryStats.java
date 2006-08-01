/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public class MemoryStats {

	public static long usedMemory() {
        Runtime rt = Runtime.getRuntime();
        while (true) {
            long memory1 = rt.freeMemory();    
            System.gc();
            System.runFinalization();
            long memory2 = rt.freeMemory();
            if (memory2 >= memory1) {
                break;
            }
        }
        return rt.totalMemory() - rt.freeMemory();
	}

}
