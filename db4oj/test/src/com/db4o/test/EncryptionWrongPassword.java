package com.db4o.test;

import com.db4o.*;

public class EncryptionWrongPassword {
	
	String name;
	
	public void storeOne() {
		name = "hi";
	}
	
	public void testOne() {
		Db4o.configure().password("wrong");
		Db4o.configure().encrypt(true);
		Test.reOpen();
		Db4o.configure().encrypt(false);

		
	}

}
