using System;
using com.db4o.ext;
using com.db4o.query;

namespace com.db4o.test.nativequeries
{
	public class NamedThing
	{
		string _name;

		public NamedThing(string name)
		{
			_name = name;
		}

		public string Name
		{
			get { return _name; }
		}

		public override string ToString()
		{
			return _name;
		}

	}

	class NameStartsWith : Predicate
	{
		string _s;

		public NameStartsWith(string s)
		{
			_s = s;
		}

		public bool Match(NamedThing thing)
		{
			return thing.Name.StartsWith(_s);
		}
	}

	/// <summary>
	/// </summary>
	public class StringComparisonTestCase : AbstractNativeQueriesTestCase
	{
		private NamedThing _robinson;
		private NamedThing _frisbee;
		private NamedThing _bee;
		private NamedThing _friday;
		private NamedThing _round;

		void setUpData()
		{
			Tester.store(_frisbee = new NamedThing("Frisbee"));
			Tester.store(_bee = new NamedThing("Bee"));
			Tester.store(_friday = new NamedThing("Friday"));
			Tester.store(_robinson = new NamedThing("Robinson Crusoe"));
			Tester.store(_round = new NamedThing("Round Robin"));
		}

		public void testStartsWith()
		{
			setUpData();
			AssertNQResult(new NameStartsWith("Fri"), _frisbee, _friday);
			AssertNQResult(new NameStartsWith("Bee"), _bee);
			AssertNQResult(new NameStartsWith("r"));
			AssertNQResult(new NameStartsWith("R"), _robinson, _round);
		}
	}
}
