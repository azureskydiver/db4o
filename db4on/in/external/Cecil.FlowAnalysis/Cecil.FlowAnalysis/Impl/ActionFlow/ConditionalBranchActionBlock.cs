using System;
using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.CodeStructure;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	/// <summary>
	/// </summary>
	internal class ConditionalBranchActionBlock : AbstractActionBlock, IConditionalBranchActionBlock
	{
		IExpression _condition;
		private IActionBlock _then;
		private IActionBlock _else;

		public ConditionalBranchActionBlock(IInstruction sourceInstruction, IExpression condition)
			: base(sourceInstruction)
		{
			if (null == condition) throw new ArgumentNullException("condition");
			_condition = condition;
		}

		public void SetTargets(IActionBlock then, IActionBlock else_)
		{
			if (null == then) throw new ArgumentNullException("then");
			AddAsPredecessorOf(then);
			if (null != else_) AddAsPredecessorOf(else_);
			_then = then;
			_else = else_;
		}

		override public ActionType ActionType
		{
			get { return ActionType.ConditionalBranch; }
		}

		public IExpression Condition
		{
			get { return _condition; }
		}

		public IActionBlock Then
		{
			get { return _then; }
		}

		public IActionBlock Else
		{
			get { return _else; }
		}

		public override IActionBlock[] Successors
		{
			get
			{
				return _else != null
					? new IActionBlock[] { _then, _else }
					: new IActionBlock[] { _then };
			}
		}

		public override void ReplaceSuccessor(IActionBlock existing, IActionBlock newBlock)
		{
			if (existing == _then)
			{
				_then = newBlock;
			}
			else if (existing == _else)
			{
				_else = newBlock;
			}
			else
			{
				throw new ArgumentException("existing");
			}
		}
	}
}
