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
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STDecimal : STClass1 {
        [Transient] public static SodaTest st;
        
        public decimal i_decimal;
      
        public STDecimal() : base() {
        }
      
        internal STDecimal(Decimal a_decimal) : base() {
            i_decimal = a_decimal;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STDecimal(1000),
                                   new STDecimal(4000),
                                   new STDecimal(5000),
                                   new STDecimal(6000),
                                   new STDecimal(7000) };
        }
      
        public void testEquals() {
            Query q1 = st.query();
            q1.constrain(store()[1]);
            st.expectOne(q1, store()[1]);
        }
      
        public void testGreater() {
            Query q1 = st.query();
            q1.constrain(store()[2]);
            q1.descend("i_decimal").constraints().greater();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testSmaller() {
            Query q1 = st.query();
            q1.constrain(store()[4]);
            q1.descend("i_decimal").constraints().smaller();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }

        public void testGreaterOrEqual(){
            Query q = st.query();
            q.constrain(store()[2]);
            q.descend("i_decimal").constraints().greater().equal();
            Object[] r = store();
            st.expect(q, new Object[] {r[2], r[3], r[4]});
        }

        public void testNotGreaterOrEqual() {
            Query q1 = st.query();
            q1.constrain(store()[3]);
            q1.descend("i_decimal").constraints().not().greater().equal();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2]         });
        }
      
        public void testNull() {
            Query q1 = st.query();
            q1.constrain(new STDecimal());
            q1.descend("i_decimal").constrain(null);
            st.expectNone(q1);
        }
    }
      
 }