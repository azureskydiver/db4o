/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public abstract class Debug extends Debug4 {
    
    /**
     * indexes all fields
     */
    public static final boolean indexAllFields = false;
    
    /**
     * prints query graph information to the console
     */
    public static final boolean queries = false;

    /**
     * prints more stack traces
     */
    public static final boolean atHome = false;

    /**
     * makes C/S timeouts longer, so C/S does not time out in the debugger
     */
    public static final boolean longTimeOuts = false;

    /**
     * turns freespace debuggin on 
     */
    public static final boolean freespace = Deploy.debug;
    
    /**
     * fills deleted slots with 'X' and overrides any configured
     * freespace filler
     */
    public static final boolean xbytes = freespace;
    
    /**
     * checks monitor conditions to make sure only the thread
     * with the global monitor is allowed entry to the core
     */
    public static final boolean checkSychronization = false;
    
    /**
     * makes sure a configuration entry is generated for each persistent
     * class 
     */
    public static final boolean configureAllClasses = indexAllFields;
    
    /**
     * makes sure a configuration entry is generated for each persistent
     * field
     */
    public static final boolean configureAllFields = indexAllFields;
    
    /**
     * allows turning weak references off
     */
    public static final boolean weakReferences = true;

    /**
     * prints all communicated messages to the console
     */
    public static final boolean messages = false;

    /**
     * allows turning NIO off on Java
     */
    public static final boolean nio = true;
    
    /**
     * allows overriding the file locking mechanism to turn it off
     */
    public static final boolean lockFile = true;
    
    /**
     * allows faking the Db4oDatabase identity object, so the first
     * stored object in the debugger is the actually persisted object  
     */
    public static final boolean staticIdentity = MarshallingSpike.enabled;

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
