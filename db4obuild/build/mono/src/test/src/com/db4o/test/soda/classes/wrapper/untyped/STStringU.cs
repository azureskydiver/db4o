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
namespace com.db4o.test.soda.classes.wrapper.untyped {

    public class STStringU : STClass1 {
        [Transient] public static SodaTest st;
        public Object str;
      
        public STStringU() : base() {
        }
      
        public STStringU(String str) : base() {
            this.str = str;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STStringU(null),
                                   new STStringU("aaa"),
                                   new STStringU("bbb"),
                                   new STStringU("dod")         };
        }
      
        public void testEquals() {
            Query q1 = st.query();
            q1.constrain(store()[2]);
            st.expectOne(q1, store()[2]);
        }
      
        public void testNotEquals() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(store()[2]);
            q1.descend("str").constraints().not();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[3]         });
        }
      
        public void testDescendantEquals() {
            Query q1 = st.query();
            q1.constrain(new STStringU());
            q1.descend("str").constrain("bbb");
            st.expectOne(q1, new STStringU("bbb"));
        }
      
        public void testContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("od"));
            q1.descend("str").constraints().contains();
            st.expectOne(q1, new STStringU("dod"));
        }
      
        public void testNotContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("od"));
            q1.descend("str").constraints().contains().not();
            st.expect(q1, new Object[]{
                                          new STStringU(null),
                                          new STStringU("aaa"),
                                          new STStringU("bbb")         });
        }
      
        public void testLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("do"));
            q1.descend("str").constraints().like();
            st.expectOne(q1, new STStringU("dod"));
            q1 = st.query();
            c1 = q1.constrain(new STStringU("od"));
            q1.descend("str").constraints().like();
            st.expectOne(q1, new STStringU("dod"));
        }
      
        public void testNotLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("aaa"));
            q1.descend("str").constraints().like().not();
            st.expect(q1, new Object[]{
                                          new STStringU(null),
                                          new STStringU("bbb"),
                                          new STStringU("dod")         });
            q1 = st.query();
            c1 = q1.constrain(new STStringU("xxx"));
            q1.descend("str").constraints().like();
            st.expectNone(q1);
        }
      
        public void testIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("aaa"));
            ObjectSet set1 = q1.execute();
            STStringU identityConstraint1 = (STStringU)set1.next();
            identityConstraint1.str = "hihs";
            q1 = st.query();
            q1.constrain(identityConstraint1).identity();
            identityConstraint1.str = "aaa";
            st.expectOne(q1, new STStringU("aaa"));
        }
      
        public void testNotIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU("aaa"));
            ObjectSet set1 = q1.execute();
            STStringU identityConstraint1 = (STStringU)set1.next();
            identityConstraint1.str = null;
            q1 = st.query();
            q1.constrain(identityConstraint1).identity().not();
            identityConstraint1.str = "aaa";
            st.expect(q1, new Object[]{
                                          new STStringU(null),
                                          new STStringU("bbb"),
                                          new STStringU("dod")         });
        }
      
        public void testNull() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU(null));
            q1.descend("str").constrain(null);
            st.expectOne(q1, new STStringU(null));
        }
      
        public void testNotNull() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STStringU(null));
            q1.descend("str").constrain(null).not();
            st.expect(q1, new Object[]{
                                          new STStringU("aaa"),
                                          new STStringU("bbb"),
                                          new STStringU("dod")         });
        }
      
        public void testConstraints() {
            Query q1 = st.query();
            q1.constrain(new STStringU("aaa"));
            q1.constrain(new STStringU("bbb"));
            Constraints cs1 = q1.constraints();
            Constraint[] csa1 = cs1.toArray();
            if (csa1.Length != 2) {
                st.error("Constraints not returned");
            }
        }

    }
}