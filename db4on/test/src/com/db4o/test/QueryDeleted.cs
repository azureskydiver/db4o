/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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