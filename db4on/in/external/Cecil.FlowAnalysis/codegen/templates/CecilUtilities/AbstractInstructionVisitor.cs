using System;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CecilUtilities
{
	public class AbstractInstructionVisitor : IInstructionVisitor
	{<%
	for instr in Instructions:
%>
		public virtual void On${instr.OpCodes[0]}(IInstruction instruction)
		{
			throw new NotImplementedException(CecilFormatter.FormatInstruction(instruction));
		}
<%
	end
%>
		public void Visit(IInstruction instruction)
		{
			InstructionDispatcher.Dispatch(instruction, this);
		}
	}
}
