using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IReturnActionBlock : IActionBlock
	{
		IExpression Expression { get; }
	}
}