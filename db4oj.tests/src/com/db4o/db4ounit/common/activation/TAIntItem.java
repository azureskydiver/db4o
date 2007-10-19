/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.activation;

import com.db4o.db4ounit.common.ta.*;

public class TAIntItem extends ActivatableImpl {
		public int value;

		public LinkedList list;

		public TAIntItem() {

		}
		
		public int getValue() {
			activate();
			return value;
		}
	}