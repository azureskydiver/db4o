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

		bool flag = true;

		public CsDate()
		{
		}

		public CsDate(DateTime value)
		{
			dateTime = value;
		}

        public void store()
		{
            Test.deleteAllInstances(this);
            Test.store(new CsDate(DateTime.Now));
        }

		public void testTrivialQuery() 
		{
			Query q = Test.query();
			q.constrain(typeof(CsDate));
			ObjectSet os = q.execute();
			Test.ensure(os.size() == 1);
		}

		public void testQueryByExample() 
		{
			CsDate template = new CsDate();
			Test.getOne(template);

			template.dateTime = new DateTime(0);
			Test.getOne(template);

			template.dateTime = new DateTime(100);
			ObjectSet os = Test.objectContainer().get(template);
			Test.ensure(os.size() == 0);
		}

		public void testDeactivation()
		{
			CsDate template = new CsDate(new DateTime(100));
            Test.objectContainer().deactivate(template, int.MaxValue);
            Test.ensure(template.dateTime.Equals(new DateTime(0)));
        }

		public void testSODA() 
		{
			DateTime before = DateTime.Now.AddDays(-1);
			DateTime after = DateTime.Now.AddDays(1);

			Query q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(before).smaller();
			Test.ensure(0 == q.execute().size());

			q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(after).greater();
			Test.ensure(0 == q.execute().size());

			q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(before).greater();
			Test.ensure(1 == q.execute().size());

			q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(after).smaller();
			Test.ensure(1 == q.execute().size());

			q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("flag").constrain(true);
			q.descend("dateTime").constrain(before).greater();
			q.descend("dateTime").constrain(after).smaller();
			Test.ensure(1 == q.execute().size());

			q = Test.query();
			q.constrain(typeof(CsDate));
			q.descend("flag").constrain(false);
			q.descend("dateTime").constrain(before).greater();
			q.descend("dateTime").constrain(after).smaller();
			Test.ensure(0 == q.execute().size());
		}
	}
}
