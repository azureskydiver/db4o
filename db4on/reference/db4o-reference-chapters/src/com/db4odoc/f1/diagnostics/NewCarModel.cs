/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using com.db4o.query;
using com.db4odoc.f1.evaluations;

namespace com.db4odoc.f1.diagnostics
{

	public class NewCarModel:Predicate 
	{
		public bool Match(Car car) 
		{
			return car.Model.EndsWith("2002");
		}
	}
}