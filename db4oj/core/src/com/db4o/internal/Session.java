/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;


final class Session
{
	final String			i_fileName;
	ObjectContainerBase				i_stream;
	private int				i_openCount;
	
	Session(String a_fileName){
		i_fileName = a_fileName;
	}
	
	/**
	 * returns true, if session is to be closed completely
	 */
	boolean closeInstance(){
		i_openCount --;
		return i_openCount < 0;
	}
	
	/**
	 * Will raise an exception if argument class doesn't match this class - violates equals() contract in favor of failing fast.
	 */
	public boolean equals(Object obj){
		if(this==obj) {
			return true;
		}
		if(null==obj) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			Exceptions4.shouldNeverHappen();
		}
		return i_fileName.equals(((Session)obj).i_fileName);
	}
	
	public int hashCode() {
		return i_fileName.hashCode();
	}
	
	String fileName(){
		return i_fileName;
	}
	
	ObjectContainerBase subSequentOpen(){
		if( i_stream.isClosed()){
			return null;
		}
		i_openCount ++;
		return i_stream;
	}
	

	
}
