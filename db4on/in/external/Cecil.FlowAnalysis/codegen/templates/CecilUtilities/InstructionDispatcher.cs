using System;
using System.Collections;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CecilUtilities
{
	public class InstructionDispatcher
	{
		public static void Dispatch(IInstruction instruction, IInstructionVisitor visitor)
		{
			InstructionVisitorDelegate handler = (InstructionVisitorDelegate)_handlers[instruction.OpCode.Value];
			if (null == handler) throw new ArgumentException(CecilFormatter.FormatInstruction(instruction), "instruction");
			handler(visitor, instruction);
		}

		delegate void InstructionVisitorDelegate(IInstructionVisitor visitor, IInstruction instruction);

		static IDictionary _handlers = new Hashtable();

		static InstructionDispatcher()
		{
<%
	for instr in Instructions:
		opcodes = join("OpCodes.${code}" for code in instr.OpCodes, ", ")
%>			Bind(new InstructionVisitorDelegate(Dispatch${instr.OpCodes[0]}), ${opcodes});
<%
	end
%>		}

		static void Bind(InstructionVisitorDelegate handler, params OpCode[] opcodes)
		{
			foreach (OpCode op in opcodes)
			{
				_handlers.Add(op.Value, handler);
			}
		}
<%		
	for instr in Instructions:
%>
		static void Dispatch${instr.OpCodes[0]}(IInstructionVisitor visitor, IInstruction instruction)
		{
			visitor.On${instr.OpCodes[0]}(instruction);
		}
<%
	end
%>	}
}

