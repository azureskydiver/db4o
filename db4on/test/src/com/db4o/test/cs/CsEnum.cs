/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
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
			Test.deleteAllInstances(this);
			Test.store(new CsEnum(CsEnumState.Open));
			Test.store(new CsEnum(CsEnumState.Closed));
		}

		public void test()
		{
			Query q = Test.query();
			q.constrain(typeof(CsEnum));
			ObjectSet os = q.execute();
			Test.ensure(os.size() == 2);

			tstQueryByEnum(CsEnumState.Open);
			tstQueryByEnum(CsEnumState.Closed);
		}

		void tstQueryByEnum(CsEnumState template)
		{
			Query q = Test.query();
			q.constrain(typeof(CsEnum));
			q.descend("_state").constrain(template);

			ObjectSet os = q.execute();
			Test.ensure(1 == os.size());
			Test.ensure(template == ((CsEnum)os.next()).State);
		}
	}
}
