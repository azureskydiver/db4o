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

		public void Store()
		{
			Tester.DeleteAllInstances(this);
			Tester.Store(new CsEnum(CsEnumState.Open));
			Tester.Store(new CsEnum(CsEnumState.Closed));
			Tester.Store(new CsEnum(CsEnumState.Running));
		}

		public void TestValueConstrain()
		{
			Query q = Tester.Query();
			q.Constrain(typeof(CsEnum));
			ObjectSet os = q.Execute();
			Tester.Ensure(os.Size() == 3);

			TstQueryByEnum(CsEnumState.Open);
			TstQueryByEnum(CsEnumState.Closed);
		}

		public void TestOrConstrain()
		{
			Query q = Tester.Query();
			q.Constrain(typeof(CsEnum));
			q.Descend("_state").Constrain(CsEnumState.Open).Or(
				q.Descend("_state").Constrain(CsEnumState.Running));
			
			EnsureObjectSet(q.Execute(), CsEnumState.Open, CsEnumState.Running);
		}

		public void TestQBE()
		{
			TstQBE(3, CsEnumState.None); // None is the zero/uninitialized value
			TstQBE(1, CsEnumState.Closed);
			TstQBE(1, CsEnumState.Open);
			TstQBE(1, CsEnumState.Running);
		}

		private void TstQBE(int expectedCount, CsEnumState value)
		{
			ObjectSet os = Tester.ObjectContainer().Get(new CsEnum(value));
			Tester.EnsureEquals(expectedCount, os.Size());
		}

		private void EnsureObjectSet(ObjectSet os, params CsEnumState[] expected)
		{
			Tester.EnsureEquals(expected.Length, os.Size());
			ArrayList l = new ArrayList();
			while (os.HasNext())
			{
				l.Add(((CsEnum)os.Next()).State);
			}
			
			foreach (CsEnumState e in expected)
			{	
				Tester.Ensure(l.Contains(e));
				l.Remove(e);
			}
		}

		void TstQueryByEnum(CsEnumState template)
		{
			Query q = Tester.Query();
			q.Constrain(typeof(CsEnum));
			q.Descend("_state").Constrain(template);

			ObjectSet os = q.Execute();
			Tester.Ensure(1 == os.Size());
			Tester.Ensure(template == ((CsEnum)os.Next()).State);
		}
	}
}
