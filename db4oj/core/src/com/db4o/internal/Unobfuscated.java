/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.util.Random;

import com.db4o.Deploy;


/**
 * @exclude 
 */
public class Unobfuscated {
    
    private static final Random _random = new Random();
    
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
	
	public static long randomLong() {
        return _random.nextLong();
	}
}
