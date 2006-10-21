/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using com.db4o.query;

namespace com.db4odoc.f1.diagnostics
{
	public class ArbitraryQuery : Predicate
	{
		private int[] _points;
		public ArbitraryQuery(int[] points)
		{
			_points=points;
		}
		public bool Match(Pilot pilot)
		{
			foreach (int points in _points)
			{
				if (pilot.Points == points)
				{
					return true;
				}
			}
			return pilot.Name.StartsWith("Rubens");
		}
	}
}