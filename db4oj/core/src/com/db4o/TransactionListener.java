/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * allows registration with a transaction to be notified of
 * commit and rollback 
 */
interface TransactionListener {
    
    public void preCommit();
    public void postRollback();
    
}
