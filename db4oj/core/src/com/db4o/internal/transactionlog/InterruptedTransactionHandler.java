/* Copyright (C) 2009 Versant Corporation http://www.db4o.com */

package com.db4o.internal.transactionlog;

/**
 * @exclude
 */
public interface InterruptedTransactionHandler {

	public void completeInterruptedTransaction();

}
