using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.ControlFlow
{
	public interface IControlFlowGraph
	{	
		/// <summary>
		/// The method body represented by this control flow graph.
		/// </summary>
		IMethodBody MethodBody { get; }

		/// <summary>
		/// The blocks.
		/// </summary>
		IInstructionBlock[] Blocks { get; }

		/// <summary>
		/// Retrieves data about a specific instruction.
		/// </summary>
		/// <param name="instruction"></param>
		/// <returns></returns>
		IInstructionData GetData(IInstruction instruction);
	}
}