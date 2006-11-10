/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Ñonstructors
{
	class C4 
	{
		private String s;
		[Transient] private int i;

		private C4(String s) 
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
