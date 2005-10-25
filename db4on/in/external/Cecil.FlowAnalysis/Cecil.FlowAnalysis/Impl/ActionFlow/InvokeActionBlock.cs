using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.CodeStructure;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal class InvokeActionBlock : AbstractFallThroughActionBlock, IInvokeActionBlock
	{
		private IMethodInvocationExpression _expression;

		public InvokeActionBlock(IInstruction sourceInstruction, IMethodInvocationExpression expression)
			: base(sourceInstruction)
		{	
			_expression = expression;
		}

		override public ActionType ActionType
		{
			get
			{
				return ActionType.Invoke;
			}
		}

		public IMethodInvocationExpression Expression
		{
			get
			{
				return _expression;
			}
		}
	}
}