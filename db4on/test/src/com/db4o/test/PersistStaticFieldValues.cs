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
      
      public void configure() {
         Db4o.configure().objectClass(this).persistStaticFieldValues();
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         PersistStaticFieldValues psfv1 = new PersistStaticFieldValues();
         psfv1.one = ONE;
         psfv1.two = TWO;
         psfv1.three = THREE;
         Test.store(psfv1);
      }
      
      public void test() {
         PersistStaticFieldValues psfv1 = (PersistStaticFieldValues)Test.getOne(this);
         Test.ensure(psfv1.one == ONE);
         Test.ensure(psfv1.two == TWO);
         Test.ensure(psfv1.three == THREE);
      }
      
      public class PsfvHelper {
         
         public PsfvHelper() : base() {
         }
      }
   }
}