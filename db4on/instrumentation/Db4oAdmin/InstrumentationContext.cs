using System;
using System.Reflection;
using Mono.Cecil;

namespace Db4oAdmin
{
	public class InstrumentationContext
	{
		protected AssemblyDefinition _assembly;
		private Configuration _configuration;

		public InstrumentationContext(Configuration configuration)
		{
			_assembly = AssemblyFactory.GetAssembly(configuration.AssemblyLocation);
			_configuration = configuration;
		}
		
		public Configuration Configuration
		{
			get { return _configuration; }
		}
		
		public AssemblyDefinition Assembly
		{
			get { return _assembly;  }
		}
		
		public string AssemblyLocation
		{
			get { return _assembly.MainModule.Image.FileInformation.FullName;  }
		}
		
		public TypeReference Import(Type type)
		{
			return _assembly.MainModule.Import(type);
		}

		public MethodReference Import(MethodBase method)
		{
			return _assembly.MainModule.Import(method);
		}
		
		public void SaveAssembly()
		{
			AssemblyFactory.SaveAssembly(_assembly, AssemblyLocation);
		}
	}
}