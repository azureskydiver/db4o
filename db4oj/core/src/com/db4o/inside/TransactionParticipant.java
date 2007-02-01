package com.db4o.inside;


/**
 * @exclude
 */
public interface TransactionParticipant {	

	void commit(Transaction transaction);

	void rollback(Transaction transaction);
	
	void dispose(Transaction transaction);
}
