using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IExpressionCollection : ICollection
	{
		IExpression this[int index] { get; }
	}
}
