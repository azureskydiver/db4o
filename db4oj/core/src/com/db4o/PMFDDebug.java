/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

/**
 * @exclude
 */
package com.db4o;

public final class PMFDDebug {
	
    private final static boolean DO_LOG=true;
    private final static boolean DO_LOG_MODIFY=true;
    private final static boolean DO_LOG_ENTER_EXIT=true;
    
    public static void logEnter(String msg,YapReader source,YapReader target) {
    	if(DO_LOG_ENTER_EXIT) {
    		log("ENTER",msg,source,target);
    	}
    }

    public static void logExit(String msg,YapReader source,YapReader target) {
    	if(DO_LOG_ENTER_EXIT) {
    		log("EXIT",msg,source,target);
    	}
    }

    public static void logModify(String msg,int oldID,int newID,YapReader source,YapReader target) {
    	if(DO_LOG_MODIFY) {
    		log("MODIFY",msg+" "+oldID+"=>"+newID,source,target);
    	}
    }

    private static void log(String header,String msg,YapReader source,YapReader target) {
    	log(header+": "+msg+" - "+renderOffsets(source, target));
    }

    public static void log(String msg) {
    	if(DO_LOG) {
    		System.out.println(msg);
    	}
    }

    private static String renderOffsets(YapReader source,YapReader target) {
    	return source._offset+"/"+target._offset;
    }

	private PMFDDebug() {}
}
