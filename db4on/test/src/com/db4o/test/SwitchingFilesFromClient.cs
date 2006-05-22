/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.ext;
using com.db4o.query;
namespace com.db4o.test {

   public class SwitchingFilesFromClient {
      
      public SwitchingFilesFromClient() : base() {
      }
      static internal String DB_FILE = "switchedToTest.yap";
      internal String name;
      
      internal void StoreOne() {
         name = "helo";
         new File(DB_FILE).Delete();
      }
      
      internal void TestOne() {
         if (Tester.IsClientServer()) {
            Tester.Ensure(name.Equals("helo"));
            ExtClient client1 = (ExtClient)Tester.ObjectContainer();
            client1.SwitchToFile(DB_FILE);
            name = "hohoho";
            client1.Set(this);
            Query q1 = client1.Query();
            q1.Constrain(j4o.lang.Class.GetClassForObject(this));
            ObjectSet results1 = q1.Execute();
            Tester.Ensure(results1.Size() == 1);
            SwitchingFilesFromClient sffc1 = (SwitchingFilesFromClient)results1.Next();
            Tester.Ensure(sffc1.name.Equals("hohoho"));
            client1.SwitchToMainFile();
            sffc1 = (SwitchingFilesFromClient)Tester.GetOne(this);
            Tester.Ensure(sffc1.name.Equals("helo"));
         }
      }
   }
}