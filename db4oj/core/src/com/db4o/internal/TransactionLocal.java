package com.db4o.internal;

/**
 * A transaction local variable.
 * 
 * @see Transaction#get(TransactionLocal)
 * @param <T>
 */
public class TransactionLocal<T> {
	public T initialValueFor(Transaction transaction) {
		return null;
	}
}
