/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o.test
{
	/// <summary>
	/// Summary description for ServerFastReopen.
	/// </summary>
	public class ServerFastReopen
	{

        public void Test(){
            OpenCloseServer();
            OpenCloseServer();
            OpenCloseServer();
            OpenCloseServer();
            OpenCloseServer();
        }

        private void OpenCloseServer(){
            ObjectServer os = Db4o.OpenServer("ServerFastReopen.yap",5001);
            os.GrantAccess("db4o", "db4o");
            ObjectContainer con = Db4o.OpenClient("localhost",5001, "db4o", "db4o");
            con.Set(this);
            con.Commit();
            con.Close();
            os.Close();
        }
	}
}
