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
using com.db4o;
using com.db4o.types;
namespace com.db4o.ext {

   public class Db4oDatabase : Db4oType {
      
      public Db4oDatabase() : base() {
      }
      public byte[] i_signature;
      public long i_uuid;
      [Transient] private ExtObjectContainer i_objectContainer;
      [Transient] private int i_id;
      
      public static Db4oDatabase generate() {
         Db4oDatabase db4odatabase1 = new Db4oDatabase();
         db4odatabase1.i_signature = Unobfuscated.generateSignature();
         db4odatabase1.i_uuid = j4o.lang.JavaSystem.currentTimeMillis();
         return db4odatabase1;
      }
      
      public override bool Equals(Object obj) {
         if (obj == this) return true;
         if (obj == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject(obj)) return false;
         Db4oDatabase db4odatabase_0_1 = (Db4oDatabase)obj;
         if (db4odatabase_0_1.i_signature == null || i_signature == null) return false;
         if (db4odatabase_0_1.i_signature.Length != i_signature.Length) return false;
         for (int i1 = 0; i1 < i_signature.Length; i1++) {
            if (i_signature[i1] != db4odatabase_0_1.i_signature[i1]) return false;
         }
         return true;
      }
      
      public int getID(ExtObjectContainer extobjectcontainer) {
         if (extobjectcontainer != i_objectContainer) {
            i_objectContainer = extobjectcontainer;
            i_id = (int)extobjectcontainer.getID(this);
            if (i_id == 0) {
               extobjectcontainer.set(this);
               i_id = (int)extobjectcontainer.getID(this);
            }
         }
         return i_id;
      }
      
      public override String ToString() {
         return "Db4oDatabase: " + i_signature;
      }
   }
}