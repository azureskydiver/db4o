/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.ta;

import com.db4o.db4ounit.common.ta.*;


public class TALinkedListItem extends ActivatableImpl {
		
		public LinkedList list;

		public TALinkedListItem() {

		}
		
		public LinkedList list() {
			activate();
			return list;
		}
	}