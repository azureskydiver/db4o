/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;


public class TAIntItem extends ActivatableImpl {
		public int value;
		
		public Object obj;
		
		public Integer i;

		public TAIntItem() {

		}
		
		public int value() {
			activate();
			return value;
		}
		
		public Integer integerValue() {
			activate();
			return i;
		}
		
		public Object object() {
			activate();
			return obj;
		}
	
	}