/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;

/**
 * interface for custom marshallers. 
 * Custom marshallers can be used for tuning the performance to store
 * and read objects. Instead of letting db4o do all the marshalling 
 * by detecting the fields on a class and by using reflection, a
 * custom {@link ObjectMarshaller ObjectMarshaller} allows the 
 * application developer to write the logic how the fields of an
 * object are converted to a byte[] and back.
 * <br><br>To implement a custom marshaller, write a class that
 * implements the methods of the {@link ObjectMarshaller ObjectMarshaller}
 * interface and register it for your persistent class:<br> 
 * <code>Db4o.configure().objectClass(YourClass.class).marshallWith(yourMarshaller);</code>
 */
public interface ObjectMarshaller {

	/**
	 * implement to write the values of fields to a byte[] when
	 * an object gets stored.
	 * @param obj the object that is stored
	 * @param slot the byte[] where the fields are to be written
	 * @param offset the offset position in the byte[] where the first
	 * field value can be written
	 */
	public void writeFields(Object obj, byte[] slot, int offset);

	/**
	 * implement to write the values of the marshalled 
	 * fields of an object to the when it is stored.  
	 * @param obj the object that is to be stored
	 * @return the marshalled byte[] for the fields of the object
	 */

	/**
	 * implement to read the values of fields from a byte[] and
	 * to set them on an object when the object gets instantiated
	 * @param obj the object that is instantiated
	 * @param slot the byte[] where the fields are to be read from
	 * @param offset the offset position in the byte[] where the first
	 * field value is to be read from
	 */
	public void readFields(Object obj, byte[] slot, int offset);

	/**
	 * return the length the marshalled fields will occupy in the 
	 * slot byte[]. You may not write beyond this offset when you
	 * store fields. 
	 * @return the marshalled length of the fields.
	 */
	public int marshalledFieldLength();

}
