/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.net.*;
import java.util.*;


/**
 * @exclude
 * @sharpen.ignore
 */
public class SignatureGenerator {
	
	private static final Random _random = new Random();
	
	private static int _counter;
	
	public static String generateSignature() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
		}
		int hostAddress = 0;
		byte[] addressBytes;
		try {
			addressBytes = java.net.InetAddress.getLocalHost().getAddress();
			for (int i = 0; i < addressBytes.length; i++) {
				hostAddress <<= 4;
				hostAddress -= addressBytes[i];
			}
		} catch (UnknownHostException e) {
		}
		sb.append(Integer.toHexString(hostAddress));
		sb.append(Long.toHexString(System.currentTimeMillis()));
		sb.append(pad(Integer.toHexString(randomInt())));
		sb.append(Integer.toHexString(_counter++));
		return sb.toString();
	}

	private static int randomInt() {
		return _random.nextInt();
	}
	
	private static String pad(String str){
		return (str + "XXXXXXXX").substring(0, 8);
	}


}
