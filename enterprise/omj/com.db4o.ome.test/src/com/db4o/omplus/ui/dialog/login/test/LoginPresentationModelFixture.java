/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.ui.dialog.login.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.*;

import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.datalayer.*;
import com.db4o.omplus.debug.*;
import com.db4o.omplus.ui.dialog.login.model.*;

public class LoginPresentationModelFixture {

	private final RecentConnectionList recentConnections;
	private final List<FileConnectionParams> presetFileParams;
	private final LoginPresentationModel model;
	private ConnectionParams paramsReceived;
	private Throwable exceptionReceived;
	private String errorMsgReceived;
	private ConnectInterceptor interceptor;

	public LoginPresentationModelFixture() {
		presetFileParams = Collections.unmodifiableList(Arrays.asList(new FileConnectionParams("foo", false), new FileConnectionParams("bar", true)));
		OMEDataStore dataStore = new InMemoryOMEDataStore();
		recentConnections = new DataStoreRecentConnectionList(dataStore);
		for(int idx = presetFileParams.size() - 1; idx >= 0; idx--) {
			recentConnections.addNewConnection(presetFileParams.get(idx));
		}
		ErrorMessageSink err = new ErrorMessageSink() {
			@Override
			protected void showError(String msg) {
				errorMsgReceived = msg;
			}

			@Override
			protected void logExc(Throwable exc) {
				exceptionReceived = exc;
			}
		};
		Connector connector = new Connector() {
			@Override
			public boolean connect(ConnectionParams params) throws DBConnectException {
				interceptor.connect(params);
				paramsReceived = params;
				return true;
			}
		};
		model = new LoginPresentationModel(recentConnections, err,  connector);
		interceptor = new NullConnectInterceptor();
		paramsReceived = null;
		exceptionReceived = null;
		errorMsgReceived = null;
	}
	
	public LoginPresentationModel model() {
		return model;
	}

	public List<FileConnectionParams> presetFileParams() {
		return presetFileParams;
	}
	
	public void interceptor(ConnectInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	
	public void assertConnected(ConnectionParams expected) {
		assertEquals(expected, paramsReceived);
		List<FileConnectionParams> recentFileConnections = recentConnections.getRecentFileConnections();
		assertEquals(expected, recentFileConnections.get(0));
		int cmpIdx = 1;
		for (FileConnectionParams curPreset : presetFileParams) {
			if(curPreset.equals(expected)) {
				continue;
			}
			assertEquals(curPreset, recentFileConnections.get(cmpIdx));
			cmpIdx++;
		}
	}

	public void assertNotConnected() {
		assertNull(paramsReceived);
	}

	public void assertNotConnected(Class<? extends Throwable> excType) {
		assertNotConnected();
		assertNotNull(errorMsgReceived);
		if(excType != null) {
			assertTrue(excType.isAssignableFrom(exceptionReceived.getClass()));
		}
	}
	
	public static interface ConnectInterceptor {
		void connect(ConnectionParams params) throws DBConnectException;
	}
	
	private static class NullConnectInterceptor implements ConnectInterceptor {
		@Override
		public void connect(ConnectionParams params) throws DBConnectException {
		}
	}
}
