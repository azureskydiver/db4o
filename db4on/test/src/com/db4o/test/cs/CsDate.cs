/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using com.db4o.query;
using j4o.lang;
using j4o.lang.reflect;

namespace com.db4o.test.cs
{
	/// <summary>
	/// testing deactivation and zero date.
	/// </summary>
	public class CsDate
	{
        DateTime dateTime;

        public void store(){
            Test.deleteAllInstances(this);
            CsDate cd = new CsDate();
            cd.dateTime = DateTime.Now;
            Test.store(cd);
        }

        public void test(){
            Query q = Test.query();
            q.constrain(typeof(CsDate));
            ObjectSet os = q.execute();
            Test.ensure(os.size() == 1);

            CsDate template = new CsDate();
            Test.getOne(template);

            template.dateTime = new DateTime(0);
            Test.getOne(template);

            template.dateTime = new DateTime(100);
            os = Test.objectContainer().get(template);
            Test.ensure(os.size() == 0);

            Test.objectContainer().deactivate(template, int.MaxValue);
            Test.ensure(template.dateTime.Equals(new DateTime(0)));
        }
	}
}
