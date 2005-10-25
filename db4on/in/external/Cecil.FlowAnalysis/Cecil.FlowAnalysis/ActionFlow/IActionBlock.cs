using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IActionBlock
	{	
		ActionType ActionType { get; }

		IActionBlock[] Successors { get; }

		IActionBlockCollection Predecessors { get; }

		IInstruction SourceInstruction { get; }
	}
}