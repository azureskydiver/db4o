/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;

namespace Db4objects.Db4odoc.Lists
{
	public class CollectionFactory
	{
		public static IList newList()
		{
			return  new VerboseList();
		}
	}
}
