/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
            new File(FILE).delete();
            ObjectServer server1 = Db4o.openServer(FILE, PORT);
            server1.grantAccess(USER, PASS);
            server1.ext().configure().messageLevel(-1);
        }
      
        public void reset() {
            new File(FILE).delete();
        }
      
        public Query query() {
            return con.query();
        }
      
        public void open() {
            Db4o.configure().messageLevel(-1);
            if (IN_PROCESS_SERVER) {
                server = Db4o.openServer(FILE, PORT);
                server.grantAccess(USER, PASS);
                try {
                    Thread.sleep(1000);
                }  catch (Exception e) {
                    j4o.lang.JavaSystem.printStackTrace(e);
                }
            }
            try {
                con = Db4o.openClient(HOST, PORT, USER, PASS);
            }  catch (Exception e) {
                j4o.lang.JavaSystem.printStackTrace(e);
            }
        }
      
        public void close() {
            con.close();
            if (IN_PROCESS_SERVER) {
                server.close();
            }
        }
      
        public void store(Object obj) {
            con.set(obj);
        }
      
        public void commit() {
            con.commit();
        }
      
        public void delete(Object obj) {
            con.delete(obj);
        }
    }
}