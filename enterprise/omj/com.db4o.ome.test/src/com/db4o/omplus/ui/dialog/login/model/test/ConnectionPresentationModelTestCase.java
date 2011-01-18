/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model.test;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.omplus.*;
import com.db4o.omplus.connection.*;
import com.db4o.omplus.debug.*;
import com.db4o.omplus.ui.dialog.login.model.*;

import org.easymock.*;
import static org.easymock.EasyMock.*;

public class ConnectionPresentationModelTestCase {

	
	private Connector connector;
	private ErrorMessageSink errSink;
	private RecentConnectionList recentConnections;
	private MockConnectionPresentationModel model;
	
	@Before
	public void setUp() {
		recentConnections = createMock(RecentConnectionList.class);
		errSink = createMock(ErrorMessageSink.class);
		connector = createMock(Connector.class);
		LoginPresentationModel loginModel = new LoginPresentationModel(recentConnections, new ErrorMessageHandler(errSink), connector);
		model = new MockConnectionPresentationModel(loginModel);
	}
	
	@Test
	public void testManualEntry() throws Exception {
		expect(connector.connect(eqParams("foo"))).andReturn(true);
		recentConnections.addNewConnection(eqParams("foo"));
		prepareMocks();
		model.state("foo");
		model.connect();
		verifyMocks();
	}

	private void prepareMocks() {
		replay(recentConnections);
		replay(errSink);
		replay(connector);
	}

	private void verifyMocks() {
		verify(recentConnections);
		verify(errSink);
		verify(connector);
	}	
	
	private static class ConnectionParamMatcher implements IArgumentMatcher {
		
		private String expectedId;
		
		public ConnectionParamMatcher(String id) {
			this.expectedId = id;
		}

		@Override
		public void appendTo(StringBuffer str) {
			str.append("eqParams(" + expectedId + ")");
		}

		@Override
		public boolean matches(Object other) {
			if(other.getClass() != MockConnectionParams.class) {
				return false;
			}
			return expectedId.equals(((MockConnectionParams)other).id);
		}
	}
	
	public static ConnectionParams eqParams(String id) {
	    EasyMock.reportMatcher(new ConnectionParamMatcher(id));
	    return null;
	}

	
	private static class MockConnectionParams extends ConnectionParams {
		
		private String id;
		private boolean connectInvoked = false;

		public MockConnectionParams(String id) {
			this.id = id;
		}

		@Override
		public String getPath() {
			return id;
		}

		@Override
		public ObjectContainer connect(Function4<String, Boolean> userCallback) throws DBConnectException {
			connectInvoked = true;
			return null;
		}
	}
	
	private static class MockConnectionPresentationModel extends ConnectionPresentationModel<MockConnectionParams> {
		
		private String id;
		
		public MockConnectionPresentationModel(LoginPresentationModel model) {
			super(model);
		}

		@Override
		protected MockConnectionParams fromState(File[] jarFiles, String[] configNames) throws DBConnectException {
			return new MockConnectionParams(id);
		}

		@Override
		protected void selected(MockConnectionParams selected) {
			id = null;
		}

		@Override
		protected List<MockConnectionParams> connections(RecentConnectionList recentConnections) {
			return recentConnections.getRecentConnections(MockConnectionParams.class);
		}	
		
		public void state(String id) {
			this.id = id;
		}
	}
	
}
