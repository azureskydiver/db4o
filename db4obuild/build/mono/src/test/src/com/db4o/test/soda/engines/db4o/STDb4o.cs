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

   public class STDb4o : STEngine {
      
      public STDb4o() : base() {
      }
      private String FILE = "soda.yap";
      
      public static void Main(String[] arguments) {
         JavaSystem._out.println(Db4o.version());
      }
      private com.db4o.ObjectContainer con;
      
      public void reset() {
         new File(FILE).delete();
      }
      
      public Query query() {
         return con.query();
      }
      
      public void open() {
         Db4o.configure().messageLevel(-1);
         Db4o.configure().activationDepth(7);
         con = Db4o.openFile(FILE);
      }
      
      public void close() {
         con.close();
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