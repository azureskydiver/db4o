/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.comparing;


public class Record {
	private MyString _record;
	
	
	public Record(String record) {
		_record = new MyString(record);
	}
	
	public String toString(){
		return _record.toString();
	}
}
