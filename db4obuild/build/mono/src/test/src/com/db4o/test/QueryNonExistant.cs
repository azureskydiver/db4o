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
      
      public void test() {
         ObjectContainer con1 = Test.objectContainer();
         con1.get(new QueryNonExistant(true));
         Test.ensureOccurrences(new QueryNonExistant(), 0);
         Query q1 = con1.query();
         q1.constrain(new QueryNonExistant(true));
         Test.ensure(q1.execute().size() == 0);
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