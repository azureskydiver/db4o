/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.Deploy;


/**
 * @exclude 
 */
public class Unobfuscated {
    
    static Object random;
    
	public static boolean createDb4oList(Object a_stream){
	    ((ObjectContainerBase)a_stream).checkClosed();
	    return ! ((ObjectContainerBase)a_stream).isInstantiating();
	}
	
	public static byte[] generateSignature() {
	    // TODO: We could add part of the file name to improve 
	    //       signature security.
	    StatefulBuffer writer = new StatefulBuffer(null, 300);
	    if(! Deploy.csharp) {
		    try {
	            new LatinStringIO().write(writer, java.net.InetAddress.getLocalHost().getHostName());
	            writer.append((byte)0);
	            writer.append(java.net.InetAddress.getLocalHost().getAddress());
	        } catch (Exception e) {
	        }
	    }
        writer.writeLong(System.currentTimeMillis());
        writer.writeLong(randomLong());
        writer.writeLong(randomLong() + 1);
        return writer.getWrittenBytes();
	}
	
	static void purgeUnsychronized(Object a_stream, Object a_object){
	    ((ObjectContainerBase)a_stream).purge1(a_object);
	}
	
	public static long randomLong() {
	    if(Deploy.csharp) {
	        // TODO: route to .NET implementation
	        return System.currentTimeMillis();
	    }
        if(random == null){
            random = new java.util.Random();
        }
        return ((java.util.Random)random).nextLong();
	}
	
	static void shutDownHookCallback(Object a_stream){
		((ObjectContainerBase)a_stream).failedToShutDown();
	}


}
