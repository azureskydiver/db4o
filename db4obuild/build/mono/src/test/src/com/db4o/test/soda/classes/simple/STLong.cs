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
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

   public class STLong : STClass1 {
      [Transient] public static SodaTest st;
      public long i_long;
      
      public STLong() : base() {
      }
      
      internal STLong(long a_long) : base() {
         i_long = a_long;
      }
      
      public Object[] store() {
         return new Object[]{
            new STLong(Int64.MinValue),
new STLong(-1),
new STLong(0),
new STLong(Int64.MaxValue - 1)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STLong(Int64.MinValue));
         st.expect(q1, new Object[]{
            new STLong(Int64.MinValue)         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         q1.constrain(new STLong(-1));
         q1.descend("i_long").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         q1.constrain(new STLong(1));
         q1.descend("i_long").constraints().smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void testBetween() {
         Query q1 = st.query();
         q1.constrain(new STLong());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(-3)).greater();
         sub1.constrain(System.Convert.ToInt64(3)).smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testAnd() {
         Query q1 = st.query();
         q1.constrain(new STLong());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(-3)).greater().and(sub1.constrain(System.Convert.ToInt64(3)).smaller());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testOr() {
         Query q1 = st.query();
         q1.constrain(new STLong());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(3)).greater().or(sub1.constrain(System.Convert.ToInt64(-3)).smaller());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
   }
}