using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal class BranchActionBlock : AbstractActionBlock, IBranchActionBlock
	{
		IActionBlock _target;

		public BranchActionBlock(IInstruction sourceInstruction) : base(sourceInstruction)
		{	
		}

		public void SetTarget(IActionBlock target)
		{
			if (null == target) throw new ArgumentNullException("target");
			_target = target;
			AddAsPredecessorOf(target);
		}

		public IActionBlock Target
		{
			get
			{
				return _target;
			}
		}

		override public ActionType ActionType
		{
			get
			{
				return ActionType.Branch;
			}
		}

		public override IActionBlock[] Successors
		{
			get
			{
				return new IActionBlock[] { _target };
			}
		}

		public override void ReplaceSuccessor(IActionBlock existing, IActionBlock newBlock)
		{
			if (_target != existing) throw new ArgumentException("existing");
			_target = newBlock;
		}
	}
}