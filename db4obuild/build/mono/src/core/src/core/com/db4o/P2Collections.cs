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
using com.db4o.ext;
using com.db4o.types;

namespace com.db4o {

   internal class P2Collections : Db4oCollections {

      internal ExtObjectContainer i_stream;
      
      internal P2Collections(Object a_stream) : base() {
         i_stream = (ExtObjectContainer)a_stream;
      }
      
      public Db4oList newLinkedList() {
          lock(i_stream.Lock()){
              if (Unobfuscated.createDb4oList(i_stream)){
                  Db4oList l = new P2LinkedList();
                  i_stream.set(l);
                  return l;
              }
              return null;
          }
      }
      
      public Db4oMap newHashMap(int size) {
          lock(i_stream.Lock()){
              if (Unobfuscated.createDb4oList(i_stream)) return new P2HashMap(size);
              return null;
          }
      }

       public Db4oMap newIdentityHashMap(int size) {
           lock(i_stream.Lock()){
               if(Unobfuscated.createDb4oList(i_stream)){
                   P2HashMap m = new P2HashMap(size);
                   m.i_type = 1;
                   i_stream.set(m);
                   return m;
               }
               return null;
           }
       }
   }
}