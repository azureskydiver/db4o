/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using com.db4odoc.f1.evaluations;

namespace com.db4odoc.f1.lists
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
