/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

class ClientTransactionHandle {
	private final LocalObjectContainer _mainStream;
    private Transaction _mainTransaction;
	private LocalObjectContainer _stream;
    private Transaction _transaction;
    private boolean _rollbackOnClose;
	
	ClientTransactionHandle(LocalObjectContainer mainStream) {
		_mainStream = mainStream;
        _mainTransaction = mainStream.newTransaction();
		_rollbackOnClose = false;
	}

	void acquireTransactionForFile(String fileName) {
		_stream = (LocalObjectContainer) Db4o.openFile(fileName);
        _stream.configImpl().setMessageRecipient(_mainStream.configImpl().messageRecipient());
        _transaction = _stream.newTransaction();
	}
	
	void releaseTransaction() {
        if (_stream != null) {
            if (_transaction != null) {
                _transaction.close(_rollbackOnClose);
                _transaction = null;
            }
            try {
                _stream.close();

            } catch (Exception e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
            }
            _stream = null;
        }
	}
	
	void write(Msg message, Socket4 socket) {
    	message.write(stream(), socket);
	}

	boolean isClosed() {
		return _mainStream == null || _mainStream.isClosed();
	}
    
	void close() {
		if (_mainStream != null && _mainTransaction != null) {
            _mainTransaction.close(_rollbackOnClose);
        }
	}
	
	Object lock() {
		return _mainStream.lock();
	}

    Transaction transaction() {
        if (_transaction != null) {
            return _transaction;
        }
        return _mainTransaction;
    }

    void transaction(Transaction transaction) {
		if (_transaction != null) {
			_transaction = transaction;
		} else {
			_mainTransaction = transaction;
		}
		_rollbackOnClose = false;
    }
    
    private final LocalObjectContainer stream() {
        if (_stream != null) {
            return _stream;
        }
        return _mainStream;
    }
}
