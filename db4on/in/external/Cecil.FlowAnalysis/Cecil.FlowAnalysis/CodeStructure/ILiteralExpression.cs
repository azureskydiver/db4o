using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface ILiteralExpression : IExpression
	{
		object Value { get; }
	}
}
