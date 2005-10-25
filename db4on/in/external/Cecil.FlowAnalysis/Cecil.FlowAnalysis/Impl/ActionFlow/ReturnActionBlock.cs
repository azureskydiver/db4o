using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.CodeStructure;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal class ReturnActionBlock : AbstractActionBlock, IReturnActionBlock
	{
		private IExpression _expression;

		public ReturnActionBlock(IInstruction sourceInstruction, IExpression expression)
			: base(sourceInstruction)
		{	
			_expression = expression;
		}

		override public ActionType ActionType
		{
			get
			{
				return ActionType.Return;
			}
		}

		public IExpression Expression
		{
			get
			{
				return _expression;
			}
		}

		public override IActionBlock[] Successors
		{
			get
			{
				return new IActionBlock[0];
			}
		}

		public override void ReplaceSuccessor(IActionBlock existing, IActionBlock newBlock)
		{
			throw new InvalidOperationException();
		}

	}
}