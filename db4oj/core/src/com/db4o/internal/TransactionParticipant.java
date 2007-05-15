/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.internal;

/**
 * @exclude
 */
public interface TransactionParticipant {	

	void commit(Transaction transaction);

	void rollback(Transaction transaction);
	
	void dispose(Transaction transaction);
}
