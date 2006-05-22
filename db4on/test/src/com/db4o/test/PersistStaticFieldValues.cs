/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class PersistStaticFieldValues {
      
      public PersistStaticFieldValues() : base() {
      }
      static internal PsfvHelper ONE = new PsfvHelper();
      static internal PsfvHelper TWO = new PsfvHelper();
      static internal PsfvHelper THREE = new PsfvHelper();
      public PsfvHelper one;
      public PsfvHelper two;
      public PsfvHelper three;
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).PersistStaticFieldValues();
      }
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         PersistStaticFieldValues psfv1 = new PersistStaticFieldValues();
         psfv1.one = ONE;
         psfv1.two = TWO;
         psfv1.three = THREE;
         Tester.Store(psfv1);
      }
      
      public void Test() {
         PersistStaticFieldValues psfv1 = (PersistStaticFieldValues)Tester.GetOne(this);
         Tester.Ensure(psfv1.one == ONE);
         Tester.Ensure(psfv1.two == TWO);
         Tester.Ensure(psfv1.three == THREE);
      }
      
      public class PsfvHelper {
         
         public PsfvHelper() : base() {
         }
      }
   }
}