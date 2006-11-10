/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;

namespace Db4objects.Db4odoc.Lists
{
	public class VerboseList: ArrayList
	{
		override public string ToString()
		{
			String output = "";
			foreach (Pilot pilot in this)
			{
				output = output.Equals("") ? pilot.ToString() : output + "," + pilot.ToString();
			}
			return output;
		}
	}
}
