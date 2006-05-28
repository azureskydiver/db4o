/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

using Db4oTools;

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
      
        public void Store() {
            Tester.DeleteAllInstances(this);
            Tester.Store(new QueryDeleted("one"));
            Tester.Store(new QueryDeleted("two"));
        }
      
        public void Test(){
            Query q = Tester.Query();
            q.Constrain(typeof(QueryDeleted));
            q.Descend("name").Constrain("one");
            QueryDeleted qd = (QueryDeleted)q.Execute().Next();
            Tester.Delete(qd);
            CheckCount(1);
            Tester.RollBack();
            CheckCount(2);
            Tester.Delete(qd);
            CheckCount(1);
            Tester.Commit();
            CheckCount(1);
        }
	
        private void CheckCount(int count){
            Query q = Tester.Query();
            q.Constrain(typeof(QueryDeleted));
            ObjectSet res = q.Execute();
            Tester.Ensure(res.Size() == count);
        }
    }
}