/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.cs.internal;

import com.db4o.*;
import com.db4o.cs.internal.ShutdownMode.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class ClientTransactionPool {

	private final Hashtable4 _transaction2Container; // Transaction -> ContainerCount
	
	private final Hashtable4 _fileName2Container; // String -> ContainerCount
	
	private final LocalObjectContainer _mainContainer;
	
	private boolean _closed;
		
	public ClientTransactionPool(LocalObjectContainer mainContainer) {
		ContainerCount mainEntry = new ContainerCount(mainContainer, 1);
		_transaction2Container = new Hashtable4();
		_fileName2Container = new Hashtable4();
		_fileName2Container.put(mainContainer.fileName(), mainEntry);
		_mainContainer = mainContainer;
	}

	public Transaction acquireMain() {
		return acquire(_mainContainer.fileName());
	}

	public Transaction acquire(String fileName) {
		synchronized(_mainContainer.lock()) {
			ContainerCount entry = (ContainerCount) _fileName2Container.get(fileName);
			if (entry == null) {
				LocalObjectContainer container = (LocalObjectContainer) Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), fileName);
		        container.configImpl().setMessageRecipient(_mainContainer.configImpl().messageRecipient());
				entry = new ContainerCount(container);
				_fileName2Container.put(fileName, entry);
			}
			Transaction transaction = entry.newTransaction();
			_transaction2Container.put(transaction, entry);
			return transaction;
		}
	}
	
	public void release(ShutdownMode mode, Transaction transaction, boolean rollbackOnClose) {
		synchronized(_mainContainer.lock()) {
			ContainerCount entry = (ContainerCount) _transaction2Container.get(transaction);
			entry._container.closeTransaction(transaction, false, mode.isFatal() ? false : rollbackOnClose);
			_transaction2Container.remove(transaction);
			entry.release();
			if(entry.isEmpty()) {
				_fileName2Container.remove(entry.fileName());
				try{
					entry.close(mode);
				}catch(Throwable t){
					// If we are in fatal ShutdownMode close will
					// throw but we want to continue shutting down
					// all entries.
					t.printStackTrace();
				}
			}
		}
	}

	public void close() {
		close(ShutdownMode.NORMAL);
	}

	public void close(ShutdownMode mode) {
		synchronized(_mainContainer.lock()) {
			Iterator4 entryIter = _fileName2Container.iterator();
			while(entryIter.moveNext()) {
				Entry4 hashEntry = (Entry4) entryIter.current();
				ContainerCount containerCount = (ContainerCount)hashEntry.value();
				try{
					containerCount.close(mode);
				} catch(Throwable t){
					// If we are in fatal ShutdownMode close will
					// throw but we want to continue shutting down
					// all entries.
					t.printStackTrace();
				}
			}
			_closed = true;
		}
	}
	
	public int openTransactionCount(){
		return isClosed() ? 0 : _transaction2Container.size();
	}

	public int openFileCount() {
		return isClosed() ? 0 : _fileName2Container.size();
	}

    public boolean isClosed() {
		return _closed == true || _mainContainer.isClosed();
	}

	private static class ContainerCount {
		public LocalObjectContainer _container;
		private int _count;

		public ContainerCount(LocalObjectContainer container) {
			this(container, 0);
		}

		public ContainerCount(LocalObjectContainer container, int count) {
			_container = container;
			_count = count;
		}
		
		public boolean isEmpty() {
			return _count <= 0;
		}
		
		public Transaction newTransaction() {
			_count++;
			return _container.newUserTransaction();
		}
		
		public void release() {
			if(_count == 0) {
				throw new IllegalStateException();
			}
			_count--;
		}
		
		public String fileName() {
			return _container.fileName();
		}

		public void close(ShutdownMode mode) {
			if(!mode.isFatal()) {
				_container.close();
				_container = null;
				return;
			}
			_container.fatalShutdown(((FatalMode)mode).exc());
		}

		public int hashCode() {
			return fileName().hashCode();
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			final ContainerCount other = (ContainerCount) obj;
			return fileName().equals(other.fileName());
		}

		
	}
}
