/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.refactoring
{
	public class PilotNew
	{
		private string _identity;
		private int _points;
		
		public PilotNew(string name, int points) 
		{
			_identity = name;
			_points = points;
		}

		public string Identity
		{
			get 
			{
				return _identity;
			}
		}

		override public string ToString() 
		{
			return string.Format("{0}/{1}",_identity,_points);
		}
	}
}
