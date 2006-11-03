/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Text;

namespace com.db4o.test {

   public class BindFileSize {
      static internal int LENGTH = 10000;
      internal String foo;
      
      public BindFileSize() : base() {
      }
      
      public BindFileSize(int Length) : base() {
         StringBuilder sb1 = new StringBuilder();
         for (int i1 = 0; i1 < Length; i1++) {
            sb1.Append("g");
         }
         this.foo = sb1.ToString();
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         Tester.Store(new BindFileSize(LENGTH));
      }
      
      public void TestGrowth() {
         int call1 = 0;
         BindFileSize bfs1 = (BindFileSize)Tester.GetOne(this);
         long id1 = Tester.ObjectContainer().GetID(bfs1);
         for (int i1 = 0; i1 < 12; i1++) {
            bfs1 = new BindFileSize(LENGTH);
            Tester.ObjectContainer().Bind(bfs1, id1);
            Tester.ObjectContainer().Set(bfs1);
            Tester.Commit();
            CheckFileSize(call1++);
            Tester.ReOpen();
         }
      }
      
      private void CheckFileSize(int call) {
         if (Tester.CanCheckFileSize()) {
            int newFileLength1 = Tester.FileLength();
            if (call == 6) {
               jumps = 0;
               fileLength = newFileLength1;
            } else if (call > 6) {
               if (newFileLength1 > fileLength) {
                  if (jumps < 4) {
                     fileLength = newFileLength1;
                     jumps++;
                  } else {
                     Tester.Error();
                  }
               }
            }
         }
      }
      [Transient] private static int fileLength;
      [Transient] private static int jumps;
   }
}