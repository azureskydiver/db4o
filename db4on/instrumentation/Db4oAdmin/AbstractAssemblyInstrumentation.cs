namespace Db4oAdmin
{
	using Mono.Cecil;
	
	public abstract class AbstractAssemblyInstrumentation : IAssemblyInstrumentation
	{
		protected InstrumentationContext _context;

		public void Run(InstrumentationContext context)
		{
			_context = context;
			try
			{
				ProcessAssembly();
			}
			finally
			{
				_context = null;
			}
		}

		private void ProcessAssembly()
		{
			foreach (ModuleDefinition module in _context.Assembly.Modules)
			{
				ProcessModule(module);
			}
		}

		protected virtual void ProcessModule(ModuleDefinition module)
		{
			foreach (TypeDefinition typedef in module.Types)
			{
				ProcessType(typedef);
			}
		}

		protected virtual void ProcessType(TypeDefinition type)
		{
		}
	}
}