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
            Tester.deleteAllInstances(this);
            Tester.store(new CsDate(DateTime.Now));
        }

		public void testTrivialQuery() 
		{
			Query q = Tester.query();
			q.constrain(typeof(CsDate));
			ObjectSet os = q.execute();
			Tester.ensure(os.size() == 1);
		}

		public void testQueryByExample() 
		{
			CsDate template = new CsDate();
			Tester.getOne(template);

			template.dateTime = new DateTime(0);
			Tester.getOne(template);

			template.dateTime = new DateTime(100);
			ObjectSet os = Tester.objectContainer().get(template);
			Tester.ensure(os.size() == 0);
		}

		public void testDeactivation()
		{
			CsDate template = new CsDate(new DateTime(100));
            Tester.objectContainer().deactivate(template, int.MaxValue);
            Tester.ensure(template.dateTime.Equals(new DateTime(0)));
        }

		public void testSODA() 
		{
			DateTime before = DateTime.Now.AddDays(-1);
			DateTime after = DateTime.Now.AddDays(1);

			Query q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(before).smaller();
			Tester.ensure(0 == q.execute().size());

			q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(after).greater();
			Tester.ensure(0 == q.execute().size());

			q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(before).greater();
			Tester.ensure(1 == q.execute().size());

			q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("dateTime").constrain(after).smaller();
			Tester.ensure(1 == q.execute().size());

			q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("flag").constrain(true);
			q.descend("dateTime").constrain(before).greater();
			q.descend("dateTime").constrain(after).smaller();
			Tester.ensure(1 == q.execute().size());

			q = Tester.query();
			q.constrain(typeof(CsDate));
			q.descend("flag").constrain(false);
			q.descend("dateTime").constrain(before).greater();
			q.descend("dateTime").constrain(after).smaller();
			Tester.ensure(0 == q.execute().size());
		}
	}
}
