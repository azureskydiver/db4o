/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;


public class TAArrayItem extends ActivatableImpl {
		
		public int[] value;
		
		public Object obj;
		
		public LinkedList[] lists;

		public Object listsObject;

		public TAArrayItem() {

		}
		
		public int[] value() {
			activate();
			return value;
		}
		
		public Object object() {
			activate();
			return obj;
		}
		
		public LinkedList[] lists() {
			activate();
			return lists;
		}
		
		public Object listsObject() {
			activate();
			return listsObject;
		}
	}