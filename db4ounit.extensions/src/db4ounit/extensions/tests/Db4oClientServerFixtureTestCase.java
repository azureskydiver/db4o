package db4ounit.extensions.tests;

import static org.easymock.EasyMock.*;

import org.easymock.*;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class Db4oClientServerFixtureTestCase implements TestCase {
	
	public void testOpenWithCustomClientServerConfiguration() throws Exception {

		final String userName = "db4o";
		final String password = "db4o";
		final int port = 42;
		
		final IMocksControl mockery = createControl();
		mockery.checkOrder(true);
		
		final ClientServerFactory clientServerFactoryMock = mockery.createMock("factory", ClientServerFactory.class);
		final ExtObjectServer objectServerMock = mockery.createMock("server", ExtObjectServer.class);
		final ExtClient clientMock = mockery.createMock("client", ExtClient.class);
		final CustomClientServerConfiguration testInstanceMock = mockery.createMock(CustomClientServerConfiguration.class);
		
		testInstanceMock.configureServer(isA(Configuration.class));
			expectLastCall().once();
		
		expect(clientServerFactoryMock.openServer(isA(Configuration.class), isA(String.class), eq(-1), isA(NativeSocketFactory.class)))
			.andReturn(objectServerMock)
			.once();
		
		expect(objectServerMock.ext())
			.andReturn(objectServerMock)
			.anyTimes();
		
		expect(objectServerMock.port())
			.andReturn(42)
			.anyTimes();
		
		objectServerMock.grantAccess(userName, password);
			expectLastCall().once();
	
		testInstanceMock.configureClient(isA(Configuration.class));
			expectLastCall().once();
			
		expect(clientServerFactoryMock.openClient(isA(Configuration.class), eq("127.0.0.1"), eq(port), eq(userName), eq(password), isA(NativeSocketFactory.class)))
			.andReturn(clientMock)
			.once();
		
		expect(clientMock.ext())
			.andReturn(clientMock)
			.anyTimes();
		
		mockery.replay();
		
		final Db4oClientServer fixture = new Db4oClientServer(clientServerFactoryMock, false, "C/S");
		fixture.open(testInstanceMock);
		
		mockery.verify();
	} 

}
