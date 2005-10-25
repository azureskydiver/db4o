using Cecil.FlowAnalysis.ControlFlow;

namespace Cecil.FlowAnalysis.Impl.ControlFlow
{
	internal class InstructionData : IInstructionData
	{
		private int _before;
		private int _after;

		public InstructionData(int before, int after)
		{
			_before = before;
			_after = after;
		}

		#region IInstructionData Members

		public int StackBefore
		{
			get
			{
				return _before;
			}
		}

		public int StackAfter
		{
			get
			{
				return _after;
			}
		}

		#endregion
	}
}