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
      
      internal void storeOne() {
         name = "helo";
         new File(DB_FILE).delete();
      }
      
      internal void testOne() {
         if (Test.isClientServer()) {
            Test.ensure(name.Equals("helo"));
            ExtClient client1 = (ExtClient)Test.objectContainer();
            client1.switchToFile(DB_FILE);
            name = "hohoho";
            client1.set(this);
            Query q1 = client1.query();
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            ObjectSet results1 = q1.execute();
            Test.ensure(results1.size() == 1);
            SwitchingFilesFromClient sffc1 = (SwitchingFilesFromClient)results1.next();
            Test.ensure(sffc1.name.Equals("hohoho"));
            client1.switchToMainFile();
            sffc1 = (SwitchingFilesFromClient)Test.getOne(this);
            Test.ensure(sffc1.name.Equals("helo"));
         }
      }
   }
}