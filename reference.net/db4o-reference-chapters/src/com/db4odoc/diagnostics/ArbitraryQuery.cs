/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Diagnostics
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