/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Ñonstructors
{
	class C3 
	{
		private string _s;
		private int _i;

		private C3(string s) 
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
