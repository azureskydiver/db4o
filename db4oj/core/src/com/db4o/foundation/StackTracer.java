/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.io.*;


/**
 * Don't use directly but use Platform4.stackTrace() for 
 * .NET compatibility.
 * @sharpen.ignore 
 * @exclude
 */
public class StackTracer {
	
	public static String stackTrace(){
		try {
			throw new Exception();
		}catch (Exception ex){
			TracingOutputStream tos = new TracingOutputStream();
			PrintWriter pw = new PrintWriter(tos, true);
			ex.printStackTrace(pw);
			return tos.stackTrace();
		}
	}
	
	static class TracingOutputStream extends OutputStream{
		
		private final StringBuffer _stringBuffer = new StringBuffer();
		
		private int _writeCalls;
		
		private static final int IGNORE_FIRST_WRITE_CALLS = 2;
		
		private static final int IGNORE_FIRST_BYTES = 3;
		
		public void write(byte[] b, int off, int len) throws IOException {
			if(_writeCalls++ < IGNORE_FIRST_WRITE_CALLS){
				return;
			}
			for (int i = off + IGNORE_FIRST_BYTES; i < off + len; i++) {
				_stringBuffer.append((char)b[i]);	
			}
		}

		public void write(int b) throws IOException {
			throw new IllegalStateException();
		}
		
		String stackTrace(){
			return _stringBuffer.toString();
		}
		
	}

}
