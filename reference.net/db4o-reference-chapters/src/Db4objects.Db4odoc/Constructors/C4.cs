/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Ñonstructors
{
	class C4 
	{
		private string _s;
		[Transient] private int _i;

		private C4(string s) 
		{
			_s=s;
			_i=s.Length;
		}

		override public string ToString() 
		{
			return _s+_i;
		}
	}
}
