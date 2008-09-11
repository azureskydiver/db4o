/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

/**
 * interface to configure the string encoding to be used.
 * <br><br>All methods need to be called <b>before</b> a database file
 * is created with the first 
 * {@link com.db4o.Db4o#openFile} or  {@link com.db4o.Db4o#openServer}.
 * db4o database files keep their string format after creation.<br><br>
 * By default unicode encoding will be used.  
 */
public interface StringEncodingConfiguration {
	
	/**
	 * configures db4o to use a UTF-8 string encoding for all strings 
	 */
	public void useUtf8();
	
	/**
	 * configures db4o to use a unicode encoding for all strings.
	 */
	public void useUnicode();

	/**
	 * configures db4o to use a latin (ISO 8859-1) string encoding for all strings 
	 */
	public void useLatin();

}
