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
using com.db4o.tools;
namespace com.db4o.test {

    public class QueryDeleted {
        public String name;
      
        public QueryDeleted() : base() {
        }
      
        public QueryDeleted(String name) : base() {
            this.name = name;
        }
      
        public void store() {
            Test.deleteAllInstances(this);
            Test.store(new QueryDeleted("one"));
            Test.store(new QueryDeleted("two"));
        }
      
        public void test(){
            Query q = Test.query();
            q.constrain(typeof(QueryDeleted));
            q.descend("name").constrain("one");
            QueryDeleted qd = (QueryDeleted)q.execute().next();
            Test.delete(qd);
            checkCount(1);
            Test.rollBack();
            checkCount(2);
            Test.delete(qd);
            checkCount(1);
            Test.commit();
            checkCount(1);
        }
	
        private void checkCount(int count){
            Query q = Test.query();
            q.constrain(typeof(QueryDeleted));
            ObjectSet res = q.execute();
            Test.ensure(res.size() == count);
        }
    }
}