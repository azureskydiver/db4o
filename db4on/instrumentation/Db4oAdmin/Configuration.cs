using System;

namespace Db4oAdmin
{
	public class Configuration
	{
		private bool _caseSensitive;
		private string _assemblyLocation;
		
		public Configuration(string assemblyLocation)
		{
			_assemblyLocation = assemblyLocation;
		}

		public bool CaseSensitive
		{
			get { return _caseSensitive; }
			set { _caseSensitive = value; }
		}

		public string AssemblyLocation
		{
			get { return _assemblyLocation; }
		}
	}
}
