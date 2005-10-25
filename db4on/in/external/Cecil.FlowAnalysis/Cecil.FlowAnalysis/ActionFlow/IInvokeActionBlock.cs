using Cecil.FlowAnalysis.CodeStructure;

namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IInvokeActionBlock : IFallThroughActionBlock
	{
		IMethodInvocationExpression Expression { get; }
	}
}