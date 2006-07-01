using System;
using System.Collections.Generic;

namespace Db4oAdmin
{
	public class InstrumentationPipeline
	{
		InstrumentationContext _context;
		List<IAssemblyInstrumentation> _instrumentations = new List<IAssemblyInstrumentation>();
		
		public InstrumentationPipeline(Configuration configuration)
		{
			_context = new InstrumentationContext(configuration);
		}

		public void Add(IAssemblyInstrumentation instrumentation)
		{
			if (null == instrumentation) throw new ArgumentNullException("instrumentation");
			_instrumentations.Add(instrumentation);
		}

		public void Run()
		{
			RunInstrumentations();
			SaveAssembly();
		}

		private void RunInstrumentations()
		{
			foreach (IAssemblyInstrumentation instrumentation in _instrumentations)
			{
				instrumentation.Run(_context);
			}
		}

		private void SaveAssembly()
		{
			_context.SaveAssembly();
		}
	}
}