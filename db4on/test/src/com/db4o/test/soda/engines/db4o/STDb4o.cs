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
		  System.Console.WriteLine(Db4o.Version());
      }
      private com.db4o.ObjectContainer con;
      
      public void Reset() {
         new File(FILE).Delete();
      }
      
      public Query Query() {
         return con.Query();
      }
      
      public void Open() {
         Db4o.Configure().MessageLevel(-1);
         Db4o.Configure().ActivationDepth(7);
         con = Db4o.OpenFile(FILE);
      }
      
      public void Close() {
         con.Close();
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