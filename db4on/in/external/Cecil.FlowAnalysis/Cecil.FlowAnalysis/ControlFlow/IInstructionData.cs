namespace Cecil.FlowAnalysis.ControlFlow
{
	public interface IInstructionData
	{
		/// <summary>
		/// Stack height before the execution of the related instruction.
		/// </summary>
		int StackBefore { get; }

		/// <summary>
		/// Stack height after the execution of the related instruction.
		/// </summary>
		int StackAfter { get; }
	}
}