/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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