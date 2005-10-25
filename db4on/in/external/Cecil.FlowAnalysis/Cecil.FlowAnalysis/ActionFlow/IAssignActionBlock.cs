using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IAssignActionBlock : IFallThroughActionBlock
	{
		IAssignExpression AssignExpression { get; }
	}
}