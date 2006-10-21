/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using com.db4o;

namespace com.db4odoc.f1.constructors
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
