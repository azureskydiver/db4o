/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.model.test;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;

import com.db4o.foundation.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.ui.dialog.login.model.LocalPresentationModel.LocalSelectionListener;
import com.db4o.omplus.ui.dialog.login.test.*;
import com.db4o.omplus.ui.dialog.login.test.LoginPresentationModelFixture.ConnectInterceptor;

public class LoginPresentationModelTestCase {
	
	private LoginPresentationModelFixture fixture;

	@Before
	public void setUp() {
		fixture = new LoginPresentationModelFixture();
	}

	@Test
	public void testLocalSelectionListener() {
		final String[] received = new String[1];
		fixture.localModel().addListener(new LocalSelectionListener() {
			@Override
			public void localSelection(String path, boolean readOnly) {
				received[0] = path;
			}

			@Override
			public void customConfig(String[] jarPaths, String[] configClassNames) {
			}
		});
		fixture.localModel().select(1);
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
		fixture.localModel().state("foo", false);
		fixture.localModel().connect();
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

	@Test
	public void testCustomConfig() {
		final ByRef<String[]> jarPathsReceived = ByRef.newInstance();
		final ByRef<String[]> configClassNamesReceived = ByRef.newInstance();
		fixture.localModel().addListener(new LocalSelectionListener() {
			@Override
			public void localSelection(String path, boolean readOnly) {
			}
			
			@Override
			public void customConfig(String[] jarPaths, String[] configClassNames) {
				jarPathsReceived.value = jarPaths;
				configClassNamesReceived.value = configClassNames;
			}
		});
		fixture.localModel().jarPaths(new String[] { "foo.jar", "bar.jar" });
		Assert.assertEquals(2, jarPathsReceived.value.length);
		Assert.assertEquals("bar.jar", new File(jarPathsReceived.value[0]).getName());
		Assert.assertEquals("foo.jar", new File(jarPathsReceived.value[1]).getName());
		Assert.assertEquals(0, configClassNamesReceived.value.length);
		fixture.assertNoError();
	}
	
	private void assertOpen(final String path, final boolean readOnly) {
		final ByRef<ConnectionParams> received = new ByRef<ConnectionParams>();
		fixture.interceptor(new ConnectInterceptor() {
			@Override
			public void connect(ConnectionParams params) throws DBConnectException {
				received.value = params;
			}
		});
		fixture.localModel().state(path, readOnly);
		fixture.localModel().connect();
		assertEquals(path, received.value.getPath());
		assertEquals(readOnly, ((FileConnectionParams)received.value).readOnly());
	}
}
