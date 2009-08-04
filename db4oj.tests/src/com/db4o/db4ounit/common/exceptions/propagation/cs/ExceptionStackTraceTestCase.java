/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */

/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.common.exceptions.propagation.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.events.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
public class ExceptionStackTraceTestCase extends TestWithTempFile implements db4ounit.extensions.fixtures.OptOutDefragSolo {

	public static class Item {		
	}
	
	public static void main(String[] args) {	
		new ConsoleTestRunner(ExceptionStackTraceTestCase.class).run();
	}
	
	private ObjectServer _server;
	private ObjectContainer _client;	
	
	public void testStackTracesContainsServerSideMethods() {
		_client.store(new Item());
		
		try {
			_client.commit();
			Assert.fail("Commit should have thrown.");
		} catch (EventException ex) {
			Assert.isInstanceOf(EventException.class, ex);
			assertExceptionContainsServerStackTrace(ex);			
		}
	}

	private void assertExceptionContainsServerStackTrace(EventException ex) {		
		final String stackTrace = stackTraceFor(ex);		
		Assert.isGreaterOrEqual(0, stackTrace.indexOf("MCommit.replyFromServer"));
	}

	private String stackTraceFor(EventException ex) {
		final StringWriter stackTrace = new StringWriter();
		ex.printStackTrace(new PrintWriter(stackTrace));
		
		return stackTrace.toString();
	}

	private void registerForCommitEventOnServer() {
		if (_server == null) {
			throw new IllegalStateException();
		}
		
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(serverContainer());
		eventRegistry.committing().addListener(new EventListener4() {
													private boolean shouldThrow = true; 
													
													public void onEvent(Event4 e, EventArgs args) {
														if (shouldThrow) {
															shouldThrow = false;
															throw new IllegalStateException();
														}
													}
												});
		
		
	}

	private ObjectContainer serverContainer() {
		return _server.ext().objectContainer();
	}


	private void openClient() {
		_client = Db4oClientServer.openClient(newClientConfig(), "localhost", 0xdb40, "db4o", "db4o");
	}

	private void openServer() {
		_server = Db4oClientServer.openServer(Db4oClientServer.newServerConfiguration(), tempFile(), 0xdb40);
		_server.grantAccess("db4o", "db4o");
	}

	private ClientConfiguration newClientConfig() {
		return Db4oClientServer.newClientConfiguration();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		openServer();
		registerForCommitEventOnServer();
		openClient();		
	}
	
	@Override
	public void tearDown() throws Exception {
		
		
		if (_client != null) {
			_client.close();
		}
		
		if (_server != null) {
			_server.close();
		}
		
		super.tearDown();
	}
}
