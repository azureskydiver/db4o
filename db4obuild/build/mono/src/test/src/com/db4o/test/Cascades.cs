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
using com.db4o;
using com.db4o.tools;
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
         Db4o.configure().activationDepth(1);
         set();
         Db4o.configure().objectClass("com.db4o.test.Cascades").objectField("cascades").cascadeOnDelete(true);
         Db4o.configure().objectClass("com.db4o.test.Cascades").objectField("arrCascades").cascadeOnDelete(true);
         Db4o.configure().objectClass("com.db4o.test.Cascades").objectField("osp").cascadeOnDelete(true);
         Db4o.configure().objectClass("com.db4o.test.Cascades").objectField("arrOsp").cascadeOnDelete(true);
         updateComplex();
         getAll();
      }
      
      static internal void set() {
         new File(FILE).delete();
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.name = "Level1";
         cc1.cascades = new Cascades();
         cc1.cascades.name = "Level2";
         cc1.cascades.cascades = new Cascades();
         cc1.cascades.cascades.name = "Level3";
         cc1.osp = new ObjectSimplePublic();
         cc1.osp.set(1);
         cc1.cascades.osp = new ObjectSimplePublic();
         cc1.cascades.osp.set(1);
         cc1.arrOsp = new ObjectSimplePublic[]{
            new ObjectSimplePublic()         };
         cc1.arrOsp[0].set(1);
         cc1.arrCascades = new Cascades[]{
            new Cascades()         };
         cc1.arrCascades[0].name = "Array Level2";
         cc1.arrCascades[0].osp = new ObjectSimplePublic();
         cc1.arrCascades[0].osp.set(1);
         cc1.arrCascades[0].cascades = new Cascades();
         cc1.arrCascades[0].cascades.name = "Array Level3";
         con.set(cc1);
         con.close();
      }
      
      static internal void get() {
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.get(cc1);
         while (set1.hasNext()) {
            Logger.log(con, set1.next());
         }
         con.close();
      }
      
      static internal void getAll() {
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.get(null);
         while (set1.hasNext()) {
            Object obj1 = set1.next();
            con.activate(obj1, Int32.MaxValue);
            Logger.log(con, obj1);
         }
         con.close();
      }
      
      static internal void updateSimple() {
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.get(cc1);
         Cascades toUpdate1 = (Cascades)set1.next();
         con.activate(toUpdate1, Int32.MaxValue);
         toUpdate1.cascades.name = "Level2 Update";
         toUpdate1.cascades.cascades.name = "Level3 Update";
         con.set(toUpdate1);
         con.close();
      }
      
      static internal void updateComplex() {
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.get(cc1);
         Cascades toUpdate1 = (Cascades)set1.next();
         con.activate(toUpdate1, Int32.MaxValue);
         toUpdate1.cascades = null;
         toUpdate1.arrCascades = new Cascades[]{
            new Cascades()         };
         toUpdate1.arrCascades[0].name = "Level2 Arr New";
         con.set(toUpdate1);
         con.close();
      }
      
      static internal void delete() {
         ObjectContainer con = Db4o.openFile(FILE);
         Cascades cc1 = new Cascades();
         cc1.cascades = new Cascades();
         cc1.cascades.cascades = new Cascades();
         ObjectSet set1 = con.get(cc1);
         Cascades toDelete1 = (Cascades)set1.next();
         con.activate(toDelete1, Int32.MaxValue);
         con.delete(toDelete1);
         con.close();
      }
   }
}