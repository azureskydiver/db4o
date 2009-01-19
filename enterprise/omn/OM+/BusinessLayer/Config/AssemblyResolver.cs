/* Copyright (C) 2009 db4objects Inc.   http://www.db4o.com */
using System.IO;
using System.Reflection;

namespace OManager.BusinessLayer.Config
{
	public class AssemblyResolver
	{
		public static Assembly Resolve(ISearchPath searchPath, string name)
		{
			foreach (string path in searchPath.Paths)
			{
				string assemblyPath = Path.Combine(path, name);
				Assembly assembly = TryLoadAssembly(assemblyPath);
				if (null != assembly)
				{
					return assembly;
				}
			}

			return null;
		}

		private static Assembly TryLoadAssembly(string path)
		{
			Assembly assembly = TryLoadAssembly(path, "exe");
			return assembly == null ? TryLoadAssembly(path, "dll") : assembly;
		}

		private static Assembly TryLoadAssembly(string path, string extension)
		{
			string assemblyPath = path + "." + extension;
			return File.Exists(assemblyPath) ? Assembly.LoadFrom(assemblyPath) : null;
		}
	}
}
