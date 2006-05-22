/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.io;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o;
namespace com.db4o.test.soda.engines.db4o {

    public class STDb4oClientServer : STEngine {
      
        public STDb4oClientServer() : base() {
        }
        private static int PORT = 4444;
        private static String HOST = "localhost";
        private static String FILE = "sodacs.yap";
        private static String USER = "S.O.D.A.";
        private static String PASS = "rocks";
        private static bool IN_PROCESS_SERVER = true;
        private com.db4o.ObjectServer server;
        private com.db4o.ObjectContainer con;
      
        /**
         * starts a db4o server. <br>To test with a remote server:<br> - set IN_PROCESS_SERVER to false - start STDb4oClientServer on the server - run SodaTest on the client - STDb4oClientServer needs to be uncommented in SodaTest#ENGINES The server can be stopped with CTRL + C.
         */
        public static void Main(String[] args) {
            new File(FILE).Delete();
            ObjectServer server1 = Db4o.OpenServer(FILE, PORT);
            server1.GrantAccess(USER, PASS);
            server1.Ext().Configure().MessageLevel(-1);
        }
      
        public void Reset() {
            new File(FILE).Delete();
        }
      
        public Query Query() {
            return con.Query();
        }
      
        public void Open() {
            Db4o.Configure().MessageLevel(-1);
            if (IN_PROCESS_SERVER) {
                server = Db4o.OpenServer(FILE, PORT);
                server.GrantAccess(USER, PASS);
                try {
                    Thread.Sleep(1000);
                }  catch (Exception e) {
                    j4o.lang.JavaSystem.PrintStackTrace(e);
                }
            }
            try {
                con = Db4o.OpenClient(HOST, PORT, USER, PASS);
            }  catch (Exception e) {
                j4o.lang.JavaSystem.PrintStackTrace(e);
            }
        }
      
        public void Close() {
            con.Close();
            if (IN_PROCESS_SERVER) {
                server.Close();
            }
        }
      
        public void Store(Object obj) {
            con.Set(obj);
        }
      
        public void Commit() {
            con.Commit();
        }
      
        public void Delete(Object obj) {
            con.Delete(obj);
        }
    }
}