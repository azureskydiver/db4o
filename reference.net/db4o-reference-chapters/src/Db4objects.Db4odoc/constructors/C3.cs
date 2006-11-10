/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.Ñonstructors
{
	class C3 
	{
		private String s;
		private int i;

		private C3(String s) 
		{
			this.s=s;
			this.i=s.Length;
		}

		override public String ToString() 
		{
			return s+i;
		}
	}
}
