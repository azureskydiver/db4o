/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Diagnostics
{

	public class CarEvaluation:IEvaluation 
	{
		public void Evaluate(ICandidate candidate)
		{
			Car car=(Car)candidate.GetObject();
			candidate.Include(car.Model.EndsWith("2002"));
		}
	}
}