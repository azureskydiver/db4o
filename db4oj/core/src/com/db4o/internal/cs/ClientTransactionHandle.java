/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

public class ClientTransactionHandle {
    private final ClientTransactionPool _transactionPool;
    private Transaction _mainTransaction;
	private LocalObjectContainer _stream;
    private Transaction _transaction;
    private boolean _rollbackOnClose;
	
    public ClientTransactionHandle(ClientTransactionPool transactionPool) {
		_transactionPool = transactionPool;
        _mainTransaction = _transactionPool.acquireMain();
		_rollbackOnClose = false;
	}

    public void acquireTransactionForFile(String fileName) {
        _transaction = _transactionPool.acquire(fileName);
	}
	
    public void releaseTransaction() {
		if (_transaction != null) {
			_transactionPool.release(_transaction, _rollbackOnClose);
			_transaction = null;
		}
	}
	
    public void write(Msg message, Socket4 socket) {
    	if(_stream != null) {
    		message.write(_stream, socket);
    	}
    	else {
    		_transactionPool.write(message, socket);
    	}
	}

    public boolean isClosed() {
		return _transactionPool.isClosed();
	}
    
    public void close() {
		if ((!_transactionPool.isClosed()) && (_mainTransaction != null)) {
            _mainTransaction.close(_rollbackOnClose);
        }
	}
	
    public Object lock() {
		return _transactionPool.streamLock();
	}

    public Transaction transaction() {
        if (_transaction != null) {
            return _transaction;
        }
        return _mainTransaction;
    }

    public void transaction(Transaction transaction) {
		if (_transaction != null) {
			_transaction = transaction;
		} else {
			_mainTransaction = transaction;
		}
		_rollbackOnClose = false;
    }

}
