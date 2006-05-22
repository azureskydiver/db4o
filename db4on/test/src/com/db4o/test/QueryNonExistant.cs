/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
namespace com.db4o.test {

   public class QueryNonExistant {
      internal QueryNonExistant1 member;
      
      public QueryNonExistant() : base() {
      }
      
      public QueryNonExistant(bool createMembers) : base() {
         member = new QueryNonExistant1();
         member.member = new QueryNonExistant2();
         member.member.member = this;
      }
      
      public void Test() {
         ObjectContainer con1 = Tester.ObjectContainer();
         con1.Get(new QueryNonExistant(true));
         Tester.EnsureOccurrences(new QueryNonExistant(), 0);
         Query q1 = con1.Query();
         q1.Constrain(new QueryNonExistant(true));
         Tester.Ensure(q1.Execute().Size() == 0);
      }
      
      public class QueryNonExistant1 {
         
         public QueryNonExistant1() : base() {
         }
         internal QueryNonExistant2 member;
      }
      
      public class QueryNonExistant2 : QueryNonExistant1 {
         
         public QueryNonExistant2() : base() {
         }
         internal QueryNonExistant member;
      }
   }
}