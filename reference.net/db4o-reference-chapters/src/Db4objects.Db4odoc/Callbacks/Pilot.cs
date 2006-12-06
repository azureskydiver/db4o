/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Callbacks
{
	public class Pilot
	{
		string _name;
		
		public Pilot(string name)
		{
			_name = name;
		}
        
		public string Name
		{
			get
			{
				return _name;
			}
		}
        
		override public string ToString()
		{
			return _name;
		}
	}
}
