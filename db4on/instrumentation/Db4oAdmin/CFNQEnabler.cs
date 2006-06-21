using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Db4oAdmin
{
	public class CFNQEnabler : AbstractNQInstrumentation
	{
		private QueryInvocationProcessor _processor;

		public CFNQEnabler(string location, Configuration configuration) : base(location, configuration)
		{
		}

		override protected void ProcessQueryInvocation(MethodDefinition parent, Instruction queryInvocation)
		{
			if (null == _processor) _processor = new QueryInvocationProcessor(_context);
			_processor.Process(parent, queryInvocation);
		}
	}
}
