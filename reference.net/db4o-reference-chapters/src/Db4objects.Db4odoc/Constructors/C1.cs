/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Ñonstructors
{
	class C1 
	{
		private string s;

		private C1(string s) 
		{
			this.s=s;
		}

		override public string ToString() 
		{
			return s;
		}
	}
}
