using Cecil.FlowAnalysis.ActionFlow;
using Cecil.FlowAnalysis.CodeStructure;
using Cecil.FlowAnalysis.Impl.ActionFlow;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.Impl.ActionFlow
{
	internal class AssignActionBlock : AbstractFallThroughActionBlock, IAssignActionBlock
	{
		private IAssignExpression _assignExpression;

		public AssignActionBlock(IInstruction sourceInstruction, IAssignExpression assign)
			: base(sourceInstruction)
		{
			_assignExpression = assign;
		}

		public override ActionType ActionType
		{
			get
			{
				return ActionType.Assign;
			}
		}

		public IAssignExpression AssignExpression
		{
			get
			{
				return _assignExpression;
			}
		}
	}
}