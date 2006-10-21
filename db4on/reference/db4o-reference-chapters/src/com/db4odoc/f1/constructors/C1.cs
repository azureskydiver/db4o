/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.constructors
{
	class C1 
	{
		private String s;

		private C1(String s) 
		{
			this.s=s;
		}

		override public String ToString() 
		{
			return s;
		}
	}
}
