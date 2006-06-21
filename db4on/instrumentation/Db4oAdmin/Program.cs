/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */
using System;

namespace Db4oAdmin
{
	public class Program
	{
		static int Main(string[] args)
		{
			if (args.Length < 1)
			{
				Console.WriteLine("Usage:\nDb4oAdmin <assembly location>");
				return -1;
			}
			try
			{
				string assemblyLocation = args[0];
				Configuration configuration = new Configuration();
				new CFNQEnabler(assemblyLocation, configuration).Run();
			}
			catch (Exception x)
			{
				Console.WriteLine(x);
				return -2;
			}
			return 0;
		}
	}
}