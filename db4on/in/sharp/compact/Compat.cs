/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.IO;
using System.Reflection;
using com.db4o.reflect;
using j4o.lang;

#if CF_1_0
// not need for CF_2_0
namespace System.Runtime.CompilerServices
{
	internal class IsVolatile
	{
	}
}

namespace System
{
	class NotImplementedException : Exception
	{
		internal NotImplementedException ()
		{
		}

		internal NotImplementedException (string message) : base (message)
		{
		}
	}
}
#endif

namespace com.db4o
{
	/// <exclude />
	public class Compat
	{
	}
}