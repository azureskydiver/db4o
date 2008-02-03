/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4odoc.typehandler;

import com.db4o.foundation.PreparedComparison;
import com.db4o.internal.DefragmentContext;
import com.db4o.internal.DeleteContext;
import com.db4o.internal.TypeHandler4;
import com.db4o.marshall.ReadBuffer;
import com.db4o.marshall.ReadContext;
import com.db4o.marshall.WriteBuffer;
import com.db4o.marshall.WriteContext;


public class StringBufferHandler implements TypeHandler4  {

	public StringBufferHandler() {
		
	}

	
	public void delete(DeleteContext context) {
		context.readSlot();
	}
	// end delete


	private final int compare(StringBuffer a_compare, StringBuffer a_with) {
		if (a_compare == null) {
			if (a_with == null) {
				return 0;
			}
			return -1;
		}
		if (a_with == null) {
			return 1;
		}
		char c_compare[] = new char[a_compare.length()];
		a_compare.getChars(0, a_compare.length() - 1, c_compare, 0);
		char c_with[] = new char[a_with.length()];
		a_with.getChars(0, a_with.length() - 1, c_with, 0);
		
		return compareChars(c_compare, c_with);
	}
	// end compare

	private static final int compareChars(char[] compare, char[] with) {
		int min = compare.length < with.length ? compare.length : with.length;
		for (int i = 0; i < min; i++) {
			if (compare[i] != with[i]) {
				return compare[i] - with[i];
			}
		}
		return compare.length - with.length;
	}
	// end compareChars

	
	public void write(WriteContext context, Object obj) {
		String str = ((StringBuffer)obj).toString();
		WriteBuffer buffer = context;
		buffer.writeInt(str.length());
		writeToBuffer(buffer, str);
	}
	// end write

	private static void writeToBuffer(WriteBuffer buffer, String str){
	    final int length = str.length();
	    char[] chars = new char[length];
	    str.getChars(0, length, chars, 0);
	    for (int i = 0; i < length; i ++){
	        buffer.writeByte((byte) (chars[i] & 0xff));
	        buffer.writeByte((byte) (chars[i] >> 8));
		}
	}
	// end writeToBuffer


	private static String readBuffer(ReadBuffer buffer, int length){
	    char[] chars = new char[length];
		for(int ii = 0; ii < length; ii++){
			chars[ii] = (char)((buffer.readByte() & 0xff) | ((buffer.readByte() & 0xff) << 8));
		}
		return new String(chars, 0, length);
	}
	// end readBuffer

	public Object read(ReadContext context) {
		ReadBuffer buffer = context;
		String str = "";
		buffer.readInt();
		buffer.readInt();
		int length = buffer.readInt();
		if (length > 0) {
			str = readBuffer(buffer, length);
		}
		return new StringBuffer(str);
	}
	// end read

	public void defragment(DefragmentContext context) {
		// To stay compatible with the old marshaller family
		// In the marshaller family 0 number 4 represented
		// length required to store ID and object length information
		context.incrementOffset(4);
	}
	// end defragment

	public PreparedComparison prepareComparison(final Object obj) {
		return new PreparedComparison() {
			public int compareTo(Object target) {
				return compare((StringBuffer)obj, (StringBuffer)target);
			}
		};
	}
	// end prepareComparison

}
