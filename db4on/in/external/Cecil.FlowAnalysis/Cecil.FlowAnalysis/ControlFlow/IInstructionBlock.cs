using System;
using System.Collections;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.ControlFlow
{
	public interface IInstructionBlock : IComparable, IEnumerable
	{
		IInstruction FirstInstruction { get; }

		IInstruction LastInstruction { get; }

		IInstructionBlock[] Successors { get; }
	}
}