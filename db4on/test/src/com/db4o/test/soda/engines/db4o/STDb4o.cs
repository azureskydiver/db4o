/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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