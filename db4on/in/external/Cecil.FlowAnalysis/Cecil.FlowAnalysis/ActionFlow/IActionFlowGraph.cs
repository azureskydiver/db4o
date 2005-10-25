using Cecil.FlowAnalysis.ControlFlow;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IActionFlowGraph
	{
		/// <summary>
		/// The control flow graph upon which this action flow graph
		/// was built.
		/// </summary>
		IControlFlowGraph ControlFlowGraph { get; }

		/// <summary>
		/// Action blocks.
		/// </summary>
		IActionBlockCollection Blocks { get; }

		/// <summary>
		/// Checks if the specified block is the target of
		/// a branch or conditional branch block (only the Then path
		/// is considered).
		/// </summary>
		/// <param name="block">a block</param>
		/// <returns>true if the block is the target of a branch</returns>
		bool IsBranchTarget(IActionBlock block);
	}
}