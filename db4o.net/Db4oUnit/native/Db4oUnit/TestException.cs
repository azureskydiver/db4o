﻿/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
namespace Db4oUnit
{
	public class TestException : System.Exception
	{
		public TestException(System.Exception reason) : base(reason.Message, reason)
		{
		}

		public System.Exception GetReason()
		{
			return this.InnerException;
		}
		
		override public string ToString()
		{
			if (null != this.InnerException) return this.InnerException.ToString();
			return base.ToString();
		}
	}
}
