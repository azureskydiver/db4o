/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test
{
	/// <summary>
	/// Summary description for ServerFastReopen.
	/// </summary>
	public class ServerFastReopen
	{

        public void test(){
            openCloseServer();
            openCloseServer();
            openCloseServer();
            openCloseServer();
            openCloseServer();
        }

        private void openCloseServer(){
            ObjectServer os = Db4o.openServer("ServerFastReopen.yap",5001);
            os.grantAccess("db4o", "db4o");
            ObjectContainer con = Db4o.openClient("localhost",5001, "db4o", "db4o");
            con.set(this);
            con.commit();
            con.close();
            os.close();
        }
	}
}
