/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class MRollback extends Msg {
	final boolean processMessageAtServer(YapSocket sock) {
		this.getTransaction().rollback();
		return true;
	}
}