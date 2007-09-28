/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation.network;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NetworkSocketTestCase implements TestLifeCycle {

	private ServerSocket4 _server;

	private int _port;
	
	Socket4 client;
	
	Socket4 serverSide;

	private PlainSocketFactory _plainSocketFactory = new PlainSocketFactory();

	public static void main(String[] args) {
		new TestRunner(NetworkSocketTestCase.class).run();
	}

	public void setUp() throws Exception {
		_server = new ServerSocket4(_plainSocketFactory, 0);
		_port = _server.getLocalPort();
		client = new NetworkSocket(_plainSocketFactory,
				"localhost", _port);
		serverSide = _server.accept();
	}

	public void tearDown() throws Exception {
		_server.close();
	}

	public void testRead_Close1() throws Exception {	
		assertReadClose(client, new CodeBlock (){
			public void run() {
				serverSide.read();
			}			
		});
	}

	public void testRead_Close2() throws Exception {
		assertReadClose(serverSide, new CodeBlock (){
			public void run() {
				client.read();
			}			
		});
	}

	public void testReadBII_Close1() throws Exception {
		assertReadClose(client, new CodeBlock (){
			public void run() {
				serverSide.read(new byte[10], 0, 10);
			}			
		});
	}

	public void testReadBII_Close2() throws Exception {
		assertReadClose(serverSide, new CodeBlock (){
			public void run() {
				client.read(new byte[10], 0, 10);
			}			
		});
	}

	
	public void testWriteB_Close1() throws Exception {	
		assertWriteClose(client, new CodeBlock (){
			public void run() {
				serverSide.write(new byte[10]);
				serverSide.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteB_Close2() throws Exception {	
		assertWriteClose(serverSide, new CodeBlock (){
			public void run() {
				client.write(new byte[10]);
				client.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteBII_Close1() throws Exception {	
		assertWriteClose(client, new CodeBlock (){
			public void run() {
				serverSide.write(new byte[10], 0, 10);
				serverSide.write(new byte[10], 0, 10);
			}			
		});
	}
	
	public void testWriteBII_Close2() throws Exception {	
		assertWriteClose(serverSide, new CodeBlock (){
			public void run() {
				client.write(new byte[10], 0, 10);
				client.write(new byte[10], 0, 10);
			}			
		});
	}
	
	public void testWriteI_Close1() throws Exception {	
		assertWriteClose(client, new CodeBlock (){
			public void run() {
				serverSide.write(0xff);
				serverSide.write(0xff);
			}			
		});
	}
	
	public void testWriteI_Close2() throws Exception {	
		assertWriteClose(serverSide, new CodeBlock (){
			public void run() {
				client.write(0xff);
				client.write(0xff);
			}			
		});
	}
	
	
	private void assertReadClose(final Socket4 socketToBeClosed,final CodeBlock codeBlock) {
		new Thread() {
			public void run() {
				Cool.sleepIgnoringInterruption(500);
				socketToBeClosed.close();
			}
		}.start();
		
		Assert.expect(Db4oIOException.class, codeBlock);
	}
	
	private void assertWriteClose(final Socket4 socketToBeClosed,final CodeBlock codeBlock) {
		socketToBeClosed.close();
		Assert.expect(Db4oIOException.class, codeBlock);
	}
}
