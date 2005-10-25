namespace Cecil.FlowAnalysis.ActionFlow
{
	public interface IBranchActionBlock : IActionBlock
	{
		IActionBlock Target { get; }
	}
}