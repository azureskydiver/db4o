/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import java.util.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class PrefetchIDCountTestCase extends TestWithTempFile {

	public static void main(String[] args) {
		new ConsoleTestRunner(PrefetchIDCountTestCase.class).run();
	}
	
	private static final int PREFETCH_ID_COUNT = 100;
	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";
	
	public static class Item {
	}

	public void test() throws Exception {
		final ObjectServerImpl server = (ObjectServerImpl) Db4oClientServer.openServer(tempFile(), Db4oClientServer.ARBITRARY_PORT);
		
		final Lock4 lock = new Lock4();
		server.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			lock.run(new Closure4() { public Object run() {
				lock.awake();
				return null;
			}});
			
		}});
		
		server.grantAccess(USER, PASSWORD);
		final ObjectContainer client = openClient(server.port());
		
		final ServerMessageDispatcherImpl msgDispatcher = firstMessageDispatcherFor(server);
		Transaction transaction = msgDispatcher.transaction();
		LocalObjectContainer container = (LocalObjectContainer) server.objectContainer();
		StandardIdSystem standardIdSystem = (StandardIdSystem) container.idSystem();
		final int prefetchedID = standardIdSystem.prefetchID(transaction);
		
		Assert.isGreater(0, prefetchedID);
		
		final DebugFreespaceManager freespaceManager = new DebugFreespaceManager(container);
		container.installDebugFreespaceManager(freespaceManager);
		
		lock.run(new Closure4() { public Object run() {
			client.close();
			lock.snooze(100000);
			Assert.isTrue(freespaceManager.wasFreed(prefetchedID));
			return null;
		}});		
		
		server.close();
	}

	private ServerMessageDispatcherImpl firstMessageDispatcherFor(final ObjectServerImpl server) {
		Iterator4 dispatchers = server.iterateDispatchers();
	
		Assert.isTrue(dispatchers.moveNext());
		final ServerMessageDispatcherImpl msgDispatcher = (ServerMessageDispatcherImpl) dispatchers.current();
		
		return msgDispatcher;
	}

	private ObjectContainer openClient(int port) {
		ClientConfiguration config = Db4oClientServer.newClientConfiguration();
		config.prefetchIDCount(PREFETCH_ID_COUNT);
		return Db4oClientServer.openClient(config, "localhost", port, USER, PASSWORD);
	}
	
	public static class DebugFreespaceManager extends AbstractFreespaceManager {
		
		public DebugFreespaceManager(LocalObjectContainer file) {
			super(null, 0);
		}

		private final List<Integer> _freedSlots = new ArrayList<Integer>();
		
		public boolean wasFreed(int id){
			return _freedSlots.contains(id);
		}

		public Slot allocateSlot(int length) {
			return null;
		}

		public Slot allocateTransactionLogSlot(int length) {
			return null;
		}

		public void beginCommit() {
			// TODO Auto-generated method stub
			
		}

		public void commit() {
			// TODO Auto-generated method stub
			
		}

		public void endCommit() {
			// TODO Auto-generated method stub
			
		}

		public void free(Slot slot) {
			_freedSlots.add(slot.address());
		}

		public void freeSelf() {
			// TODO Auto-generated method stub
			
		}

		public void freeTransactionLogSlot(Slot slot) {
			// TODO Auto-generated method stub
			
		}

		public void listener(FreespaceListener listener) {
			// TODO Auto-generated method stub
			
		}

		public void migrateTo(FreespaceManager fm) {
			// TODO Auto-generated method stub
			
		}

		public void read(LocalObjectContainer container, int freeSpaceID) {
			// TODO Auto-generated method stub
			
		}

		public int slotCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void start(int slotAddress) {
			// TODO Auto-generated method stub
			
		}

		public byte systemType() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int totalFreespace() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void traverse(Visitor4 visitor) {
			// TODO Auto-generated method stub
			
		}

		public int write(LocalObjectContainer container) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
