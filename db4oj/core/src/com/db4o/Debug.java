/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public abstract class Debug extends Debug4 {
    
    public static final boolean indexAllFields = false;
    
    public static final boolean queries = false;

    public static final boolean atHome = false;

    public static final boolean longTimeOuts = false;

    public static final boolean freespace = Deploy.debug;
    
    // overrides any configured freespace filler
    public static final boolean xbytes = freespace;
    
    public static final boolean checkSychronization = false;
    
    public static final boolean configureAllClasses = indexAllFields;
    public static final boolean configureAllFields = indexAllFields;
    
    public static final boolean weakReferences = true;

    public static final boolean fakeServer = false;
    
    public static final boolean messages = false;

    public static final boolean nio = true;
    
    public static final boolean lockFile = true;

	public static void expect(boolean cond){
        if(! cond){
            throw new RuntimeException("Should never happen");
        }
    }
    
    public static void ensureLock(Object obj) {
        if (atHome) {
            try {
                obj.wait(1);
            } catch (IllegalMonitorStateException imse) {
                System.err.println("No Lock Alarm.");
                imse.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean exceedsMaximumBlockSize(int a_length) {
        if (a_length > Const4.MAXIMUM_BLOCK_SIZE) {
            if (atHome) {
                System.err.println("Maximum block size  exceeded!!!");
                new Exception().printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    public static boolean exceedsMaximumArrayEntries(int a_entries, boolean a_primitive){
        if (a_entries > (a_primitive ? Const4.MAXIMUM_ARRAY_ENTRIES_PRIMITIVE : Const4.MAXIMUM_ARRAY_ENTRIES)) {
            if (atHome) {
                System.err.println("Maximum array elements exceeded!!!");
                new Exception().printStackTrace();
            }
            return true;
        }
        return false;
    }
}
