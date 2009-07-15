/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.common.exceptions.propagation.cs;

import com.db4o.cs.internal.messages.*;
import com.db4o.db4ounit.common.cs.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class MsgExceptionHandlingTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS {

	private static final String EXCEPTION_MESSAGE = "exc";

	public void testRecoverableExceptionWithResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITH_RESPONSE.getWriterForSingleObject(trans(), new Db4oRecoverableException(EXCEPTION_MESSAGE)));
		try {
			client().expectedResponse(Msg.OK);
			Assert.fail();
		}
		catch(Db4oRecoverableException exc) {
			Assert.areEqual(EXCEPTION_MESSAGE, exc.getMessage());
		}
		Assert.isTrue(client().isAlive());
		assertServerContainerStateClosed(false);
	}

	public void testNonRecoverableExceptionWithResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITH_RESPONSE.getWriterForSingleObject(trans(), new Db4oException(EXCEPTION_MESSAGE)));
		assertDatabaseClosedException();
		assertServerContainerStateClosed(true);
	}

	public void testRecoverableExceptionWithoutResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITHOUT_RESPONSE.getWriterForSingleObject(trans(), new Db4oRecoverableException(EXCEPTION_MESSAGE)));
		assertDatabaseClosedException();
		assertServerContainerStateClosed(false);
	}

	public void testNonRecoverableExceptionWithoutResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITHOUT_RESPONSE.getWriterForSingleObject(trans(), new Db4oException(EXCEPTION_MESSAGE)));
		assertDatabaseClosedException();
		assertServerContainerStateClosed(true);
	}

	private void assertDatabaseClosedException() {
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				client().expectedResponse(Msg.OK);
			}
		});
		Assert.isFalse(client().isAlive());
	}

	private void assertServerContainerStateClosed(boolean expectedClosed) {
//		Assert.areEqual(expectedClosed, server().objectContainer().ext().isClosed());
//		ExtObjectContainer otherClient = openNewClient();
//		otherClient.close();
	}

}
