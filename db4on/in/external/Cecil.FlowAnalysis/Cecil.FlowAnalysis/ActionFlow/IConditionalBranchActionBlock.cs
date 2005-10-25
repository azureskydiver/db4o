using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IConditionalBranchActionBlock : IActionBlock
	{
		IExpression Condition { get; }
		IActionBlock Then { get; }
		IActionBlock Else { get; }
	}
}