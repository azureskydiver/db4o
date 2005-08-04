/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.Reflection;
using com.db4o.query;
using j4o.lang;
using j4o.lang.reflect;

namespace com.db4o.test.cs
{
	public enum CsEnumState
	{
		None,
		Open,
		Running,
		Closed
	}

	/// <summary>
	/// enums
	/// </summary>
	public class CsEnum
	{
		CsEnumState _state;

		public CsEnum()
		{
		}

		public CsEnum(CsEnumState state)
		{
			_state = state;
		}

		public CsEnumState State
		{
			get
			{
				return _state;
			}

			set
			{
				_state = value;
			}
		}

		public void store()
		{
			Tester.deleteAllInstances(this);
			Tester.store(new CsEnum(CsEnumState.Open));
			Tester.store(new CsEnum(CsEnumState.Closed));
			Tester.store(new CsEnum(CsEnumState.Running));
		}

		public void testValueConstrain()
		{
			Query q = Tester.query();
			q.constrain(typeof(CsEnum));
			ObjectSet os = q.execute();
			Tester.ensure(os.size() == 3);

			tstQueryByEnum(CsEnumState.Open);
			tstQueryByEnum(CsEnumState.Closed);
		}

		public void testOrConstrain()
		{
			Query q = Tester.query();
			q.constrain(typeof(CsEnum));
			q.descend("_state").constrain(CsEnumState.Open).or(
				q.descend("_state").constrain(CsEnumState.Running));
			
			ensureObjectSet(q.execute(), CsEnumState.Open, CsEnumState.Running);
		}

		public void testQBE()
		{
			tstQBE(3, CsEnumState.None); // None is the zero/uninitialized value
			tstQBE(1, CsEnumState.Closed);
			tstQBE(1, CsEnumState.Open);
			tstQBE(1, CsEnumState.Running);
		}

		private void tstQBE(int expectedCount, CsEnumState value)
		{
			ObjectSet os = Tester.objectContainer().get(new CsEnum(value));
			Tester.ensureEquals(expectedCount, os.size());
		}

		private void ensureObjectSet(ObjectSet os, params CsEnumState[] expected)
		{
			Tester.ensureEquals(expected.Length, os.size());
			ArrayList l = new ArrayList();
			while (os.hasNext())
			{
				l.Add(((CsEnum)os.next()).State);
			}
			
			foreach (CsEnumState e in expected)
			{	
				Tester.ensure(l.Contains(e));
				l.Remove(e);
			}
		}

		void tstQueryByEnum(CsEnumState template)
		{
			Query q = Tester.query();
			q.constrain(typeof(CsEnum));
			q.descend("_state").constrain(template);

			ObjectSet os = q.execute();
			Tester.ensure(1 == os.size());
			Tester.ensure(template == ((CsEnum)os.next()).State);
		}
	}
}
