/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Queries
{
	public class ComplexQuery : Predicate
	{
		public bool Match(Pilot pilot)
		{
			return pilot.Points > 99
				&& pilot.Points < 199
				|| pilot.Name=="Rubens Barrichello";
		}
	}
}
