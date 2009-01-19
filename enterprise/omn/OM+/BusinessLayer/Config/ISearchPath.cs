/* Copyright (C) 2009 db4objects Inc.   http://www.db4o.com */
using System.Collections.Generic;

namespace OManager.BusinessLayer.Config
{
	public interface ISearchPath
	{
		bool Add(string path);
		
		void Remove(string path);
		
		IEnumerable<string> Paths {  get; }
	}
}
