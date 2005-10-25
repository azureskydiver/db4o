using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CecilUtilities
{
	public interface IInstructionVisitor
	{
<%
	for instr in Instructions:
%>		void On${instr.OpCodes[0]}(IInstruction instruction);
<%
	end
%>	}
}
