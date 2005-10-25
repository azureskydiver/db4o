using System.Collections;
using Cecil.FlowAnalysis.ControlFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ControlFlow
{
	internal class FlowGraph : IControlFlowGraph
	{
		private InstructionBlock[] _blocks;
		private IMethodBody _body;
		private IDictionary _data;

		public FlowGraph(IMethodBody body, InstructionBlock[] blocks, IDictionary instructionData)
		{	
			_body = body;
			_blocks = blocks;
			_data = instructionData;
		}
		
		public IInstructionData GetData(IInstruction instruction)
		{	
			return (IInstructionData) _data[instruction.Offset];
		}

		public IMethodBody MethodBody
		{
			get
			{
				return _body;
			}
		}

		public IInstructionBlock[] Blocks
		{
			get
			{
				return _blocks;
			}
		}
	}
}