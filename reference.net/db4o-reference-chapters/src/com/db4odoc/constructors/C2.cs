/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Ñonstructors
{
	class C2 
	{
		[Transient] private String x;
		private String s;

		private C2(String s) 
		{
			this.s=s;
			this.x="x";
		}

		override public String ToString() 
		{
			return s+x.Length;
		}
	}
}
