/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class BindFileSize {
      static internal int LENGTH = 10000;
      internal String foo;
      
      public BindFileSize() : base() {
      }
      
      public BindFileSize(int Length) : base() {
         StringBuffer sb1 = new StringBuffer();
         for (int i1 = 0; i1 < Length; i1++) {
            sb1.append("g");
         }
         this.foo = sb1.ToString();
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.store(new BindFileSize(LENGTH));
      }
      
      public void testGrowth() {
         int call1 = 0;
         BindFileSize bfs1 = (BindFileSize)Test.getOne(this);
         long id1 = Test.objectContainer().getID(bfs1);
         for (int i1 = 0; i1 < 12; i1++) {
            bfs1 = new BindFileSize(LENGTH);
            Test.objectContainer().bind(bfs1, id1);
            Test.objectContainer().set(bfs1);
            Test.commit();
            checkFileSize(call1++);
            Test.reOpen();
         }
      }
      
      private void checkFileSize(int call) {
         if (Test.canCheckFileSize()) {
            int newFileLength1 = Test.fileLength();
            if (call == 6) {
               jumps = 0;
               fileLength = newFileLength1;
            } else if (call > 6) {
               if (newFileLength1 > fileLength) {
                  if (jumps < 4) {
                     fileLength = newFileLength1;
                     jumps++;
                  } else {
                     Test.error();
                  }
               }
            }
         }
      }
      [Transient] private static int fileLength;
      [Transient] private static int jumps;
   }
}