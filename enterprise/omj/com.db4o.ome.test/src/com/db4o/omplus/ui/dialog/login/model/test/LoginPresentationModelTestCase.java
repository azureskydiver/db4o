/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.model.test;

import static org.junit.Assert.*;

import org.junit.*;

import com.db4o.foundation.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.*;
import com.db4o.omplus.ui.dialog.login.model.LoginPresentationModel.*;
import com.db4o.omplus.ui.dialog.login.test.*;
import com.db4o.omplus.ui.dialog.login.test.LoginPresentationModelFixture.*;

public class LoginPresentationModelTestCase {
	
	private LoginPresentationModelFixture fixture;

	@Before
	public void setUp() {
		fixture = new LoginPresentationModelFixture();
	}

	@Test
	public void testLocalSelectionListener() {
		final String[] received = new String[1];
		fixture.model().addListener(new LocalSelectionListener() {
			@Override
			public void localSelection(String path, boolean readOnly) {
				received[0] = path;
			}
		});
		fixture.model().select(LoginPresentationModel.LoginMode.LOCAL, 1);
		assertEquals(fixture.presetFileParams().get(1).getPath(), received[0]);
	}

	@Test
	public void testLocalOpenException() {
		fixture.interceptor(new ConnectInterceptor() {
			@Override
			public void connect(ConnectionParams params) throws DBConnectException {
				throw new DBConnectException(params, "");
			}
		});
		fixture.model().connect("foo", false);
		fixture.assertNotConnected(DBConnectException.class);
	}

	@Test
	public void testPlainOpen() {
		assertOpen("foo", false);
	}

	@Test
	public void testReadOnlyOpen() {
		assertOpen("bar", true);
	}

	private void assertOpen(final String path, final boolean readOnly) {
		final ByRef<ConnectionParams> received = new ByRef<ConnectionParams>();
		fixture.interceptor(new ConnectInterceptor() {
			@Override
			public void connect(ConnectionParams params) throws DBConnectException {
				received.value = params;
			}
		});
		fixture.model().connect(path, readOnly);
		assertEquals(path, received.value.getPath());
		assertEquals(readOnly, ((FileConnectionParams)received.value).readOnly());
	}
}
