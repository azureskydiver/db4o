/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.Diagnostics
{
	public class Empty
	{
		public Empty()
		{
		}

		public string CurrentTime()
		{
			DateTime dt = DateTime.Now;
			String time = dt.ToString("d");
			return time;
		}

		override public string ToString()
		{
			return CurrentTime();
		}
	}
}