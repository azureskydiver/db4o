/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.StaticFields
{
	public class Pilot
	{
		private string _name;
		private PilotCategories _category;

		public Pilot(string name,PilotCategories category) 
		{
			_name=name;
			_category=category;
		}

		public PilotCategories Category
		{
			get 
			{
				return _category;
			}
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
			return string.Format("{0}/{1}", _name, _category);
		}
	}
}
