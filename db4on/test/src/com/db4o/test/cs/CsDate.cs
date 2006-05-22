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

        public void Store()
		{
            Tester.DeleteAllInstances(this);
            Tester.Store(new CsDate(DateTime.Now));
        }

		public void TestTrivialQuery() 
		{
			Query q = Tester.Query();
			q.Constrain(typeof(CsDate));
			ObjectSet os = q.Execute();
			Tester.Ensure(os.Size() == 1);
		}

		public void TestQueryByExample() 
		{
			CsDate template = new CsDate();
			Tester.GetOne(template);

			template.dateTime = new DateTime(0);
			Tester.GetOne(template);

			template.dateTime = new DateTime(100);
			ObjectSet os = Tester.ObjectContainer().Get(template);
			Tester.Ensure(os.Size() == 0);
		}

		public void TestDeactivation()
		{
			CsDate template = new CsDate(new DateTime(100));
            Tester.ObjectContainer().Deactivate(template, int.MaxValue);
            Tester.Ensure(template.dateTime.Equals(new DateTime(0)));
        }

		public void TestSODA() 
		{
			DateTime before = DateTime.Now.AddDays(-1);
			DateTime after = DateTime.Now.AddDays(1);

			Query q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("dateTime").Constrain(before).Smaller();
			Tester.Ensure(0 == q.Execute().Size());

			q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("dateTime").Constrain(after).Greater();
			Tester.Ensure(0 == q.Execute().Size());

			q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("dateTime").Constrain(before).Greater();
			Tester.Ensure(1 == q.Execute().Size());

			q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("dateTime").Constrain(after).Smaller();
			Tester.Ensure(1 == q.Execute().Size());

			q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("flag").Constrain(true);
			q.Descend("dateTime").Constrain(before).Greater();
			q.Descend("dateTime").Constrain(after).Smaller();
			Tester.Ensure(1 == q.Execute().Size());

			q = Tester.Query();
			q.Constrain(typeof(CsDate));
			q.Descend("flag").Constrain(false);
			q.Descend("dateTime").Constrain(before).Greater();
			q.Descend("dateTime").Constrain(after).Smaller();
			Tester.Ensure(0 == q.Execute().Size());
		}
	}
}
