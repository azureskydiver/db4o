using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IMethodReferenceExpression : IExpression
	{
		IExpression Target { get; }
		IMethodReference Method { get; }
	}
}
