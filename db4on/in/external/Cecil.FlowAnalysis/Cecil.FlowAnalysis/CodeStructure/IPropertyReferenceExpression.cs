using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IPropertyReferenceExpression : IExpression
	{
		IExpression Target { get; }
		IPropertyReference Property { get; }
	}
}
