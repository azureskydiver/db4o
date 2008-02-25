/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation.network;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NetworkSocketTestCase implements TestLifeCycle {

	private ServerSocket4 _serverSocket;

	private int _port;
	
	Socket4 _client;
	
	Socket4 _server;

	private PlainSocketFactory _plainSocketFactory = new PlainSocketFactory();

	public static void main(String[] args) {
		new ConsoleTestRunner(NetworkSocketTestCase.class).run();
	}

	public void setUp() throws Exception {
		_serverSocket = new ServerSocket4(_plainSocketFactory, 0);
		_port = _serverSocket.getLocalPort();
		_client = new NetworkSocket(_plainSocketFactory, "localhost", _port);
		_server = _serverSocket.accept();
	}

	public void tearDown() throws Exception {
		_serverSocket.close();
	}

	public void testReadIntCloseClient() throws Exception {	
		assertReadClose(_client, new CodeBlock (){
			public void run() {
				_server.read();
			}			
		});
	}

	public void testReadIntCloseServer() throws Exception {
		assertReadClose(_server, new CodeBlock (){
			public void run() {
				_client.read();
			}			
		});
	}

	public void testReadByteArrayCloseClient() throws Exception {
		assertReadClose(_client, new CodeBlock (){
			public void run() {
				_server.read(new byte[10], 0, 10);
			}			
		});
	}

	public void testReadByteArrayCloseServer() throws Exception {
		assertReadClose(_server, new CodeBlock (){
			public void run() {
				_client.read(new byte[10], 0, 10);
			}			
		});
	}

	
	public void testWriteByteArrayCloseClient() throws Exception {	
		assertWriteClose(_client, new CodeBlock (){
			public void run() {
			    _server.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteByteArrayCloseServer() throws Exception {	
		assertWriteClose(_server, new CodeBlock (){
			public void run() {
			    _client.write(new byte[10]);
			}			
		});
	}
	
	public void testWriteByteArrayPartCloseClient() throws Exception {	
		assertWriteClose(_client, new CodeBlock (){
			public void run() {
			    _server.write(new byte[10], 0, 10);
			}			
		});
	}
	
	public void testWriteByteArrayPartCloseServer() throws Exception {	
		assertWriteClose(_server, new CodeBlock (){
			public void run() {
			    _client.write(new byte[10], 0, 10);
			}			
		});
	}
	
	public void testWriteIntCloseClient() throws Exception {	
		assertWriteClose(_client, new CodeBlock (){
			public void run() {
			    _server.write(0xff);
			}			
		});
	}
	
	public void testWriteIntCloseServer() throws Exception {	
		assertWriteClose(_server, new CodeBlock (){
			public void run() {
			    _client.write(0xff);    
			}			
		});
	}
	
	private void assertReadClose(final Socket4 socketToBeClosed,final CodeBlock codeBlock) throws InterruptedException {
	    CatchAllThread thread = new CatchAllThread(codeBlock);
	    thread.ensureStarted();
		socketToBeClosed.close();
		thread.join();
		Assert.isInstanceOf(Db4oIOException.class, thread.caught());
	}
	
	private void assertWriteClose(final Socket4 socketToBeClosed,final CodeBlock codeBlock){
		socketToBeClosed.close();
		Assert.expect(Db4oIOException.class, new CodeBlock() {
            public void run() throws Throwable {
                // This is a magic number: 
                // On my machine all tests start to pass when I write at least 7 times.
                // Trying with 20 on the build machine.
                for (int i = 0; i < 20; i++) {
                    codeBlock.run();
                }
            }
        });
	}
	
	
	static class CatchAllThread {
	    
	    private final Thread _thread;
	    
	    boolean _isRunning;
	    
        final CodeBlock _codeBlock;
        
	    Throwable _throwable;
	    
	    public CatchAllThread(CodeBlock codeBlock){
	        _thread = new Thread(new Runnable() {
                public void run() {
                    try{
                        synchronized(this){
                            _isRunning = true;
                        }
                        _codeBlock.run();
                    } catch (Throwable t){
                        _throwable = t;
                    }
                }
            });
	        _codeBlock = codeBlock;
	    }
	    
	    public void join() throws InterruptedException {
	        _thread.join();
        }
	    
	    private boolean isRunning(){
	        synchronized(this){
	            return _isRunning;
	        }
	    }
	    
	    public void ensureStarted(){
	        _thread.start();
	        while(! isRunning()){
	            Cool.sleepIgnoringInterruption(10);
	        }
	        Cool.sleepIgnoringInterruption(10);
	    }
	    
	    public Throwable caught(){
	        return _throwable;
	    }
	    
	}
	
}
