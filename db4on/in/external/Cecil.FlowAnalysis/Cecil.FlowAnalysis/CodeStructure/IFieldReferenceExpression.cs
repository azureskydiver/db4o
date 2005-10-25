using System;
using System.Collections;
using Mono.Cecil;
using Mono.Cecil.Cil;

namespace Cecil.FlowAnalysis.CodeStructure
{
	public interface IFieldReferenceExpression : IExpression
	{
		IExpression Target { get; }
		IFieldReference Field { get; }
	}
}
