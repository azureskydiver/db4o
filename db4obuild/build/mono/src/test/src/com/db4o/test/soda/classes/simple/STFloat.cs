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

   public class STFloat : STClass1 {
      [Transient] public static SodaTest st;
      public float i_float;
      
      public STFloat() : base() {
      }
      
      internal STFloat(float a_float) : base() {
         i_float = a_float;
      }
      
      public Object[] store() {
         return new Object[]{
            new STFloat(Single.MinValue),
new STFloat((float)1.23E-5),
new STFloat((float)1.345),
new STFloat(Single.MaxValue)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(store()[0]);
         st.expectOne(q1, store()[0]);
      }
      
      public void testGreater() {
         Query q1 = st.query();
         q1.constrain(new STFloat((float)0.1));
         q1.descend("i_float").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         q1.constrain(new STFloat((float)1.5));
         q1.descend("i_float").constraints().smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}