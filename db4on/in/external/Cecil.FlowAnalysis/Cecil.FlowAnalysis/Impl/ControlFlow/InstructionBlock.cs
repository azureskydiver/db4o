using System;
using System.Collections;
using Cecil.FlowAnalysis.ControlFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ControlFlow
{
	internal class InstructionBlock : IInstructionBlock
	{
		public static readonly IInstructionBlock[] NoSuccessors = new IInstructionBlock[0];

		private IInstruction _firstInstruction;
		private IInstruction _lastInstruction;
		private IInstructionBlock[] _successors = NoSuccessors;

		internal InstructionBlock(IInstruction first)
		{
			if (null == first) throw new ArgumentNullException("first");
			_firstInstruction = first;
		}

		internal void SetLastInstruction(IInstruction last)
		{
			if (null == last) throw new ArgumentNullException("last");
			_lastInstruction = last;
		}

		internal void SetSuccessors(IInstructionBlock[] successors)
		{
			_successors = successors;
		}

		public IInstruction FirstInstruction
		{
			get
			{
				return _firstInstruction;
			}
		}

		public IInstruction LastInstruction
		{
			get
			{
				return _lastInstruction;
			}
		}

		public IInstructionBlock[] Successors
		{
			get
			{
				return _successors;
			}
		}

		public int CompareTo(object obj)
		{
			return _firstInstruction.Offset.CompareTo(((InstructionBlock)obj).FirstInstruction.Offset);
		}

		public IEnumerator GetEnumerator()
		{
			ArrayList instructions = new ArrayList();
			IInstruction instruction = _firstInstruction;
			while (true)
			{
				instructions.Add(instruction);
				if (instruction == _lastInstruction) break;
				instruction = instruction.Next;
			}
			return instructions.GetEnumerator();
		}
	}
}