/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.selpersist;


public class Test {
		transient String transientField;
		String persistentField;

		public Test(String transientField, String persistentField)
		{
			this.transientField = transientField;
			this.persistentField = persistentField;
		}

		public String toString() 
		{
			return "Test: persistent: " + persistentField + ", transient: " + transientField ;
		}

}
