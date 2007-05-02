/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Serializing
{
	public class Pilot
	{
		private string _name;
		
		public Pilot() 
		{
			_name="";
		}

		public Pilot(string name) 
		{
			_name=name;
		}

		public string Name 
		{
			get 
			{
				return _name;
			}
			set
			{
				_name = value;
			}
		}
		override public string ToString() 
		{
			return _name;
		}
	}
}
