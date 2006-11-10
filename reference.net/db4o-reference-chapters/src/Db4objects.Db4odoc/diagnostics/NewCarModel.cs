/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.Diagnostics
{

    public class NewCarModel : Db4objects.Db4o.Query.Predicate 
	{
		public bool Match(Car car) 
		{
			return car.Model.EndsWith("2002");
		}
	}
}