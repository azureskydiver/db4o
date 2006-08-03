package com.db4o;

/**
 * @exclude
 */
public interface TransactionParticipant {	

	void commit(Transaction transaction);
	
	void dispose(Transaction transaction);

}
