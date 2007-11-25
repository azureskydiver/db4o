/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Ñonstructors
{
	class C2 
	{
		[Transient] private string _x;
		private string _s;

		private C2(string s) 
		{
			_s=s;
			_x="x";
		}

		override public string ToString() 
		{
			return _s+_x.Length;
		}
	}
}
