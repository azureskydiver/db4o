using System;
using System.Collections;

namespace Cecil.FlowAnalysis.ActionFlow
{
	/// <summary>
	/// </summary>
	public interface IActionBlockCollection : ICollection
	{
		IActionBlock this[int index] { get; }

		int IndexOf(IActionBlock block);

		IActionBlock[] ToArray();
	}
}
