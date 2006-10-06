/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;

public class ServerRevokeAccess {
	
	static final String FILE = "ServerRevokeAccessTest.yap";
	
	public void test(){
		
		if(! Test.isClientServer() && Test.currentRunner.CLIENT_SERVER){
			
			try{
				new File(FILE).delete();
				ObjectServer server = Db4o.openServer(FILE, AllTests.SERVER_PORT);
			
				String user = "hohohi";
				String password = "hohoho";
				server.grantAccess(user, password);
			
				ObjectContainer con = Db4o.openClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user,password);
				Test.ensure(con != null);
				con.close();
                con=null;
				
				server.ext().revokeAccess(user);
				
				boolean exceptionThrown = false; 
				
				try{
					con = Db4o.openClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user,password);
				}catch(Exception e){
					exceptionThrown = true;
				}finally{
					if(con!=null) {
						con.close();
					}
                }
				Test.ensure(con==null);
				Test.ensure(exceptionThrown);
				
				server.close();
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			
			
		}
		
	}

	
	public static void main(String[] args) {
		AllTests.run(ServerRevokeAccess.class);
	}
}
