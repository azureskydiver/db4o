/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;

namespace com.db4odoc.f1.lists
{
	public class CollectionFactory
	{
		public static IList newList()
		{
			return  new VerboseList();
		}
	}
}
