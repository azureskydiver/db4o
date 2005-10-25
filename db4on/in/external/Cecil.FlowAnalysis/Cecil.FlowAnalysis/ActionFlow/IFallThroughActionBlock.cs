namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IFallThroughActionBlock : IActionBlock
	{
		IActionBlock Next { get; }
	}
}