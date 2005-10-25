using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IStatementCollection : ICollection
	{
		IStatement this[int index] { get; }
	}
}
