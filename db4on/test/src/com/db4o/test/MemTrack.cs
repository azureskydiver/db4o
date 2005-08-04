/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Text;
using j4o.lang;
namespace com.db4o.test {

   public class MemTrack {

       static String bigString;
       static int counter;
      
      public MemTrack() : base() {
      }

       public void configure(){
           if(bigString == null){
               StringBuilder sb = new StringBuilder();
               for(int i = 0; i < 10000; i ++){
                   sb.Append(i);
               }
               bigString = sb.ToString();
           }
       }

      public void test() {
         Tester.deleteAllInstances(new Atom());
         Tester.store(new Atom(bigString + counter++));
      }
   }
}