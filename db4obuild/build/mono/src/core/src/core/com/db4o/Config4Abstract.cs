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
namespace com.db4o {

   abstract internal class Config4Abstract {
      
      internal Config4Abstract() : base() {
      }
      internal int i_cascadeOnActivate = 0;
      internal int i_cascadeOnDelete = 0;
      internal int i_cascadeOnUpdate = 0;
      internal String i_name;
      
      public void cascadeOnActivate(bool xbool) {
         i_cascadeOnActivate = xbool ? 1 : -1;
      }
      
      public void cascadeOnDelete(bool xbool) {
         i_cascadeOnDelete = xbool ? 1 : -1;
      }
      
      public void cascadeOnUpdate(bool xbool) {
         i_cascadeOnUpdate = xbool ? 1 : -1;
      }
      
      abstract internal String className();
      
      public override bool Equals(Object obj) {
         return i_name.Equals(((Config4Abstract)obj).i_name);
      }
      
      public String getName() {
         return i_name;
      }
   }
}