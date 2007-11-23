/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o;

namespace Db4oUnit.Extensions
{
	public interface IContainerBlock
	{
		/// <exception cref="Exception"></exception>
		void Run(IObjectContainer client);
	}
}
