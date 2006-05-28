/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4oTools;

using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.test.types;

namespace com.db4o.test {

   public class Cascades {
      
      public Cascades() : base() {
      }
      internal String name;
      internal Cascades cascades;
      internal ObjectSimplePublic osp;
      internal Cascades[] arrCascades;
      internal ObjectSimplePublic[] arrOsp;
      static internal String FILE = "cascades.yap";
      
      public static void Main(String[] args) {
         Db4o.Configure().ActivationDepth(1);
         Set();
         Db4o.Configure().ObjectClass("com.db4o.test.Cascades").ObjectField("cascades").CascadeOnDelete(true);
         Db4o.Configure().ObjectClass("com.db4o.test.Cascades").ObjectField("arrCascades").CascadeOnDelete(true);
         Db4o.Configure().ObjectClass("com.db4o.test.Cascades").ObjectField("osp").CascadeOnDelete(true);
         Db4o.Configure().ObjectClass("com.db4o.test.Cascades").ObjectField("arrOsp").CascadeOnDelete(true);
         UpdateComplex();
         GetAll();
      }
      
      static internal void Set() {
         new File(FILE).Delete();
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.name = "Level1";
         cc1.cascades = new Cascades();
         cc1.cascades.name = "Level2";
         cc1.cascades.cascades = new Cascades();
         cc1.cascades.cascades.name = "Level3";
         cc1.osp = new ObjectSimplePublic();
         cc1.osp.Set(1);
         cc1.cascades.osp = new ObjectSimplePublic();
         cc1.cascades.osp.Set(1);
         cc1.arrOsp = new ObjectSimplePublic[]{
            new ObjectSimplePublic()         };
         cc1.arrOsp[0].Set(1);
         cc1.arrCascades = new Cascades[]{
            new Cascades()         };
         cc1.arrCascades[0].name = "Array Level2";
         cc1.arrCascades[0].osp = new ObjectSimplePublic();
         cc1.arrCascades[0].osp.Set(1);
         cc1.arrCascades[0].cascades = new Cascades();
         cc1.arrCascades[0].cascades.name = "Array Level3";
         con.Set(cc1);
         con.Close();
      }
      
      static internal void Get() {
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.Get(cc1);
         while (set1.HasNext()) {
            Logger.Log(con, set1.Next());
         }
         con.Close();
      }
      
      static internal void GetAll() {
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.Get(null);
         while (set1.HasNext()) {
            Object obj1 = set1.Next();
            con.Activate(obj1, Int32.MaxValue);
            Logger.Log(con, obj1);
         }
         con.Close();
      }
      
      static internal void UpdateSimple() {
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.Get(cc1);
         Cascades toUpdate1 = (Cascades)set1.Next();
         con.Activate(toUpdate1, Int32.MaxValue);
         toUpdate1.cascades.name = "Level2 Update";
         toUpdate1.cascades.cascades.name = "Level3 Update";
         con.Set(toUpdate1);
         con.Close();
      }
      
      static internal void UpdateComplex() {
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.Get(cc1);
         Cascades toUpdate1 = (Cascades)set1.Next();
         con.Activate(toUpdate1, Int32.MaxValue);
         toUpdate1.cascades = null;
         toUpdate1.arrCascades = new Cascades[]{
            new Cascades()         };
         toUpdate1.arrCascades[0].name = "Level2 Arr New";
         con.Set(toUpdate1);
         con.Close();
      }
      
      static internal void Delete() {
         ObjectContainer con = Db4o.OpenFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.Get(cc1);
         Cascades toDelete1 = (Cascades)set1.Next();
         con.Activate(toDelete1, Int32.MaxValue);
         con.Delete(toDelete1);
         con.Close();
      }
   }
}