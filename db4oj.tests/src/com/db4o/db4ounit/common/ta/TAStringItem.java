/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;


public class TAStringItem extends ActivatableImpl {
		public String value;
		
		public Object obj;

		public LinkedList list;

		public TAStringItem() {

		}
		
		public String value() {
			activate();
			return value;
		}
		
		public Object object() {
			activate();
			return obj;
		}
		
		public LinkedList list() {
			activate();
			return list;
		}
	}