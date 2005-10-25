using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.ControlFlow;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Cecil.FlowAnalysis.Impl.ControlFlow;
using Mono.Cecil;

namespace Cecil.FlowAnalysis
{
	/// <summary>
	/// Creates the specific graphs.
	/// </summary>
	public class FlowGraphFactory
	{
		public static IControlFlowGraph CreateControlFlowGraph(IMethodDefinition method)
		{
			if (null == method) throw new ArgumentNullException("method");
			return new FlowGraphBuilder(method).ControlFlowGraph;
		}

		public static IActionFlowGraph CreateActionFlowGraph(IControlFlowGraph cfg)
		{
			if (null == cfg) throw new ArgumentNullException("cfg");
			return new ActionGraphBuilder(cfg).ActionFlowGraph;
		}

		private FlowGraphFactory()
		{
			
		}
	}
}
