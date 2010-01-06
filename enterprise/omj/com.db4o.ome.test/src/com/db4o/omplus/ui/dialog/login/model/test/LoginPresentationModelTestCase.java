/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.model.test;

import static org.junit.Assert.*;

import org.junit.*;

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
			public void localSelection(String path) {
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
			public void connect() throws DBConnectException {
				throw new IllegalArgumentException();
			}
		});
		fixture.model().connect("foo");
		fixture.assertNotConnected(IllegalArgumentException.class);
	}
}
