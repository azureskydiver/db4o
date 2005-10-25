using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal abstract class AbstractFallThroughActionBlock : AbstractActionBlock, IFallThroughActionBlock
	{
		protected IActionBlock _next;

		public AbstractFallThroughActionBlock(IInstruction sourceInstruction) : base(sourceInstruction)
		{	
		}

		public void SetNext(IActionBlock next)
		{
			if (null == next) return;
			_next = next;
			AddAsPredecessorOf(_next);
		}

		public IActionBlock Next
		{
			get
			{
				return _next;
			}
		}

		public override IActionBlock[] Successors
		{
			get
			{
				return new IActionBlock[] { _next };
			}
		}

		public override void ReplaceSuccessor(IActionBlock existing, IActionBlock newBlock)
		{
			if (_next != existing) throw new ArgumentException("existing");
			_next = newBlock;
		}
	}
}