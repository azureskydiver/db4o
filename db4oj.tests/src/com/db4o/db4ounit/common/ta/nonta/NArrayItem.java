/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.*;


public class NArrayItem {
		
		public int[][] value;
		
		public Object obj;
		
		public LinkedList[][] lists;

		public Object listsObject;

		public NArrayItem() {

		}
		
		public int[][] value() {
			return value;
		}
		
		public Object object() {
			return obj;
		}
		
		public LinkedList[][] lists() {
			return lists;
		}
		
		public Object listsObject() {
			return listsObject;
		}
	}