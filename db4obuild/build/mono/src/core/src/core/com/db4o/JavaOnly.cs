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

   internal class JavaOnly {
      
      internal JavaOnly() : base() {
      }
      static internal Class[] SIMPLE_CLASSES = null;
      
      static internal int collectionUpdateDepth(Class var_class) {
         return 0;
      }
      
      static internal bool isCollection(Class var_class) {
         return false;
      }
      
      static internal bool isCollectionTranslator(Config4Class config4class) {
         return false;
      }
      
      public static JDK jdk() {
         return new JDK();
      }
      
      public static void link() {
      }
      
      public static void runFinalizersOnExit() {
      }
   }
}