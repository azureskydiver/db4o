using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IMethodInvocationExpression : IExpression
	{
		IExpression Target { get; }
		IExpressionCollection Arguments { get; }
	}
}
