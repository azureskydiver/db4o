/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4o.Tools;

using j4o.lang;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test {

    public class QueryDeleted {
        public String name;
      
        public QueryDeleted() : base() {
        }
      
        public QueryDeleted(String name) : base() {
            this.name = name;
        }
      
        public void store() {
            Tester.deleteAllInstances(this);
            Tester.store(new QueryDeleted("one"));
            Tester.store(new QueryDeleted("two"));
        }
      
        public void test(){
            Query q = Tester.query();
            q.constrain(typeof(QueryDeleted));
            q.descend("name").constrain("one");
            QueryDeleted qd = (QueryDeleted)q.execute().next();
            Tester.delete(qd);
            checkCount(1);
            Tester.rollBack();
            checkCount(2);
            Tester.delete(qd);
            checkCount(1);
            Tester.commit();
            checkCount(1);
        }
	
        private void checkCount(int count){
            Query q = Tester.query();
            q.constrain(typeof(QueryDeleted));
            ObjectSet res = q.execute();
            Tester.ensure(res.size() == count);
        }
    }
}